package com.giadinh.apporderbill.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderBillOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AppOrderBill API")
                        .description("REST API for menu, orders, billing, kitchen, tables, reporting, printer and system modules.")
                        .version("v1")
                        .contact(new Contact().name("AppOrderBill Team")))
                .addServersItem(new Server().url("/").description("Default server"));
    }
}
