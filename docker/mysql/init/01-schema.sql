CREATE DATABASE IF NOT EXISTS apporderbill;
USE apporderbill;

CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE IF NOT EXISTS menu_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255),
    unit_price BIGINT NOT NULL DEFAULT 0,
    is_active TINYINT NOT NULL DEFAULT 1,
    image_url TEXT,
    stock_tracked TINYINT NOT NULL DEFAULT 0,
    stock_qty INT NOT NULL DEFAULT 0,
    stock_min INT NOT NULL DEFAULT 0,
    stock_max INT NOT NULL DEFAULT 0,
    base_unit VARCHAR(64),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders (
    id VARCHAR(64) PRIMARY KEY,
    order_code VARCHAR(64),
    table_id VARCHAR(255),
    order_date VARCHAR(64),
    status VARCHAR(32) NOT NULL,
    total_amount DOUBLE NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS order_items (
    id VARCHAR(64) PRIMARY KEY,
    order_id VARCHAR(64) NOT NULL,
    menu_item_id VARCHAR(64) NOT NULL,
    menu_item_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL,
    note TEXT,
    status VARCHAR(32),
    is_printed TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tables (
    id VARCHAR(64) PRIMARY KEY,
    table_name VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(32) NOT NULL,
    current_order_id VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(64) NOT NULL,
    total_amount BIGINT NOT NULL,
    final_amount BIGINT NOT NULL,
    paid_amount BIGINT NOT NULL,
    payment_method VARCHAR(32) NOT NULL,
    discount_amount BIGINT,
    discount_percent DOUBLE,
    cashier VARCHAR(128),
    paid_at VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS printer_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    printer_name VARCHAR(255),
    connection_type VARCHAR(64),
    paper_size VARCHAR(32),
    copies INT NOT NULL DEFAULT 1,
    default_kitchen TINYINT NOT NULL DEFAULT 0,
    default_receipt TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS print_templates (
    template_type VARCHAR(64) PRIMARY KEY,
    store_name VARCHAR(255),
    store_address TEXT,
    store_phone VARCHAR(64),
    header TEXT,
    footer TEXT
);
