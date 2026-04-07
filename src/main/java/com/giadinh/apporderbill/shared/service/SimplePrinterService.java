package com.giadinh.apporderbill.shared.service;

import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;

public class SimplePrinterService implements PrinterService {

    public SimplePrinterService(Object orderRepository,
            Object menuItemRepository,
            Object printTemplateRepository,
            Object printerConfigRepository) {
        // Giữ nguyên chữ ký constructor để không phải sửa wiring,
        // nhưng bỏ qua cấu hình phức tạp, dùng default printer của Windows.
    }

    @Override
    public boolean printKitchenTicket(String content) {
        return printText(content, "Kitchen Ticket");
    }

    @Override
    public boolean printReceipt(String content) {
        return printText(content, "Receipt");
    }

    @Override
    public boolean printTest(String content) {
        return printText(content, "Test Print");
    }

    private boolean printText(String content, String jobName) {
        try {
            String text = content == null ? "" : content;
            String[] lines = text.split("\\r?\\n");

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName(jobName);

            Printable printable = new Printable() {
                @Override
                public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                    if (pageIndex > 0) {
                        return NO_SUCH_PAGE;
                    }
                    Graphics2D g2d = (Graphics2D) graphics;
                    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                    g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
                    int y = 0;
                    int lineHeight = g2d.getFontMetrics().getHeight();
                    try {
                        for (String line : lines) {
                            y += lineHeight;
                            g2d.drawString(line, 0, y);
                        }
                        return PAGE_EXISTS;
                    } catch (Exception ex) {
                        return NO_SUCH_PAGE;
                    }
                }
            };

            job.setPrintable(printable);
            // Không mở hộp thoại, in thẳng ra default printer
            job.print();
            System.out.println(jobName + " sent to default printer.");
            return true;
        } catch (Exception e) {
            System.err.println("Print failed (" + jobName + "): " + e.getMessage());
            return false;
        }
    }
}

