package com.giadinh.apporderbill.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.giadinh.apporderbill.shared.util.DataModeConfig;
import com.giadinh.apporderbill.shared.util.SqliteConnectionProvider;

import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.repository.SqliteOrderRepository;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.catalog.repository.SqliteMenuItemRepository;
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
import com.giadinh.apporderbill.shared.service.DefaultPrinterService;

import com.giadinh.apporderbill.orders.OrdersComponent;
import com.giadinh.apporderbill.orders.OrdersComponentImpl;
import com.giadinh.apporderbill.kitchen.KitchenComponent;
import com.giadinh.apporderbill.kitchen.KitchenComponentImpl;
import com.giadinh.apporderbill.billing.BillingComponent;
import com.giadinh.apporderbill.billing.BillingComponentImpl;
import com.giadinh.apporderbill.catalog.CatalogComponent;
import com.giadinh.apporderbill.catalog.CatalogComponentImpl;
import com.giadinh.apporderbill.catalog.repository.CategoryRepository;
import com.giadinh.apporderbill.catalog.repository.SqliteCategoryRepository;
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
        // REST/server-side: no JavaFX preview; default to console printer.
        return new DefaultPrinterService();
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
    public CategoryRepository categoryRepository(SqliteConnectionProvider connectionProvider) {
        return new SqliteCategoryRepository(connectionProvider);
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
