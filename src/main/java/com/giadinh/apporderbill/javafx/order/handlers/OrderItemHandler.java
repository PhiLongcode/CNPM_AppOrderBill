package com.giadinh.apporderbill.javafx.order.handlers;

import com.giadinh.apporderbill.javafx.order.OrderItemViewModel;
import com.giadinh.apporderbill.javafx.order.OrderScreenPresenter;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.util.List;
import java.util.function.Consumer;

public class OrderItemHandler {

    private final TableView<OrderItemViewModel> orderItemsTable;
    private final ObservableList<OrderItemViewModel> itemViewModels = FXCollections.observableArrayList();

    private Consumer<String> errorHandler = m -> {};
    private Consumer<OrderItemViewModel> onRowSelectedCallback = i -> {};
    private OrderScreenPresenter presenter;

    public OrderItemHandler(
            TableView<OrderItemViewModel> orderItemsTable,
            TableColumn<OrderItemViewModel, Boolean> selectColumn,
            TableColumn<OrderItemViewModel, Integer> itemNumberColumn,
            TableColumn<OrderItemViewModel, String> nameColumn,
            TableColumn<OrderItemViewModel, Integer> quantityColumn,
            TableColumn<OrderItemViewModel, String> unitColumn,
            TableColumn<OrderItemViewModel, Long> unitPriceColumn,
            TableColumn<OrderItemViewModel, Long> totalColumn,
            TableColumn<OrderItemViewModel, Double> discountColumn,
            TableColumn<OrderItemViewModel, Double> discountAmountColumn,
            TableColumn<OrderItemViewModel, String> notesColumn,
            TableColumn<OrderItemViewModel, Boolean> printedColumn,
            TableColumn<OrderItemViewModel, Void> actionColumn) {
        this.orderItemsTable = orderItemsTable;
        this.orderItemsTable.setEditable(true);
        this.orderItemsTable.setItems(itemViewModels);
        this.orderItemsTable.setRowFactory(tv -> {
            TableRow<OrderItemViewModel> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldV, newV) -> {
                if (newV != null && newV.isCanceled()) {
                    row.setOpacity(0.55);
                } else {
                    row.setOpacity(1);
                }
            });
            return row;
        });

        wireSelectColumn(selectColumn);
        wireIndexColumn(itemNumberColumn);
        nameColumn.setCellValueFactory(c -> {
            String n = c.getValue().getName();
            return new ReadOnlyStringWrapper(n != null ? n : "");
        });
        
        // Cấu hình các cột editable dùng SimpleProperty
        quantityColumn.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantityColumn.setOnEditCommit(event -> {
            OrderItemViewModel vm = event.getRowValue();
            if (vm != null && presenter != null && !vm.isCanceled()) {
                presenter.updateItemQuantity(vm.getOrderItemId(), event.getNewValue());
            } else {
                orderItemsTable.refresh();
            }
        });

        unitColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getUnitName() != null ? c.getValue().getUnitName() : ""));
        unitPriceColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getUnitPrice()));
        totalColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTotalPrice()));
        
        discountColumn.setCellValueFactory(c -> {
            Double val = c.getValue().getDiscountPercent();
            return new SimpleDoubleProperty(val != null ? val : 0.0).asObject();
        });
        discountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        discountColumn.setOnEditCommit(event -> {
            OrderItemViewModel vm = event.getRowValue();
            if (vm != null && presenter != null && !vm.isCanceled()) {
                presenter.updateItemDiscount(vm.getOrderItemId(), event.getNewValue());
            } else {
                orderItemsTable.refresh();
            }
        });

        discountAmountColumn.setCellValueFactory(c -> {
            Double val = c.getValue().getDiscountAmount();
            return new SimpleDoubleProperty(val != null ? val : 0.0).asObject();
        });
        discountAmountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        discountAmountColumn.setOnEditCommit(event -> {
            OrderItemViewModel vm = event.getRowValue();
            if (vm != null && presenter != null && !vm.isCanceled()) {
               var updateDiscountUseCase = presenter.getUpdateOrderItemDiscountUseCase();
               if(updateDiscountUseCase != null) {
                   try {
                       var input = new com.giadinh.apporderbill.orders.usecase.dto.UpdateOrderItemDiscountInput(
                                      presenter.getCurrentOrderId(), vm.getOrderItemId(), vm.getDiscountPercent(), event.getNewValue());
                       var output = updateDiscountUseCase.execute(input);
                       presenter.refreshOrder();
                   } catch (Exception e) {
                       errorHandler.accept(e.getMessage());
                       orderItemsTable.refresh();
                   }
               }
            } else {
                orderItemsTable.refresh();
            }
        });

        notesColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getNotes() != null ? c.getValue().getNotes() : ""));
        notesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        notesColumn.setOnEditCommit(event -> {
            OrderItemViewModel vm = event.getRowValue();
            if (vm != null && presenter != null && !vm.isCanceled()) {
                presenter.updateItemNote(vm.getOrderItemId(), event.getNewValue());
            } else {
                 orderItemsTable.refresh();
            }
        });
        
        printedColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().isPrintedToKitchen()));

        unitPriceColumn.setCellFactory(col -> new TableCell<OrderItemViewModel, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,d", item));
                }
            }
        });

        totalColumn.setCellFactory(col -> new TableCell<OrderItemViewModel, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,d", item));
                }
            }
        });

        // Commented out overridden discountColumn cell factory since we are using TextFieldTableCell
        /*
        discountColumn.setCellFactory(col -> new TableCell<OrderItemViewModel, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else if (item == null) {
                    setText("—");
                } else {
                    setText(String.format("%,.2f", item));
                }
            }
        });
        */

        printedColumn.setCellFactory(col -> new TableCell<OrderItemViewModel, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(Boolean.TRUE.equals(item) ? "✓" : "");
                }
            }
        });

        wireActionColumn(actionColumn);

        orderItemsTable.getSelectionModel().selectedItemProperty().addListener((obs, o, selected) ->
                onRowSelectedCallback.accept(selected));
    }

    private void wireSelectColumn(TableColumn<OrderItemViewModel, Boolean> selectColumn) {
        selectColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().isSelected()));
        selectColumn.setCellFactory(col -> new TableCell<OrderItemViewModel, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(e -> {
                    TableRow<OrderItemViewModel> row = getTableRow();
                    if (row != null) {
                        OrderItemViewModel vm = row.getItem();
                        if (vm != null) {
                            vm.setSelected(checkBox.isSelected());
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    OrderItemViewModel vm = getTableRow().getItem();
                    checkBox.setSelected(vm.isSelected());
                    setGraphic(checkBox);
                }
            }
        });
    }

    private void wireIndexColumn(TableColumn<OrderItemViewModel, Integer> itemNumberColumn) {
        itemNumberColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(0));
        itemNumberColumn.setCellFactory(col -> new TableCell<OrderItemViewModel, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });
    }

    private void wireActionColumn(TableColumn<OrderItemViewModel, Void> actionColumn) {
        actionColumn.setCellFactory(col -> new TableCell<OrderItemViewModel, Void>() {
            private final Button minus = new Button("−");
            private final Button plus = new Button("+");
            private final HBox box = new HBox(4, minus, plus);

            {
                minus.setOnAction(e -> {
                    OrderItemViewModel vm = getTableRow() != null ? getTableRow().getItem() : null;
                    if (vm == null || presenter == null) {
                        return;
                    }
                    if (vm.isCanceled()) {
                        return;
                    }
                    int q = vm.getQuantity();
                    if (q <= 1) {
                        errorHandler.accept("Số lượng tối thiểu là 1");
                        return;
                    }
                    presenter.updateItemQuantity(vm.getOrderItemId(), q - 1);
                });
                plus.setOnAction(e -> {
                    OrderItemViewModel vm = getTableRow() != null ? getTableRow().getItem() : null;
                    if (vm == null || presenter == null || vm.isCanceled()) {
                        return;
                    }
                    presenter.updateItemQuantity(vm.getOrderItemId(), vm.getQuantity() + 1);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                OrderItemViewModel vm = getTableRow().getItem();
                boolean locked = vm.isCanceled();
                minus.setDisable(locked || vm.getQuantity() <= 1);
                plus.setDisable(locked);
                setGraphic(box);
            }
        });
    }

    public void setErrorHandler(Consumer<String> errorHandler) {
        this.errorHandler = errorHandler != null ? errorHandler : m -> {};
    }

    public void setOnRowSelectedCallback(Consumer<OrderItemViewModel> callback) {
        this.onRowSelectedCallback = callback != null ? callback : i -> {};
    }

    public void setPresenter(OrderScreenPresenter presenter) {
        this.presenter = presenter;
    }

    public List<OrderItemViewModel> getItemViewModels() {
        return itemViewModels;
    }

    public void displayOrderItems(List<OrderItemViewModel> items) {
        Runnable apply = () -> {
            orderItemsTable.getSelectionModel().clearSelection();
            if (items == null) {
                itemViewModels.clear();
            } else {
                itemViewModels.setAll(items);
            }
            orderItemsTable.refresh();
        };
        if (Platform.isFxApplicationThread()) {
            apply.run();
        } else {
            Platform.runLater(apply);
        }
    }
}
