package com.giadinh.apporderbill;

import com.giadinh.apporderbill.shared.util.SqliteConnectionProvider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Class để khởi tạo dữ liệu menu items từ SQL script.
 */
public class MenuDataInitializer {
    private final SqliteConnectionProvider connectionProvider;

    public MenuDataInitializer(SqliteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    /**
     * Khởi tạo dữ liệu menu từ file SQL.
     * Sử dụng PreparedStatement và batch insert để tránh database lock.
     */
    public void initializeMenuData() {
        // Đọc file SQL trước (không cần connection)
        InputStream sqlStream = getClass().getClassLoader()
                .getResourceAsStream("com/giadinh/apporderbill/data/init_menu_items.sql");

        if (sqlStream == null) {
            sqlStream = getClass().getResourceAsStream("/com/giadinh/apporderbill/data/init_menu_items.sql");
            if (sqlStream == null) {
                System.err.println("ERROR: Không tìm thấy file SQL init_menu_items.sql trong resources!");
                return;
            }
        }

        // Parse SQL file
        List<MenuItemData> menuItems = parseMenuItems(sqlStream);
        System.out.println("Đã parse được " + menuItems.size() + " món từ file SQL");

        if (menuItems.isEmpty()) {
            System.out.println("Không có món nào để insert!");
            return;
        }

        // Mở connection và thực thi với transaction
        try (Connection conn = connectionProvider.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu transaction

            String insertSql = "INSERT INTO menu_items (name, category, unit_price, is_active, created_at, updated_at) "
                    +
                    "VALUES (?, ?, ?, ?, datetime('now'), datetime('now'))";

            int successCount = 0;
            int skipCount = 0;
            int errorCount = 0;

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (MenuItemData item : menuItems) {
                    try {
                        // Kiểm tra xem món đã tồn tại chưa
                        if (itemExists(conn, item.name)) {
                            skipCount++;
                            continue;
                        }

                        ps.setString(1, item.name);
                        ps.setString(2, item.category);
                        ps.setLong(3, item.unitPrice);
                        ps.setInt(4, item.isActive ? 1 : 0);
                        ps.addBatch();
                        successCount++;
                    } catch (SQLException e) {
                        errorCount++;
                        System.err.println("Lỗi khi chuẩn bị insert món '" + item.name + "': " + e.getMessage());
                    }
                }

                // Execute batch
                try {
                    ps.executeBatch();
                    conn.commit();
                    System.out.println("Đã commit " + successCount + " món vào database");
                } catch (SQLException e) {
                    conn.rollback();
                    System.err.println("Lỗi khi execute batch, đã rollback: " + e.getMessage());
                    // Thử insert từng cái một
                    System.out.println("Thử insert từng món một...");
                    insertOneByOne(conn, menuItems);
                    return;
                }
            }

            System.out.println("========================================");
            System.out.println("Kết quả khởi tạo menu:");
            System.out.println("  - Thành công: " + successCount);
            System.out.println("  - Đã tồn tại (bỏ qua): " + skipCount);
            System.out.println("  - Lỗi: " + errorCount);
            System.out.println("========================================");

            // Verify
            try (Statement stmt = conn.createStatement()) {
                var rs = stmt.executeQuery("SELECT COUNT(*) as count FROM menu_items");
                int totalCount = rs.getInt("count");
                System.out.println("Tổng số món trong database: " + totalCount);
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi khởi tạo dữ liệu menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Insert từng món một nếu batch insert thất bại.
     */
    private void insertOneByOne(Connection conn, List<MenuItemData> menuItems) throws SQLException {
        String insertSql = "INSERT INTO menu_items (name, category, unit_price, is_active, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, datetime('now'), datetime('now'))";

        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;

        for (MenuItemData item : menuItems) {
            try {
                // Kiểm tra xem món đã tồn tại chưa
                if (itemExists(conn, item.name)) {
                    skipCount++;
                    continue;
                }

                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setString(1, item.name);
                    ps.setString(2, item.category);
                    ps.setLong(3, item.unitPrice);
                    ps.setInt(4, item.isActive ? 1 : 0);
                    ps.executeUpdate();
                    successCount++;
                } catch (SQLException e) {
                    if (e.getMessage() != null &&
                            (e.getMessage().contains("UNIQUE") || e.getMessage().contains("duplicate"))) {
                        skipCount++;
                    } else {
                        errorCount++;
                        System.err.println("Lỗi khi insert món '" + item.name + "': " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                errorCount++;
                System.err.println("Lỗi khi xử lý món '" + item.name + "': " + e.getMessage());
            }
        }

        try {
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Lỗi khi commit: " + e.getMessage());
        }

        System.out.println("Kết quả insert từng món:");
        System.out.println("  - Thành công: " + successCount);
        System.out.println("  - Đã tồn tại: " + skipCount);
        System.out.println("  - Lỗi: " + errorCount);
    }

    /**
     * Parse menu items từ SQL file.
     */
    private List<MenuItemData> parseMenuItems(InputStream sqlStream) {
        List<MenuItemData> items = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(sqlStream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Loại bỏ comments và dòng trống
                line = line.trim();
                if (line.startsWith("--") || line.isEmpty()) {
                    continue;
                }

                // Tìm INSERT statements
                if (line.toUpperCase().startsWith("INSERT INTO")) {
                    // Parse VALUES từ dòng này và các dòng tiếp theo
                    parseInsertLine(reader, line, items);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi parse SQL file: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Parse một INSERT statement và extract menu items.
     */
    private void parseInsertLine(BufferedReader reader, String firstLine, List<MenuItemData> items) {
        try {
            // Tìm phần VALUES
            int valuesIndex = firstLine.toUpperCase().indexOf("VALUES");
            if (valuesIndex == -1) {
                return;
            }

            // Lấy phần sau VALUES
            String valuesPart = firstLine.substring(valuesIndex + 6).trim();

            // Đọc các dòng tiếp theo nếu cần
            String line = firstLine;
            while (!line.endsWith(";") && line != null) {
                String nextLine = reader.readLine();
                if (nextLine == null)
                    break;
                nextLine = nextLine.trim();
                if (nextLine.isEmpty() || nextLine.startsWith("--"))
                    continue;
                valuesPart += " " + nextLine;
                line = nextLine;
            }

            // Loại bỏ dấu ; cuối
            if (valuesPart.endsWith(";")) {
                valuesPart = valuesPart.substring(0, valuesPart.length() - 1).trim();
            }

            // Parse các giá trị: (val1, 'name', 'category', price, ...)
            parseValues(valuesPart, items);

        } catch (Exception e) {
            System.err.println("Lỗi khi parse INSERT line: " + e.getMessage());
        }
    }

    /**
     * Parse phần VALUES và extract các menu items.
     */
    private void parseValues(String valuesPart, List<MenuItemData> items) {
        // Tách các giá trị: (val1, val2, ...), (val3, val4, ...)
        List<String> valueRows = new ArrayList<>();
        StringBuilder currentRow = new StringBuilder();
        int parenDepth = 0;

        for (char c : valuesPart.toCharArray()) {
            if (c == '(') {
                parenDepth++;
                if (parenDepth == 1) {
                    currentRow = new StringBuilder();
                }
                currentRow.append(c);
            } else if (c == ')') {
                currentRow.append(c);
                parenDepth--;
                if (parenDepth == 0) {
                    valueRows.add(currentRow.toString());
                    currentRow = new StringBuilder();
                }
            } else {
                if (parenDepth > 0) {
                    currentRow.append(c);
                }
            }
        }

        // Parse từng row
        for (String row : valueRows) {
            MenuItemData item = parseValueRow(row);
            if (item != null) {
                items.add(item);
            }
        }
    }

    /**
     * Parse một row VALUES: ('name', 'category', price, active, ...)
     */
    private MenuItemData parseValueRow(String row) {
        try {
            // Loại bỏ dấu ngoặc
            row = row.trim();
            if (row.startsWith("(") && row.endsWith(")")) {
                row = row.substring(1, row.length() - 1).trim();
            }

            // Tách các giá trị bằng dấu phẩy (cẩn thận với string có dấu phẩy)
            List<String> values = new ArrayList<>();
            StringBuilder currentValue = new StringBuilder();
            boolean inQuotes = false;
            char quoteChar = 0;

            for (int i = 0; i < row.length(); i++) {
                char c = row.charAt(i);

                if ((c == '\'' || c == '"') && (i == 0 || row.charAt(i - 1) != '\\')) {
                    if (!inQuotes) {
                        inQuotes = true;
                        quoteChar = c;
                        currentValue.append(c);
                    } else if (c == quoteChar) {
                        inQuotes = false;
                        currentValue.append(c);
                    } else {
                        currentValue.append(c);
                    }
                } else if (c == ',' && !inQuotes) {
                    values.add(currentValue.toString().trim());
                    currentValue = new StringBuilder();
                } else {
                    currentValue.append(c);
                }
            }

            // Thêm giá trị cuối cùng
            if (currentValue.length() > 0) {
                values.add(currentValue.toString().trim());
            }

            // Parse values: name, category, unit_price, is_active, created_at, updated_at
            if (values.size() >= 4) {
                String name = unquote(values.get(0));
                String category = unquote(values.get(1));
                long unitPrice = Long.parseLong(values.get(2).trim());
                int isActive = values.size() > 3 ? Integer.parseInt(values.get(3).trim()) : 1;

                return new MenuItemData(name, category, unitPrice, isActive == 1);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi parse value row: " + row + " - " + e.getMessage());
        }
        return null;
    }

    /**
     * Loại bỏ dấu ngoặc kép từ string.
     */
    private String unquote(String str) {
        str = str.trim();
        if ((str.startsWith("'") && str.endsWith("'")) ||
                (str.startsWith("\"") && str.endsWith("\""))) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    /**
     * Kiểm tra xem món đã tồn tại chưa.
     */
    private boolean itemExists(Connection conn, String itemName) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM menu_items WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, itemName);
            var rs = ps.executeQuery();
            return rs.getInt("count") > 0;
        }
    }

    /**
     * Khởi tạo dữ liệu menu một cách an toàn (chỉ insert nếu chưa tồn tại).
     */
    public void initializeMenuDataSafe() {
        try (Connection conn = connectionProvider.getConnection();
                Statement stmt = conn.createStatement()) {

            // Kiểm tra xem đã có dữ liệu chưa
            var rs = stmt.executeQuery("SELECT COUNT(*) as count FROM menu_items");
            int count = rs.getInt("count");

            if (count > 0) {
                System.out.println("Đã có " + count + " món trong database. Bỏ qua khởi tạo.");
                System.out.println("Đã load " + count + " món từ database");
                return;
            }

            // Nếu chưa có dữ liệu thì mới khởi tạo
            System.out.println("Database trống, bắt đầu khởi tạo menu items...");
            initializeMenuData();

            // Verify after initialization
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM menu_items");
            count = rs.getInt("count");
            System.out.println("Đã load " + count + " món từ database");
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra dữ liệu menu: " + e.getMessage());
            e.printStackTrace();
            // Vẫn thử khởi tạo nếu có lỗi khi kiểm tra (có thể table chưa tồn tại)
            try {
                System.out.println("Thử khởi tạo menu items...");
                initializeMenuData();
            } catch (Exception ex) {
                System.err.println("Lỗi khi khởi tạo menu sau khi kiểm tra: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Data class để lưu thông tin menu item từ SQL.
     */
    private static class MenuItemData {
        final String name;
        final String category;
        final long unitPrice;
        final boolean isActive;

        MenuItemData(String name, String category, long unitPrice, boolean isActive) {
            this.name = name;
            this.category = category;
            this.unitPrice = unitPrice;
            this.isActive = isActive;
        }
    }
}
