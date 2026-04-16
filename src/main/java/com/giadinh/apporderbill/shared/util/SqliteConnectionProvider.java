package com.giadinh.apporderbill.shared.util;

import com.giadinh.apporderbill.MenuDataInitializer;
import com.giadinh.apporderbill.table.model.Table;
import com.giadinh.apporderbill.table.repository.TableRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteConnectionProvider {
    private final String dbPath;

    public SqliteConnectionProvider(boolean useDemoDatabase) {
        this.dbPath = useDemoDatabase ? "output/pos-demo.db" : "output/pos.db";
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON;");
            s.execute("PRAGMA journal_mode = WAL;");
            s.execute("PRAGMA busy_timeout = 10000;");
            // Core POS schema
            s.execute("""
                    CREATE TABLE IF NOT EXISTS menu_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        category TEXT,
                        unit_price INTEGER NOT NULL DEFAULT 0,
                        is_active INTEGER NOT NULL DEFAULT 1,
                        created_at TEXT,
                        updated_at TEXT
                    )
                    """);
            s.execute("""
                    CREATE TABLE IF NOT EXISTS customers (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        phone TEXT NOT NULL UNIQUE,
                        points INTEGER NOT NULL DEFAULT 0
                    )
                    """);
            s.execute("""
                    CREATE TABLE IF NOT EXISTS tables (
                        id TEXT PRIMARY KEY,
                        table_name TEXT NOT NULL UNIQUE,
                        status TEXT NOT NULL,
                        current_order_id TEXT
                    )
                    """);
            s.execute("""
                    CREATE TABLE IF NOT EXISTS orders (
                        id TEXT PRIMARY KEY,
                        order_code TEXT,
                        table_id TEXT,
                        order_date TEXT,
                        status TEXT NOT NULL,
                        total_amount REAL NOT NULL DEFAULT 0
                    )
                    """);
            try {
                s.execute("ALTER TABLE orders ADD COLUMN order_code TEXT");
            } catch (Exception ignoredColumnExists) {
            }
            s.execute("""
                    CREATE TABLE IF NOT EXISTS order_items (
                        id TEXT PRIMARY KEY,
                        order_id TEXT NOT NULL,
                        menu_item_id TEXT NOT NULL,
                        menu_item_name TEXT NOT NULL,
                        quantity INTEGER NOT NULL,
                        price REAL NOT NULL,
                        note TEXT,
                        status TEXT,
                        is_printed INTEGER NOT NULL DEFAULT 0
                    )
                    """);
            try {
                s.execute("ALTER TABLE order_items ADD COLUMN discount_percent REAL DEFAULT 0");
            } catch (Exception ignoredColumnExists) {
            }
            try {
                s.execute("ALTER TABLE order_items ADD COLUMN discount_amount REAL DEFAULT 0");
            } catch (Exception ignoredColumnExists) {
            }
            s.execute("""
                    CREATE TABLE IF NOT EXISTS payments (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        order_id TEXT NOT NULL,
                        total_amount INTEGER NOT NULL,
                        final_amount INTEGER NOT NULL,
                        paid_amount INTEGER NOT NULL,
                        payment_method TEXT NOT NULL,
                        discount_amount INTEGER,
                        discount_percent REAL,
                        cashier TEXT,
                        paid_at TEXT NOT NULL
                    )
                    """);
            try {
                s.execute("ALTER TABLE payments ADD COLUMN customer_id INTEGER");
            } catch (Exception ignoredColumnExists) {
            }
            try {
                s.execute("ALTER TABLE orders ADD COLUMN customer_id INTEGER");
            } catch (Exception ignoredColumnExists) {
            }
            s.execute("""
                    CREATE TABLE IF NOT EXISTS printer_configs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        printer_name TEXT,
                        connection_type TEXT,
                        paper_size TEXT,
                        copies INTEGER NOT NULL DEFAULT 1,
                        default_kitchen INTEGER NOT NULL DEFAULT 0,
                        default_receipt INTEGER NOT NULL DEFAULT 1
                    )
                    """);
            s.execute("""
                    CREATE TABLE IF NOT EXISTS print_templates (
                        template_type TEXT PRIMARY KEY,
                        store_name TEXT,
                        store_address TEXT,
                        store_phone TEXT,
                        header TEXT,
                        footer TEXT
                    )
                    """);

            // Identity schema (shared same DB)
            s.execute("""
                    CREATE TABLE IF NOT EXISTS modules (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE
                    )
                    """);
            s.execute("""
                    CREATE TABLE IF NOT EXISTS functions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE,
                        moduleId INTEGER
                    )
                    """);
            s.execute("""
                    CREATE TABLE IF NOT EXISTS rolegroups (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE,
                        description TEXT
                    )
                    """);
            s.execute("""
                    CREATE TABLE IF NOT EXISTS permission_assignments (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        roleGroupId INTEGER NOT NULL,
                        functionId INTEGER NOT NULL,
                        canView BOOLEAN NOT NULL,
                        canOperate BOOLEAN NOT NULL,
                        UNIQUE (roleGroupId, functionId)
                    )
                    """);
            s.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL UNIQUE,
                        passwordHash TEXT NOT NULL,
                        roleGroupId INTEGER NOT NULL
                    )
                    """);

            // Settings table for configurable parameters (loyalty config etc.)
            s.execute("""
                    CREATE TABLE IF NOT EXISTS settings (
                        key TEXT PRIMARY KEY,
                        value TEXT NOT NULL
                    )
                    """);
            // Seed default loyalty config if not yet set
            s.execute("""
                    INSERT OR IGNORE INTO settings (key, value) VALUES
                        ('loyalty.earnUnitAmount', '10000'),
                        ('loyalty.pointsPerUnit', '1'),
                        ('loyalty.redeemPointsRequired', '100'),
                        ('loyalty.redeemValue', '5000')
                    """);
            s.execute("""
                    CREATE TABLE IF NOT EXISTS point_transactions (
                        id          INTEGER PRIMARY KEY AUTOINCREMENT,
                        customer_id INTEGER NOT NULL,
                        delta       INTEGER NOT NULL,
                        balance_after INTEGER NOT NULL,
                        type        TEXT NOT NULL,
                        note        TEXT,
                        order_id    TEXT,
                        created_at  TEXT NOT NULL,
                        FOREIGN KEY (customer_id) REFERENCES customers(id)
                    )
                    """);
        } catch (Exception ignored) {
        }
    }

    public Connection getConnection() throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        try (Statement s = c.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON;");
            s.execute("PRAGMA busy_timeout = 10000;");
        }
        return c;
    }

    public void initializeMenuData() {
        new MenuDataInitializer(this).initializeMenuDataSafe();
    }

    public void initializeTables(TableRepository tableRepository) {
        if (tableRepository == null) {
            return;
        }
        if (!tableRepository.findAll().isEmpty()) {
            return;
        }
        for (int i = 1; i <= 20; i++) {
            tableRepository.save(new Table("Bàn " + i));
        }
    }
}

