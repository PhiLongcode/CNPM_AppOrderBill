package com.giadinh.apporderbill.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.giadinh.apporderbill.shared.util.DataModeConfig;
import com.giadinh.apporderbill.shared.util.SqliteConnectionProvider;

import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.repository.SqliteOrderRepository;
import com.giadinh.apporderbill.menu.repository.MenuItemRepository;
import com.giadinh.apporderbill.menu.repository.SqliteMenuItemRepository;
import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.billing.repository.SqlitePaymentRepository;
import com.giadinh.apporderbill.kitchen.repository.KitchenTicketRepository;
import com.giadinh.apporderbill.kitchen.repository.SqliteKitchenTicketRepository;
import com.giadinh.apporderbill.table.repository.TableRepository;
import com.giadinh.apporderbill.table.infrastructure.repository.sqlite.SqliteTableRepository;
import com.giadinh.apporderbill.printer.repository.PrintTemplateRepository;
import com.giadinh.apporderbill.printer.repository.SqlitePrintTemplateRepository;
import com.giadinh.apporderbill.printer.repository.PrinterConfigRepository;
import com.giadinh.apporderbill.printer.repository.SqlitePrinterConfigRepository;

import com.giadinh.apporderbill.shared.service.PrinterService;
import com.giadinh.apporderbill.shared.service.SimplePrinterService;

import com.giadinh.apporderbill.orders.OrdersComponent;
import com.giadinh.apporderbill.orders.OrdersComponentImpl;
import com.giadinh.apporderbill.kitchen.KitchenComponent;
import com.giadinh.apporderbill.kitchen.KitchenComponentImpl;
import com.giadinh.apporderbill.billing.BillingComponent;
import com.giadinh.apporderbill.billing.BillingComponentImpl;
import com.giadinh.apporderbill.menu.MenuComponent;
import com.giadinh.apporderbill.menu.MenuComponentImpl;
import com.giadinh.apporderbill.menu.service.ExcelService;
import com.giadinh.apporderbill.reporting.ReportingComponent;
import com.giadinh.apporderbill.reporting.ReportingComponentImpl;
import com.giadinh.apporderbill.printer.PrinterComponent;
import com.giadinh.apporderbill.printer.PrinterComponentImpl;
import com.giadinh.apporderbill.table.TableComponent;
import com.giadinh.apporderbill.table.TableComponentImpl;
import com.giadinh.apporderbill.system.SystemComponent;
import com.giadinh.apporderbill.system.SystemComponentImpl;

@Configuration
public class OrderApiConfig {

    @Bean
    public SqliteConnectionProvider sqliteConnectionProvider() {
        DataModeConfig dataModeConfig = DataModeConfig.load();
        boolean useDemoDatabase = dataModeConfig.isDemo();
        SqliteConnectionProvider connectionProvider = new SqliteConnectionProvider(useDemoDatabase);

        // Initialize basic data
        connectionProvider.initializeMenuData();
        TableRepository tableRepository = new SqliteTableRepository(connectionProvider);
        connectionProvider.initializeTables(tableRepository);

        return connectionProvider;
    }

    @Bean
    public OrderRepository orderRepository(SqliteConnectionProvider connectionProvider) {
        return new SqliteOrderRepository(connectionProvider);
    }

    @Bean
    public MenuItemRepository menuItemRepository(SqliteConnectionProvider connectionProvider) {
        return new SqliteMenuItemRepository(connectionProvider);
    }

    @Bean
    public PaymentRepository paymentRepository(SqliteConnectionProvider connectionProvider) {
        return new SqlitePaymentRepository(connectionProvider);
    }

    @Bean
    public KitchenTicketRepository kitchenTicketRepository(SqliteConnectionProvider connectionProvider) {
        return new SqliteKitchenTicketRepository(connectionProvider);
    }

    @Bean
    public TableRepository tableRepository(SqliteConnectionProvider connectionProvider) {
        return new SqliteTableRepository(connectionProvider);
    }

    @Bean
    public PrintTemplateRepository printTemplateRepository(SqliteConnectionProvider connectionProvider) {
        return new SqlitePrintTemplateRepository(connectionProvider);
    }

    @Bean
    public PrinterConfigRepository printerConfigRepository(SqliteConnectionProvider connectionProvider) {
        return new SqlitePrinterConfigRepository(connectionProvider);
    }

    @Bean
    public PrinterService printerService(OrderRepository orderRepository,
            MenuItemRepository menuItemRepository,
            PrintTemplateRepository printTemplateRepository,
            PrinterConfigRepository printerConfigRepository) {
        return new SimplePrinterService(orderRepository, menuItemRepository, printTemplateRepository,
                printerConfigRepository);
    }

    // --- Component Implementations ---

    @Bean
    public OrdersComponent ordersComponent(OrderRepository orderRepository,
            MenuItemRepository menuItemRepository,
            PaymentRepository paymentRepository) {
        return new OrdersComponentImpl(orderRepository, menuItemRepository, paymentRepository);
    }

    @Bean
    public KitchenComponent kitchenComponent(OrderRepository orderRepository,
            MenuItemRepository menuItemRepository,
            KitchenTicketRepository kitchenTicketRepository,
            PrinterService printerService) {
        return new KitchenComponentImpl(orderRepository, menuItemRepository, kitchenTicketRepository, printerService);
    }

    @Bean
    public BillingComponent billingComponent(PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            MenuItemRepository menuItemRepository,
            PrinterService printerService) {
        return new BillingComponentImpl(paymentRepository, orderRepository, menuItemRepository, printerService);
    }

    @Bean
    public MenuComponent menuComponent(MenuItemRepository menuItemRepository) {
        return new MenuComponentImpl(menuItemRepository, new ExcelService());
    }

    @Bean
    public ReportingComponent reportingComponent(PaymentRepository paymentRepository) {
        return new ReportingComponentImpl(paymentRepository);
    }

    @Bean
    public PrinterComponent printerComponent(PrinterConfigRepository printerConfigRepository,
            PrintTemplateRepository printTemplateRepository) {
        return new PrinterComponentImpl(printerConfigRepository, printTemplateRepository);
    }

    @Bean
    public TableComponent tableComponent(TableRepository tableRepository,
            OrderRepository orderRepository) {
        return new TableComponentImpl(tableRepository, orderRepository);
    }

    @Bean
    public SystemComponent systemComponent(SqliteConnectionProvider connectionProvider) {
        return new SystemComponentImpl(connectionProvider);
    }
}
