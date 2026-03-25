package com.giadinh.apporderbill.javafx.order.handlers;

import com.giadinh.apporderbill.menu.usecase.GetActiveMenuItemsUseCase;
import com.giadinh.apporderbill.menu.usecase.dto.MenuItemOutput;
import com.giadinh.apporderbill.shared.util.VietnameseTextUtils;
import com.giadinh.apporderbill.javafx.order.OrderScreenPresenter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Handler xử lý menu items: hiển thị, tìm kiếm, lọc theo category.
 */
public class MenuItemHandler {
    private GetActiveMenuItemsUseCase getActiveMenuItemsUseCase;
    private final FlowPane menuItemsContainer;
    private final TextField searchField;
    private final HBox categoryTabsContainer;
    private final ToggleGroup categoryGroup;

    private final ObservableList<MenuItemOutput> allMenuItems = FXCollections.observableArrayList();
    private final ObservableList<MenuItemOutput> filteredMenuItems = FXCollections.observableArrayList();
    private String selectedCategory = "Tất cả";

    private OrderScreenPresenter presenter;
    private Consumer<String> errorHandler;
    private Consumer<MenuItemOutput> onMenuItemClickHandler;

    public MenuItemHandler(FlowPane menuItemsContainer,
            TextField searchField,
            HBox categoryTabsContainer,
            ToggleGroup categoryGroup) {
        this.menuItemsContainer = menuItemsContainer;
        this.searchField = searchField;
        this.categoryTabsContainer = categoryTabsContainer;
        this.categoryGroup = categoryGroup;

        setupSearchField();
    }

    public void setPresenter(OrderScreenPresenter presenter) {
        this.presenter = presenter;
    }

    public void setErrorHandler(Consumer<String> errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void setGetActiveMenuItemsUseCase(GetActiveMenuItemsUseCase getActiveMenuItemsUseCase) {
        this.getActiveMenuItemsUseCase = getActiveMenuItemsUseCase;
    }

    public void setOnMenuItemClickHandler(Consumer<MenuItemOutput> onMenuItemClickHandler) {
        this.onMenuItemClickHandler = onMenuItemClickHandler;
    }

    private void showError(String message) {
        if (errorHandler != null) {
            errorHandler.accept(message);
        }
    }

    public void loadMenuItems() {
        try {
            if (getActiveMenuItemsUseCase == null) {
                System.err.println("GetActiveMenuItemsUseCase chưa được khởi tạo!");
                return;
            }

            List<MenuItemOutput> items = getActiveMenuItemsUseCase.execute();
            System.out.println("Đã load " + items.size() + " món từ database");

            if (items.isEmpty()) {
                showError("Không có món nào trong menu. Vui lòng kiểm tra database.");
                return;
            }

            // Lưu lại category đang được chọn trước khi clear
            String categoryToPreserve = selectedCategory;
            
            allMenuItems.clear();
            allMenuItems.addAll(items);
            filteredMenuItems.clear();
            filteredMenuItems.addAll(items);

            setupCategoryTabs();
            
            // Restore lại category đã chọn và filter
            // Kiểm tra xem category có còn tồn tại không
            if (categoryToPreserve != null && !categoryToPreserve.isEmpty() && !"Tất cả".equals(categoryToPreserve)) {
                Set<String> availableCategories = allMenuItems.stream()
                        .map(MenuItemOutput::getCategory)
                        .collect(Collectors.toSet());
                
                if (availableCategories.contains(categoryToPreserve)) {
                    // Category vẫn tồn tại, giữ nguyên
                    selectedCategory = categoryToPreserve;
                    filterMenuItems();
                } else {
                    // Category không còn tồn tại, fallback về "Tất cả"
                    selectedCategory = "Tất cả";
                    // Update toggle button
                    ToggleButton allBtn = (ToggleButton) categoryGroup.getToggles().get(0);
                    if (allBtn != null) {
                        allBtn.setSelected(true);
                        updateCategoryButtonStyles();
                    }
                    filterMenuItems();
                }
            } else {
                // Mặc định là "Tất cả"
                selectedCategory = "Tất cả";
                filterMenuItems();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi khi tải menu: " + e.getMessage());
        }
    }

    private void setupCategoryTabs() {
        if (categoryTabsContainer == null) {
            System.err.println("categoryTabsContainer chưa được khởi tạo!");
            return;
        }

        Set<String> categories = allMenuItems.stream()
                .map(MenuItemOutput::getCategory)
                .collect(Collectors.toSet());

        System.out.println("Tìm thấy " + categories.size() + " danh mục: " + categories);

        // Lưu lại category đang được chọn trước khi clear
        String categoryToRestore = selectedCategory;
        
        categoryTabsContainer.getChildren().clear();

        ToggleButton allBtn = new ToggleButton("Tất cả");
        allBtn.setToggleGroup(categoryGroup);
        // Chỉ set selected = true nếu đang chọn "Tất cả" hoặc lần đầu tiên
        boolean shouldSelectAll = "Tất cả".equals(categoryToRestore) || categoryToRestore == null;
        allBtn.setSelected(shouldSelectAll);
        if (shouldSelectAll) {
            allBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        } else {
            allBtn.setStyle("-fx-background-color: #e0e0e0;");
        }
        allBtn.setOnAction(e -> {
            selectedCategory = "Tất cả";
            filterMenuItems();
            updateCategoryButtonStyles();
        });
        categoryTabsContainer.getChildren().add(allBtn);

        categories.stream().sorted().forEach(category -> {
            ToggleButton btn = new ToggleButton(category);
            btn.setToggleGroup(categoryGroup);
            // Set selected nếu đây là category đang được chọn
            boolean isSelected = category.equals(categoryToRestore);
            btn.setSelected(isSelected);
            if (isSelected) {
                btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            } else {
                btn.setStyle("-fx-background-color: #e0e0e0;");
            }
            btn.setOnAction(e -> {
                selectedCategory = category;
                filterMenuItems();
                updateCategoryButtonStyles();
            });
            categoryTabsContainer.getChildren().add(btn);
        });
        
        // Đảm bảo selectedCategory được giữ nguyên
        if (categoryToRestore != null && !"Tất cả".equals(categoryToRestore)) {
            selectedCategory = categoryToRestore;
        }
    }

    public void onCategorySelected() {
        ToggleButton selected = (ToggleButton) categoryGroup.getSelectedToggle();
        if (selected != null) {
            selectedCategory = selected.getText();
            filterMenuItems();
        }
    }

    private void updateCategoryButtonStyles() {
        categoryGroup.getToggles().forEach(t -> {
            if (t instanceof ToggleButton) {
                ToggleButton tb = (ToggleButton) t;
                if (tb.isSelected()) {
                    tb.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                } else {
                    tb.setStyle("-fx-background-color: #e0e0e0;");
                }
            }
        });
    }

    private void setupSearchField() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterMenuItems();
        });
    }

    public void filterMenuItems() {
        String searchText = searchField.getText().trim();

        filteredMenuItems.clear();
        filteredMenuItems.addAll(allMenuItems.stream()
                .filter(item -> {
                    boolean matchesCategory = "Tất cả".equals(selectedCategory) ||
                            item.getCategory().equals(selectedCategory);

                    boolean matchesSearch = searchText.isEmpty() ||
                            VietnameseTextUtils.containsIgnoreVietnameseAccents(
                                    item.getName(), searchText);

                    return matchesCategory && matchesSearch;
                })
                .collect(Collectors.toList()));

        displayMenuItems();
    }

    private void displayMenuItems() {
        menuItemsContainer.getChildren().clear();

        filteredMenuItems.forEach(item -> {
            VBox card = createMenuItemCard(item);
            menuItemsContainer.getChildren().add(card);
        });
    }

    private VBox createMenuItemCard(MenuItemOutput item) {
        VBox card = new VBox(5);
        card.setPrefWidth(240);
        card.setPrefHeight(200);

        // Kiểm tra tồn kho thấp
        boolean isLowStock = isLowStock(item);
        boolean isOutOfStock = isOutOfStock(item);

        String borderColor = "#ddd";
        String bgColor = "white";
        if (isOutOfStock) {
            borderColor = "#ff4444";
            bgColor = "#fff0f0";
        } else if (isLowStock) {
            borderColor = "#ff9800";
            bgColor = "#fff8e1";
        }

        card.setStyle(String.format(
                "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 6;",
                bgColor, borderColor));
        card.setOnMouseClicked(e -> onMenuItemClicked(item));

        // Hiển thị hình ảnh nếu có
        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Image image;
                if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                    image = new Image(imageUrl, 200, 100, true, true, true);
                } else {
                    File file = new File(imageUrl);
                    if (file.exists()) {
                        image = new Image(file.toURI().toString(), 200, 100, true, true, true);
                    } else {
                        image = null;
                    }
                }
                if (image != null) {
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(200);
                    imageView.setFitHeight(100);
                    imageView.setPreserveRatio(true);
                    imageView.setStyle("-fx-background-radius: 3;");
                    card.getChildren().add(imageView);
                }
            } catch (Exception ex) {
                // Bỏ qua lỗi khi load hình ảnh
            }
        }

        // Hiển thị tên món + đơn vị nếu khác "unit" mặc định
        String displayName = item.getName();
        String baseUnit = item.getBaseUnit();
        if (baseUnit != null && !baseUnit.isEmpty() && !baseUnit.equalsIgnoreCase("unit")) {
            displayName = item.getName() + " (" + baseUnit + ")";
        }

        Label nameLabel = new Label(displayName);
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(220);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        Label priceLabel = new Label(String.format("%,d VNĐ", item.getUnitPrice()));
        priceLabel.setStyle("-fx-background-color: #ffd700; -fx-padding: 6; -fx-background-radius: 3;");
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        priceLabel.setTextFill(Color.web("#333"));

        card.getChildren().addAll(nameLabel, priceLabel);

        // Hiển thị số lượng tồn kho nếu có quản lý tồn kho
        if (item.isStockTracked()) {
            Long stockQty = item.getStockQty();
            if (stockQty == null)
                stockQty = 0L;

            HBox stockRow = new HBox(8);

            Label stockLabel;
            if (isOutOfStock) {
                stockLabel = new Label("⛔ Hết hàng (Kho: 0)");
                stockLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 14px; -fx-font-weight: bold;");
            } else if (isLowStock) {
                stockLabel = new Label("⚠ Kho: " + stockQty + " (Sắp hết)");
                stockLabel.setStyle("-fx-text-fill: #f57c00; -fx-font-size: 14px; -fx-font-weight: bold;");
            } else {
                stockLabel = new Label("📦 Kho: " + stockQty);
                stockLabel.setStyle("-fx-text-fill: #388e3c; -fx-font-size: 14px;");
            }

            stockRow.getChildren().add(stockLabel);

            // Bất kỳ món nào đang quản lý tồn kho đều cho phép nhập kho nhanh
            javafx.scene.control.Button restockButton = new javafx.scene.control.Button("Nhập kho");
            restockButton.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-size: 11px;");
            restockButton.setOnAction(e -> {
                if (presenter != null) {
                    presenter.openQuickRestockDialog(item);
                } else {
                    showError("Chức năng nhập kho nhanh chưa sẵn sàng.");
                }
                // Ngăn sự kiện click lan lên card (tránh thêm món ngoài ý muốn)
                e.consume();
            });
            stockRow.getChildren().add(restockButton);

            card.getChildren().add(stockRow);
        }

        return card;
    }

    /**
     * Kiểm tra tồn kho thấp (dưới mức tối thiểu)
     */
    private boolean isLowStock(MenuItemOutput item) {
        if (!item.isStockTracked()) {
            return false;
        }
        Long stockQty = item.getStockQty();
        Long stockMin = item.getStockMin();
        if (stockQty == null)
            stockQty = 0L;
        if (stockMin == null || stockMin <= 0)
            return false;
        return stockQty <= stockMin && stockQty > 0;
    }

    /**
     * Kiểm tra hết hàng
     */
    private boolean isOutOfStock(MenuItemOutput item) {
        if (!item.isStockTracked()) {
            return false;
        }
        Long stockQty = item.getStockQty();
        if (stockQty == null)
            return false;
        return stockQty <= 0;
    }

    private void onMenuItemClicked(MenuItemOutput menuItem) {
        if (onMenuItemClickHandler != null) {
            onMenuItemClickHandler.accept(menuItem);
        }
    }
}
