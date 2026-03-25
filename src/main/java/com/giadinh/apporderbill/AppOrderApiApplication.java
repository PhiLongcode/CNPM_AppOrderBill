package com.giadinh.apporderbill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.giadinh.apporderbill.web", "com.giadinh.apporderbill.web.config" })
public class AppOrderApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppOrderApiApplication.class, args);
        System.out.println("=========================================================");
        System.out.println("  AppOrderBill API is running at: http://localhost:8080/api");
        System.out.println("=========================================================");
    }
}
