package com.giadinh.apporderbill;

import com.giadinh.apporderbill.shared.formatter.KitchenTicketFormatter;
import com.giadinh.apporderbill.shared.formatter.ReceiptFormatter;
import com.giadinh.apporderbill.shared.formatter.SimpleKitchenTicketFormatter;
import com.giadinh.apporderbill.shared.formatter.SimpleReceiptFormatter;
import com.giadinh.apporderbill.shared.service.DefaultPrinterService;
import com.giadinh.apporderbill.shared.service.PrinterService;
import com.giadinh.apporderbill.shared.service.UsbThermalPrinterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = { "com.giadinh.apporderbill", "com.giadinh.apporderbill.web", "com.giadinh.apporderbill.web.config" })
public class AppOrderApiApplication {

    @Value("${printer.usb.name:default_printer}")
    private String usbPrinterName;

    public static void main(String[] args) {
        SpringApplication.run(AppOrderApiApplication.class, args);
        System.out.println("=========================================================");
        System.out.println("  AppOrderBill API is running at: http://localhost:8080/api");
        System.out.println("=========================================================");
    }

    @Bean
    public PrinterService printerService() {
        // Bạn có thể thêm logic để chọn giữa UsbThermalPrinterService và DefaultPrinterService
        // dựa trên một cấu hình khác, ví dụ: printer.type=usb hoặc printer.type=default
        UsbThermalPrinterService usbPrinter = new UsbThermalPrinterService();
        usbPrinter.setPrinterName(usbPrinterName);
        return usbPrinter;
    }

    @Bean
    public ReceiptFormatter receiptFormatter() {
        return new SimpleReceiptFormatter();
    }

    @Bean
    public KitchenTicketFormatter kitchenTicketFormatter() {
        return new SimpleKitchenTicketFormatter();
    }
}
