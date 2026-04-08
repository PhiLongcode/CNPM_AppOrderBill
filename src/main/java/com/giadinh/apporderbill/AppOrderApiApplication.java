package com.giadinh.apporderbill;

import com.giadinh.apporderbill.shared.formatter.KitchenTicketFormatter;
import com.giadinh.apporderbill.shared.formatter.ReceiptFormatter;
import com.giadinh.apporderbill.shared.formatter.SimpleKitchenTicketFormatter;
import com.giadinh.apporderbill.shared.formatter.SimpleReceiptFormatter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = { "com.giadinh.apporderbill", "com.giadinh.apporderbill.web", "com.giadinh.apporderbill.web.config", "com.giadinh.apporderbill.identity.config" })
public class AppOrderApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppOrderApiApplication.class, args);
        System.out.println("=========================================================");
        System.out.println("  AppOrderBill API is running at: http://localhost:8080/api");
        System.out.println("=========================================================");
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
