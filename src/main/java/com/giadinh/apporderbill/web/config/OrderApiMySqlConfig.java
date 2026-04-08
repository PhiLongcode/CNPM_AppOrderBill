package com.giadinh.apporderbill.web.config;

import com.giadinh.apporderbill.billing.BillingComponent;
import com.giadinh.apporderbill.billing.BillingComponentImpl;
import com.giadinh.apporderbill.billing.repository.MySqlPaymentRepository;
import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.catalog.CatalogComponent;
import com.giadinh.apporderbill.catalog.CatalogComponentImpl;
import com.giadinh.apporderbill.catalog.repository.*;
import com.giadinh.apporderbill.customer.repository.CustomerRepository;
import com.giadinh.apporderbill.customer.repository.MySqlCustomerRepository;
import com.giadinh.apporderbill.customer.usecase.CustomerUseCases;
import com.giadinh.apporderbill.kitchen.KitchenComponent;
import com.giadinh.apporderbill.kitchen.KitchenComponentImpl;
import com.giadinh.apporderbill.kitchen.repository.KitchenTicketRepository;
import com.giadinh.apporderbill.kitchen.repository.MySqlKitchenTicketRepository;
import com.giadinh.apporderbill.orders.OrdersComponent;
import com.giadinh.apporderbill.orders.OrdersComponentImpl;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.repository.mysql.MySqlOrderRepository;
import com.giadinh.apporderbill.printer.PrinterComponent;
import com.giadinh.apporderbill.printer.PrinterComponentImpl;
import com.giadinh.apporderbill.printer.repository.*;
import com.giadinh.apporderbill.reporting.ReportingComponent;
import com.giadinh.apporderbill.reporting.ReportingComponentImpl;
import com.giadinh.apporderbill.shared.service.DefaultPrinterService;
import com.giadinh.apporderbill.shared.service.PrinterService;
import com.giadinh.apporderbill.shared.util.MySqlConnectionProvider;
import com.giadinh.apporderbill.system.SystemComponent;
import com.giadinh.apporderbill.system.SystemComponentImpl;
import com.giadinh.apporderbill.table.TableComponent;
import com.giadinh.apporderbill.table.TableComponentImpl;
import com.giadinh.apporderbill.table.infrastructure.repository.mysql.MySqlTableRepository;
import com.giadinh.apporderbill.table.repository.TableRepository;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("api-mysql")
public class OrderApiMySqlConfig {

    @Bean
    public DataSource dataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password) {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setUrl(url);
        ds.setUser(username);
        ds.setPassword(password);
        return ds;
    }

    @Bean
    public MySqlConnectionProvider mySqlConnectionProvider(DataSource dataSource) {
        return new MySqlConnectionProvider(dataSource);
    }

    @Bean
    public OrderRepository orderRepository(MySqlConnectionProvider provider) {
        return new MySqlOrderRepository(provider);
    }

    @Bean
    public MenuItemRepository menuItemRepository(MySqlConnectionProvider provider) {
        return new MySqlMenuItemRepository(provider);
    }

    @Bean
    public PaymentRepository paymentRepository(MySqlConnectionProvider provider) {
        return new MySqlPaymentRepository(provider);
    }

    @Bean
    public KitchenTicketRepository kitchenTicketRepository() {
        return new MySqlKitchenTicketRepository();
    }

    @Bean
    public TableRepository tableRepository(MySqlConnectionProvider provider) {
        return new MySqlTableRepository(provider);
    }

    @Bean
    public PrintTemplateRepository printTemplateRepository(MySqlConnectionProvider provider) {
        return new MySqlPrintTemplateRepository(provider);
    }

    @Bean
    public PrinterConfigRepository printerConfigRepository(MySqlConnectionProvider provider) {
        return new MySqlPrinterConfigRepository(provider);
    }

    @Bean
    public CategoryRepository categoryRepository(MySqlConnectionProvider provider) {
        return new MySqlCategoryRepository(provider);
    }

    @Bean
    public PrinterService printerService() {
        return new DefaultPrinterService();
    }

    @Bean
    public OrdersComponent ordersComponent(OrderRepository orderRepository, MenuItemRepository menuItemRepository, PaymentRepository paymentRepository) {
        return new OrdersComponentImpl(orderRepository, menuItemRepository, paymentRepository);
    }

    @Bean
    public KitchenComponent kitchenComponent(OrderRepository orderRepository, MenuItemRepository menuItemRepository, KitchenTicketRepository kitchenTicketRepository, PrinterService printerService) {
        return new KitchenComponentImpl(orderRepository, menuItemRepository, kitchenTicketRepository, printerService);
    }

    @Bean
    public BillingComponent billingComponent(PaymentRepository paymentRepository, OrderRepository orderRepository, MenuItemRepository menuItemRepository, PrinterService printerService) {
        return new BillingComponentImpl(paymentRepository, orderRepository, menuItemRepository, printerService);
    }

    @Bean
    public CatalogComponent menuComponent(MenuItemRepository menuItemRepository, CategoryRepository categoryRepository) {
        return new CatalogComponentImpl(menuItemRepository, categoryRepository);
    }

    @Bean
    public ReportingComponent reportingComponent(PaymentRepository paymentRepository) {
        return new ReportingComponentImpl(paymentRepository);
    }

    @Bean
    public PrinterComponent printerComponent(PrinterConfigRepository printerConfigRepository, PrintTemplateRepository printTemplateRepository) {
        return new PrinterComponentImpl(printerConfigRepository, printTemplateRepository);
    }

    @Bean
    public TableComponent tableComponent(TableRepository tableRepository, OrderRepository orderRepository) {
        return new TableComponentImpl(tableRepository, orderRepository);
    }

    @Bean
    public SystemComponent systemComponent(MySqlConnectionProvider provider) {
        return new SystemComponentImpl(provider);
    }

    @Bean
    public CustomerRepository customerRepository(MySqlConnectionProvider provider) {
        return new MySqlCustomerRepository(provider);
    }

    @Bean
    public CustomerUseCases customerUseCases(CustomerRepository customerRepository) {
        return new CustomerUseCases(customerRepository);
    }
}
