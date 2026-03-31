package com.giadinh.apporderbill.javafx.order.handlers;

import com.giadinh.apporderbill.javafx.order.OrderItemViewModel;
import com.giadinh.apporderbill.javafx.order.OrderScreenPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OrderItemHandler {
    private final List<OrderItemViewModel> itemViewModels = new ArrayList<>();
    public OrderItemHandler(Object... args) {}
    public void setErrorHandler(Consumer<String> errorHandler) {}
    public void setOnRowSelectedCallback(Consumer<OrderItemViewModel> callback) {}
    public void setPresenter(OrderScreenPresenter presenter) {}
    public List<OrderItemViewModel> getItemViewModels() { return itemViewModels; }
    public void displayOrderItems(List<OrderItemViewModel> items) {
        itemViewModels.clear();
        if (items != null) itemViewModels.addAll(items);
    }
}

