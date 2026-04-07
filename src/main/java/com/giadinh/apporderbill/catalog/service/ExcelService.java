package com.giadinh.apporderbill.catalog.service;

import com.giadinh.apporderbill.catalog.model.MenuItemStatus;
import com.giadinh.apporderbill.catalog.model.MenuItem;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ExcelService {
    private static final String SHEET_NAME = "menu_items";

    public List<MenuItem> importMenu(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return List.of();
        }
        List<MenuItem> result = new ArrayList<>();
        try (FileInputStream in = new FileInputStream(filePath); XSSFWorkbook workbook = new XSSFWorkbook(in)) {
            XSSFSheet sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null) {
                sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            }
            if (sheet == null) {
                return List.of();
            }
            boolean hasIdColumn = hasIdColumn(sheet);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                int base = hasIdColumn ? 1 : 0;
                int id = hasIdColumn ? (int) longValue(row, 0, 0L) : 0;
                String name = stringValue(row, base);
                if (name == null || name.isBlank()) {
                    continue;
                }
                String category = defaultIfBlank(stringValue(row, base + 1), "Khac");
                long unitPrice = longValue(row, base + 2, 0L);
                if (unitPrice <= 0) {
                    continue;
                }
                String baseUnit = defaultIfBlank(stringValue(row, base + 3), "phan");
                boolean stockTracked = boolValue(row, base + 4, false);
                long stockQty = longValue(row, base + 5, 0L);
                long stockMin = longValue(row, base + 6, 0L);
                long stockMax = longValue(row, base + 7, Math.max(stockMin, stockQty));
                boolean active = boolValue(row, base + 8, true);
                String imageUrl = defaultIfBlank(stringValue(row, base + 9), null);

                MenuItem item = new MenuItem(
                        Math.max(0, id),
                        name,
                        unitPrice,
                        category,
                        imageUrl,
                        stockTracked,
                        (int) Math.max(0, stockQty),
                        (int) Math.max(0, stockMin),
                        (int) Math.max(stockMin, stockMax),
                        baseUnit,
                        active ? MenuItemStatus.ACTIVE : MenuItemStatus.INACTIVE);
                result.add(item);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot import Excel file: " + e.getMessage(), e);
        }
        return result;
    }

    public void exportMenu(List<MenuItem> items, String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Excel file path is required");
        }
        Path path = Path.of(filePath);
        if (path.getParent() != null) {
            path.getParent().toFile().mkdirs();
        }
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet(SHEET_NAME);
            createHeader(sheet);
            int rowIdx = 1;
            for (MenuItem item : items == null ? List.<MenuItem>of() : items) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getId());
                row.createCell(1).setCellValue(defaultIfBlank(item.getName(), ""));
                row.createCell(2).setCellValue(defaultIfBlank(item.getCategoryName(), ""));
                row.createCell(3).setCellValue(Math.round(item.getPrice()));
                row.createCell(4).setCellValue(defaultIfBlank(item.getUnitOfMeasureName(), "phan"));
                row.createCell(5).setCellValue(item.isStockManaged());
                row.createCell(6).setCellValue(item.getCurrentStockQuantity());
                row.createCell(7).setCellValue(item.getMinStockQuantity());
                row.createCell(8).setCellValue(item.getMaxStockQuantity());
                row.createCell(9).setCellValue(item.getStatus() == MenuItemStatus.ACTIVE);
                row.createCell(10).setCellValue(defaultIfBlank(item.getImageUrl(), ""));
            }
            for (int i = 0; i <= 10; i++) {
                sheet.autoSizeColumn(i);
            }
            try (FileOutputStream out = new FileOutputStream(path.toFile())) {
                workbook.write(out);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot export Excel file: " + e.getMessage(), e);
        }
    }

    private void createHeader(XSSFSheet sheet) {
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("id");
        header.createCell(1).setCellValue("name");
        header.createCell(2).setCellValue("category");
        header.createCell(3).setCellValue("unit_price");
        header.createCell(4).setCellValue("base_unit");
        header.createCell(5).setCellValue("stock_tracked");
        header.createCell(6).setCellValue("stock_qty");
        header.createCell(7).setCellValue("stock_min");
        header.createCell(8).setCellValue("stock_max");
        header.createCell(9).setCellValue("active");
        header.createCell(10).setCellValue("image_url");
    }

    private boolean hasIdColumn(XSSFSheet sheet) {
        Row header = sheet.getRow(0);
        if (header == null) {
            return false;
        }
        String firstCol = stringValue(header, 0);
        return firstCol != null && firstCol.trim().equalsIgnoreCase("id");
    }

    private String stringValue(Row row, int idx) {
        var cell = row.getCell(idx);
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }
        return null;
    }

    private long longValue(Row row, int idx, long defaultValue) {
        String raw = stringValue(row, idx);
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            return Math.round(Double.parseDouble(raw.trim()));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private boolean boolValue(Row row, int idx, boolean defaultValue) {
        String raw = stringValue(row, idx);
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        String normalized = raw.trim().toLowerCase();
        return normalized.equals("true") || normalized.equals("1") || normalized.equals("yes");
    }

    private String defaultIfBlank(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }
}
