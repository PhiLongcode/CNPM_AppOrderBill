package com.giadinh.apporderbill.shared.service;

import com.giadinh.apporderbill.billing.model.Payment;
import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.printer.model.PrintTemplate;
import com.giadinh.apporderbill.printer.model.PrintTemplateType;
import com.giadinh.apporderbill.printer.model.PrinterConfig;
import com.giadinh.apporderbill.printer.repository.PrintTemplateRepository;
import com.giadinh.apporderbill.printer.repository.PrinterConfigRepository;
import com.giadinh.apporderbill.shared.formatter.RichKitchenTicketFormatter;
import com.giadinh.apporderbill.shared.formatter.RichReceiptFormatter;
import com.giadinh.apporderbill.shared.formatter.TestPrintFormatter;
import com.giadinh.apporderbill.shared.error.DomainMessages;
import com.giadinh.apporderbill.shared.util.PrintUtils;
import javafx.application.Platform;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * PrinterService cho POS JavaFX:
 * - tự format từ Order/Payment + PrintTemplate/PrinterConfig
 * - có preview dialog + in thật bằng JavaFX PrinterJob
 */
public class RichPrinterService implements PrinterService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PrintTemplateRepository printTemplateRepository;
    private final PrinterConfigRepository printerConfigRepository;

    private final RichKitchenTicketFormatter kitchenFormatter = new RichKitchenTicketFormatter();
    private final RichReceiptFormatter receiptFormatter = new RichReceiptFormatter();
    private final TestPrintFormatter testPrintFormatter = new TestPrintFormatter();

    public RichPrinterService(OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            PrintTemplateRepository printTemplateRepository,
            PrinterConfigRepository printerConfigRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.printTemplateRepository = printTemplateRepository;
        this.printerConfigRepository = printerConfigRepository;
    }

    @Override
    public boolean printKitchenTicket(String content) {
        return printWithPreview("KitchenTicket", content, "PB");
    }

    @Override
    public boolean printReceipt(String content) {
        return printWithPreview("Receipt", content, "HD");
    }

    @Override
    public boolean printTest(String content) {
        return printWithPreview("TestPrint", content, "TEST");
    }

    @Override
    public boolean printKitchenTicket(Long orderId, boolean isAddOn, boolean isReprint) throws PrinterException {
        if (orderId == null) return false;
        Order order = orderRepository.findById(String.valueOf(orderId)).orElse(null);
        if (order == null) return false;

        PrinterConfig cfg = safeGetConfig();
        int lineWidth = lineWidthFromConfig(cfg, PrintTemplateType.KITCHEN);
        PrintTemplate tpl = printTemplateRepository.getByType(PrintTemplateType.KITCHEN.key());
        String typeLabel = isReprint ? "IN LẠI" : (isAddOn ? "THÊM MÓN" : "PHIẾU MỚI");

        String content = kitchenFormatter.format(order, tpl, lineWidth, typeLabel);
        return printWithPreview("KitchenTicket", content, "PB");
    }

    @Override
    public boolean printReceipt(Long paymentId, String orderId) throws PrinterException {
        if (paymentId == null) return false;
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) return false;

        Order order = orderId == null ? null : orderRepository.findById(orderId).orElse(null);
        PrinterConfig cfg = safeGetConfig();
        int lineWidth = lineWidthFromConfig(cfg, PrintTemplateType.RECEIPT);
        PrintTemplate tpl = printTemplateRepository.getByType(PrintTemplateType.RECEIPT.key());

        String content = receiptFormatter.formatReceipt(payment, order, tpl, lineWidth, "Liên 1");
        return printWithPreview("Receipt", content, "HD");
    }

    @Override
    public boolean printDraftReceipt(Long orderId, long subtotal, Double discountPercent) throws PrinterException {
        if (orderId == null) return false;
        Order order = orderRepository.findById(String.valueOf(orderId)).orElse(null);
        if (order == null) return false;

        long totalAmount = Math.round(order.getTotalAmount());
        long percentDiscount = 0L;
        if (discountPercent != null && discountPercent > 0) {
            percentDiscount = Math.round(totalAmount * discountPercent / 100.0);
        }
        long discountAmount = Math.max(0, Math.max(subtotal, percentDiscount));
        long finalAmount = Math.max(0, totalAmount - discountAmount);

        PrinterConfig cfg = safeGetConfig();
        int lineWidth = lineWidthFromConfig(cfg, PrintTemplateType.DRAFT);
        PrintTemplate tpl = printTemplateRepository.getByType(PrintTemplateType.DRAFT.key());
        if (tpl == null) {
            tpl = printTemplateRepository.getByType(PrintTemplateType.RECEIPT.key());
        }

        String content = receiptFormatter.formatDraftReceipt(order, totalAmount, discountAmount, finalAmount, tpl, lineWidth);
        return printWithPreview("DraftReceipt", content, "PHIEU_TAM");
    }

    @Override
    public boolean testPrint(PrintTemplateType type) throws PrinterException {
        PrinterConfig cfg = safeGetConfig();
        int lineWidth = lineWidthFromConfig(cfg, type == null ? PrintTemplateType.TEST : type);
        PrintTemplate tpl = printTemplateRepository.getByType((type == null ? PrintTemplateType.TEST : type).key());
        if (tpl == null) tpl = printTemplateRepository.getByType(PrintTemplateType.RECEIPT.key());
        String content = testPrintFormatter.format(type, tpl, lineWidth);
        return printWithPreview("TestPrint", content, "TEST");
    }

    private PrinterConfig safeGetConfig() {
        try {
            return printerConfigRepository == null ? null : printerConfigRepository.getCurrent();
        } catch (Exception ignored) {
            return null;
        }
    }

    private int lineWidthFromConfig(PrinterConfig cfg, PrintTemplateType type) {
        // paperSize ví dụ: "80mm" / "58mm"
        int paperMm = 58;
        if (cfg != null && cfg.getPaperSize() != null) {
            String digits = cfg.getPaperSize().replaceAll("[^0-9]", "");
            try {
                if (!digits.isBlank()) paperMm = Integer.parseInt(digits);
            } catch (Exception ignored) {
            }
        }
        return PrintUtils.calculateCharsPerLine(paperMm);
    }

    private boolean printWithPreview(String title, String content, String docPrefix) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Runnable r = () -> {
            try {
                String documentId = docPrefix + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                Boolean ok = showPreviewDialog(title, content, documentId);
                future.complete(ok != null && ok);
            } catch (Exception e) {
                future.complete(false);
            }
        };

        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
        try {
            return future.get();
        } catch (Exception e) {
            return false;
        }
    }

    private Boolean showPreviewDialog(String title, String content, String documentId) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(msg("ui.printer.preview_title", title));
        dialog.setHeaderText(null);

        TextArea area = new TextArea(content == null ? "" : content);
        area.setEditable(false);
        area.setWrapText(false);
        area.setFont(Font.font("Courier New", FontWeight.NORMAL, FontPosture.REGULAR, 11));
        area.setPrefRowCount(28);

        VBox box = new VBox(area);
        box.setSpacing(8);
        dialog.getDialogPane().setContent(box);

        ButtonType printBtn = new ButtonType(msg("ui.printer.action_print"), ButtonBar.ButtonData.OK_DONE);
        ButtonType saveBtn = new ButtonType(msg("ui.printer.action_save_txt"), ButtonBar.ButtonData.APPLY);
        ButtonType cancelBtn = new ButtonType(msg("ui.printer.action_close"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(printBtn, saveBtn, cancelBtn);

        dialog.setResultConverter(bt -> {
            if (bt == printBtn) return "PRINT";
            if (bt == saveBtn) return "SAVE";
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return false;
        if ("SAVE".equals(result.get())) {
            saveToOutput(documentId + ".txt", content == null ? "" : content);
            return true;
        }
        if ("PRINT".equals(result.get())) {
            return printJavaFx(content == null ? "" : content);
        }
        return false;
    }

    private void saveToOutput(String filename, String content) {
        try {
            Path outDir = Path.of("output");
            Files.createDirectories(outDir);
            Files.write(outDir.resolve(filename), content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
    }

    private boolean printJavaFx(String content) {
        try {
            Printer printer = Printer.getDefaultPrinter();
            PrinterJob job = PrinterJob.createPrinterJob(printer);
            if (job == null) return false;

            // Cho phép user đổi printer/options
            boolean ok = job.showPrintDialog(null);
            if (!ok) return false;

            Node node = buildPrintableNode(content);
            boolean printed = job.printPage(node);
            if (printed) {
                return job.endJob();
            }
            job.cancelJob();
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private Node buildPrintableNode(String content) {
        // TextFlow monospace: đủ dùng cho MVP, sẽ nâng cấp phân trang ở bước test/tinh chỉnh.
        Text t = new Text(content);
        t.setFont(Font.font("Courier New", FontWeight.NORMAL, 10));
        TextFlow flow = new TextFlow(t);
        return new VBox(flow);
    }

    private String msg(String key, Object... args) {
        return DomainMessages.formatKey(key, args);
    }
}

