package com.giadinh.apporderbill.shared.service;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class UsbThermalPrinterService implements PrinterService {

    private String printerName; // Tên máy in USB (có thể là tên cổng USB như /dev/usb/lp0)

    // Constructor mặc định cho Spring DI
    public UsbThermalPrinterService() {
    }

    // Setter để Spring inject giá trị từ application.properties
    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    private boolean printContent(String content, String jobName) {
        if (printerName == null || printerName.trim().isEmpty()) {
            System.err.println("Lỗi: Tên máy in USB chưa được cấu hình (printer.usb.name trong application.properties).");
            return false;
        }
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService target = null;
        for (PrintService s : services) {
            if (s.getName() != null && s.getName().equalsIgnoreCase(printerName.trim())) {
                target = s;
                break;
            }
        }
        if (target == null) {
            System.err.println("Không tìm thấy máy in với tên: " + printerName);
            return false;
        }
        try {
            DocPrintJob job = target.createPrintJob();
            InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            Doc doc = new SimpleDoc(is, flavor, null);

            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            aset.add(new Copies(1));
            job.print(doc, aset);
            is.close();
            System.out.println(jobName + " đã được gửi đến máy in: " + printerName);
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi in " + jobName + " đến máy in " + printerName + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean printKitchenTicket(String content) {
        return printContent(content, "Phiếu bếp");
    }

    @Override
    public boolean printReceipt(String content) {
        return printContent(content, "Hóa đơn thanh toán");
    }

    @Override
    public boolean printTest(String content) {
        return printContent(content, "Kiểm tra máy in");
    }
}
