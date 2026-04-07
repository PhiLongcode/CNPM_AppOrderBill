package com.giadinh.apporderbill.javafx.order;

import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.billing.usecase.GetTodayPaymentsUseCase;
import com.giadinh.apporderbill.billing.usecase.PrintReceiptUseCase;
import com.giadinh.apporderbill.billing.usecase.ReprintReceiptUseCase;
import com.giadinh.apporderbill.billing.usecase.dto.PaymentSummaryOutput;
import com.giadinh.apporderbill.billing.usecase.dto.PrintReceiptInput;
import com.giadinh.apporderbill.kitchen.usecase.PrintKitchenTicketUseCase;
import com.giadinh.apporderbill.kitchen.usecase.PrintSelectedItemsUseCase;
import com.giadinh.apporderbill.kitchen.usecase.dto.PrintKitchenTicketInput;
import com.giadinh.apporderbill.kitchen.usecase.dto.PrintKitchenTicketOutput;
import com.giadinh.apporderbill.kitchen.usecase.dto.PrintSelectedItemsInput;
import com.giadinh.apporderbill.orders.usecase.*;
import com.giadinh.apporderbill.orders.usecase.dto.*;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.DomainMessages;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Presenter chứa logic nghiệp vụ cho Order Screen.
 * Không phụ thuộc vào JavaFX hay bất kỳ UI framework nào.
 * Có thể được test dễ dàng mà không cần JavaFX.
 */
public class OrderScreenPresenter {
    private final OrderScreenView view;

    // Order Management Use Cases
    private final OpenOrCreateOrderUseCase openOrCreateOrderUseCase;
    private final AddCustomItemToOrderUseCase addCustomItemToOrderUseCase;
    private final AddMenuItemToOrderUseCase addMenuItemToOrderUseCase;
    private final GetOrderDetailsUseCase getOrderDetailsUseCase;
    private final CalculateOrderTotalUseCase calculateOrderTotalUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    // Kitchen Use Cases
    private final PrintKitchenTicketUseCase printKitchenTicketUseCase;
    private final PrintSelectedItemsUseCase printSelectedItemsUseCase;

    // Billing Use Cases
    private final CheckoutOrderUseCase checkoutOrderUseCase;
    private final PrintReceiptUseCase printReceiptUseCase;
    private GetTodayPaymentsUseCase getTodayPaymentsUseCase;
    private ReprintReceiptUseCase reprintReceiptUseCase;
    
    // Printer Service (for draft receipt)
    private com.giadinh.apporderbill.shared.service.PrinterService printerService;

    // Item Management Use Cases
    private final UpdateOrderItemQuantityUseCase updateOrderItemQuantityUseCase;
    private final RemoveOrderItemUseCase removeOrderItemUseCase;
    private final DeleteOrderItemUseCase deleteOrderItemUseCase;
    private final UpdateOrderItemNoteUseCase updateOrderItemNoteUseCase;
    private final UpdateOrderItemDiscountUseCase updateOrderItemDiscountUseCase;

    // Repository for table status
    private final OrderRepository orderRepository;

    private Long currentOrderId;
    private String currentTableNumber;

    public OrderScreenPresenter(OrderScreenView view,
            OpenOrCreateOrderUseCase openOrCreateOrderUseCase,
            AddCustomItemToOrderUseCase addCustomItemToOrderUseCase,
            AddMenuItemToOrderUseCase addMenuItemToOrderUseCase,
            GetOrderDetailsUseCase getOrderDetailsUseCase,
            CalculateOrderTotalUseCase calculateOrderTotalUseCase,
            CancelOrderUseCase cancelOrderUseCase,
            PrintKitchenTicketUseCase printKitchenTicketUseCase,
            PrintSelectedItemsUseCase printSelectedItemsUseCase,
            CheckoutOrderUseCase checkoutOrderUseCase,
            PrintReceiptUseCase printReceiptUseCase,
            UpdateOrderItemQuantityUseCase updateOrderItemQuantityUseCase,
            RemoveOrderItemUseCase removeOrderItemUseCase,
            DeleteOrderItemUseCase deleteOrderItemUseCase,
            UpdateOrderItemNoteUseCase updateOrderItemNoteUseCase,
            UpdateOrderItemDiscountUseCase updateOrderItemDiscountUseCase,
            OrderRepository orderRepository) {
        this.view = view;
        this.openOrCreateOrderUseCase = openOrCreateOrderUseCase;
        this.addCustomItemToOrderUseCase = addCustomItemToOrderUseCase;
        this.addMenuItemToOrderUseCase = addMenuItemToOrderUseCase;
        this.getOrderDetailsUseCase = getOrderDetailsUseCase;
        this.calculateOrderTotalUseCase = calculateOrderTotalUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.printKitchenTicketUseCase = printKitchenTicketUseCase;
        this.printSelectedItemsUseCase = printSelectedItemsUseCase;
        this.checkoutOrderUseCase = checkoutOrderUseCase;
        this.printReceiptUseCase = printReceiptUseCase;
        this.updateOrderItemQuantityUseCase = updateOrderItemQuantityUseCase;
        this.removeOrderItemUseCase = removeOrderItemUseCase;
        this.deleteOrderItemUseCase = deleteOrderItemUseCase;
        this.updateOrderItemNoteUseCase = updateOrderItemNoteUseCase;
        this.updateOrderItemDiscountUseCase = updateOrderItemDiscountUseCase;
        this.orderRepository = orderRepository;
    }

    /**
     * Mở dialog nhập kho nhanh cho một món (từ màn Order).
     * Chỉ tăng tồn kho (không thay đổi các thông tin khác).
     */
    public void openQuickRestockDialog(com.giadinh.apporderbill.catalog.usecase.dto.MenuItemOutput menuItem) {
        if (menuItem == null || menuItem.getMenuItemId() == null) {
            view.showError(msg("ui.order.restock_item_unknown"));
            return;
        }

        // Ủy quyền cho View hiện dialog và xử lý nhập kho (để không phụ thuộc JavaFX ở Presenter)
        view.showQuickRestockDialog(menuItem);
    }

    /**
     * Xử lý khi người dùng chọn bàn.
     */
    public void onTableSelected(String tableNumber) {
        if (tableNumber == null || tableNumber.isBlank()) {
            view.showError(msg("ui.order.table_number_required"));
            return;
        }

        try {
            // Kiểm tra xem bàn đã có order active chưa
            var existingOrder = orderRepository.findActiveByTable(tableNumber);
            if (existingOrder.isPresent()) {
                Order ord = existingOrder.get();
                currentOrderId = ord.getId();
                if (currentOrderId == null && ord.getOrderId() != null) {
                    try {
                        currentOrderId = Long.parseLong(ord.getOrderId());
                    } catch (NumberFormatException ex) {
                        currentOrderId = null;
                    }
                }
                currentTableNumber = ord.getTableNumber();
                if (currentOrderId == null) {
                    view.showError(msg("ui.order.order_id_unreadable"));
                    return;
                }

                GetOrderDetailsInput input = new GetOrderDetailsInput(currentOrderId);
                var orderDetails = getOrderDetailsUseCase.execute(input);
                updateView(orderDetails);
                return;
            }
            
            // Bàn chưa có order, tạo mới
            OpenOrCreateOrderInput input = new OpenOrCreateOrderInput(tableNumber, "THU_NGAN");
            OpenOrCreateOrderOutput output = openOrCreateOrderUseCase.execute(input);

            currentOrderId = output.getOrderId();
            currentTableNumber = output.getTableNumber();

            updateView(output);
            view.showSuccess(msg("ui.order.opened_for_table", tableNumber));
        } catch (DomainException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError(msg("ui.order.open_failed", e.getMessage()));
        }
    }

    /**
     * Xử lý khi người dùng thêm món phát sinh (custom item).
     */
    public void onAddCustomItem() {
        if (currentOrderId == null) {
            view.showError(msg("ui.order.select_table_first"));
            return;
        }

        String itemName = view.getItemName();
        String quantityText = view.getItemQuantity();
        String priceText = view.getItemPrice();

        // Validation
        if (itemName == null || itemName.isBlank()) {
            view.showError(msg("ui.order.item_name_required"));
            return;
        }
        if (quantityText == null || quantityText.isBlank()) {
            view.showError(msg("ui.order.quantity_required"));
            return;
        }
        if (priceText == null || priceText.isBlank()) {
            view.showError(msg("ui.order.price_required"));
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityText.trim());
            long unitPrice = Long.parseLong(priceText.trim());

            if (quantity <= 0) {
                view.showError(msg("ui.order.quantity_positive"));
                return;
            }
            if (unitPrice <= 0) {
                view.showError(msg("ui.order.price_positive"));
                return;
            }

            AddCustomItemInput input = new AddCustomItemInput(
                    currentOrderId,
                    itemName.trim(),
                    quantity,
                    unitPrice,
                    null);

            AddCustomItemOutput output = addCustomItemToOrderUseCase.execute(input);

            updateView(output);
            view.clearItemInputFields();
        } catch (NumberFormatException e) {
            view.showError(msg("ui.order.quantity_price_invalid"));
        } catch (DomainException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError(msg("ui.order.add_item_failed", e.getMessage()));
        }
    }

    /**
     * Xử lý khi người dùng thêm món từ menu.
     */
    public void onAddMenuItem(Long menuItemId, int quantity) {
        if (currentOrderId == null) {
            view.showError(msg("ui.order.select_table_first"));
            return;
        }

        if (menuItemId == null) {
            view.showError(msg("ui.order.menu_item_id_required"));
            return;
        }

        if (quantity <= 0) {
            view.showError(msg("ui.order.quantity_positive"));
            return;
        }

        try {
            AddMenuItemInput input = new AddMenuItemInput(
                    currentOrderId,
                    menuItemId,
                    quantity,
                    null);

            AddCustomItemOutput output = addMenuItemToOrderUseCase.execute(input);

            updateView(output);
            
            // Refresh menu items để cập nhật tồn kho realtime
            view.refreshMenuItems();
        } catch (DomainException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError(msg("ui.order.add_item_failed", e.getMessage()));
        }
    }

    /**
     * Refresh order details từ database.
     */
    public void refreshOrder() {
        if (currentOrderId == null) {
            return;
        }

        try {
            GetOrderDetailsInput input = new GetOrderDetailsInput(currentOrderId);
            GetOrderDetailsOutput output = getOrderDetailsUseCase.execute(input);

            updateView(output);
        } catch (Exception e) {
            view.showError(msg("ui.order.refresh_failed", e.getMessage()));
        }
    }

    /**
     * Tính tổng tiền với giảm giá.
     */
    public void calculateTotalWithDiscount(long discountAmount, Double discountPercent) {
        if (currentOrderId == null) {
            view.showError(msg("ui.order.select_table_first"));
            return;
        }

        try {
            CalculateOrderTotalInput input = new CalculateOrderTotalInput(
                    currentOrderId,
                    discountAmount,
                    discountPercent);

            CalculateOrderTotalOutput output = calculateOrderTotalUseCase.execute(input);

            view.displayTotalAmount(output.getSubtotal());
            view.displayFinalAmount(output.getFinalAmount());
            view.displayDiscountAmount(output.getDiscountAmount());
        } catch (Exception e) {
            view.showError(msg("ui.order.calculate_total_failed", e.getMessage()));
        }
    }

    /**
     * Hủy order.
     */
    public void cancelOrder(String reason) {
        if (currentOrderId == null) {
            view.showError(msg("ui.order.no_order_to_cancel"));
            return;
        }

        try {
            CancelOrderInput input = new CancelOrderInput(
                    currentOrderId,
                    reason != null ? reason : "Hủy bởi thu ngân",
                    "THU_NGAN");

            cancelOrderUseCase.execute(input);

            currentOrderId = null;
            currentTableNumber = null;

            view.displayTableNumber("");
            view.displayOrderItems(List.of());
            view.displayTotalAmount(0);
            view.displayFinalAmount(0);
            view.displayDiscountAmount(0);

            view.showSuccess(msg("ui.order.cancel_order_success"));
            
            // Refresh menu items để cập nhật tồn kho realtime (hoàn lại tồn kho)
            view.refreshMenuItems();
        } catch (Exception e) {
            view.showError(msg("ui.order.cancel_order_failed", e.getMessage()));
        }
    }

    /**
     * In phiếu bếp.
     */
    public void printKitchenTicket(boolean isAddOn) {
        if (currentOrderId == null) {
            view.showError(msg("ui.order.select_table_and_have_order"));
            return;
        }

        try {
            PrintKitchenTicketInput input = new PrintKitchenTicketInput(currentOrderId, isAddOn);
            PrintKitchenTicketOutput output = printKitchenTicketUseCase.execute(input);

            if (output.isPrinted()) {
                // In thành công - refresh order để cập nhật trạng thái món đã in
                refreshOrder();
            } else {
                // In thất bại hoặc bị hủy
                String errorMsg = output.getPrintError();
                if (errorMsg != null && !errorMsg.isEmpty()) {
                    view.showError(msg("ui.order.print_kitchen_failed", errorMsg));
                } else {
                    view.showError(msg("ui.order.print_kitchen_cancelled_or_failed"));
                }
                // Vẫn refresh order vì món đã được đánh dấu là đã in
                refreshOrder();
            }
        } catch (DomainException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError(msg("ui.order.print_kitchen_exception", e.getMessage()));
        }
    }

    /**
     * Thanh toán order.
     */
    public boolean checkout(long paidAmount, String paymentMethod, long discountAmount, Double discountPercent) {
        if (currentOrderId == null) {
            view.showError(msg("ui.order.select_table_and_have_order"));
            return false;
        }

        try {
            // Tính tổng tiền trước
            CalculateOrderTotalInput calcInput = new CalculateOrderTotalInput(
                    currentOrderId,
                    discountAmount,
                    discountPercent);
            CalculateOrderTotalOutput calcOutput = calculateOrderTotalUseCase.execute(calcInput);

            if (paidAmount < calcOutput.getFinalAmount()) {
                view.showError(msg("error.CHECKOUT_PAID_AMOUNT_INSUFFICIENT", calcOutput.getFinalAmount()));
                return false;
            }

            // LƯU THANH TOÁN TRƯỚC
            CheckoutOrderInput input = new CheckoutOrderInput(
                    currentOrderId,
                    paidAmount,
                    paymentMethod != null ? paymentMethod : "CASH",
                    discountAmount > 0 ? discountAmount : null,
                    discountPercent,
                    "THU_NGAN",
                    view.getCustomerPhone());

            CheckoutOrderOutput output = checkoutOrderUseCase.execute(input);

            // IN HÓA ĐƠN SAU KHI ĐÃ LƯU - với paymentId từ output
            boolean printSuccess = false;
            try {
                PrintReceiptInput receiptInput = new PrintReceiptInput(output.getPaymentId(), output.getOrderId());
                printReceiptUseCase.execute(receiptInput);
                printSuccess = true;
            } catch (DomainException e) {
                System.err.println("In hóa đơn thất bại: " + DomainMessages.format(e));
                view.showError(msg("ui.order.checkout_saved_but_print_failed", DomainMessages.format(e)));
            } catch (Exception e) {
                System.err.println("In hóa đơn thất bại: " + e.getMessage());
                view.showError(msg("ui.order.checkout_saved_but_print_failed", e.getMessage()));
            }

            // Reset order state sau khi thanh toán đã hoàn tất
            Long completedOrderId = currentOrderId;
            currentOrderId = null;
            currentTableNumber = null;

            view.displayTableNumber("");
            view.displayOrderItems(List.of());
            view.displayTotalAmount(0);
            view.displayFinalAmount(0);
            view.displayDiscountAmount(0);
            view.displayChangeAmount(0);

            // Refresh menu items để cập nhật tồn kho realtime sau thanh toán
            view.refreshMenuItems();

            // Log kết quả
            if (printSuccess) {
                System.out.println("✓ Thanh toán và in hóa đơn thành công cho order #" + completedOrderId);
            } else {
                System.out.println("! Thanh toán thành công cho order #" + completedOrderId + " nhưng in bị hủy/lỗi");
            }
            return true;

        } catch (DomainException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError(msg("ui.order.checkout_failed", e.getMessage()));
        }
        return false;
    }

    /**
     * Cập nhật view với dữ liệu từ OpenOrCreateOrderOutput.
     */
    private void updateView(OpenOrCreateOrderOutput output) {
        view.displayTableNumber(output.getTableNumber());
        view.displayOrderItems(convertToViewModels(output.getItems()));
        view.displayTotalAmount(output.getTotalAmount());
        view.displayFinalAmount(output.getTotalAmount());
        view.setAddItemButtonEnabled(true);
    }

    /**
     * Cập nhật view với dữ liệu từ AddCustomItemOutput.
     */
    private void updateView(AddCustomItemOutput output) {
        view.displayTableNumber(output.getTableNumber());
        view.displayOrderItems(convertToViewModels(output.getItems()));
        view.displayTotalAmount(output.getTotalAmount());
        view.displayFinalAmount(output.getTotalAmount());
    }

    /**
     * Cập nhật view với dữ liệu từ GetOrderDetailsOutput.
     */
    private void updateView(GetOrderDetailsOutput output) {
        view.displayTableNumber(output.getTableNumber());
        view.displayOrderItems(convertToViewModels(output.getItems()));
        view.displayTotalAmount(output.getTotalAmount());
        view.displayFinalAmount(output.getTotalAmount());
    }

    /**
     * Chuyển đổi OrderItemOutput sang OrderItemViewModel.
     */
    private List<OrderItemViewModel> convertToViewModels(List<OrderItemOutput> items) {
        return items.stream()
                .map(item -> new OrderItemViewModel(
                        item.getOrderItemId(),
                        item.getItemName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal(),
                        item.getNotes(),
                        item.getUnitName(),
                        item.isPrintedToKitchen(),
                        item.isCanceled(),
                        item.getDiscountPercent()))
                .collect(Collectors.toList());
    }

    public Long getCurrentOrderId() {
        return currentOrderId;
    }

    public String getCurrentTableNumber() {
        return currentTableNumber;
    }

    /**
     * Reset UI sau khi xóa order rỗng (mở nhầm bàn) cho đúng bàn đang chọn.
     */
    public void clearCurrentOrderUiAfterEmptyRelease() {
        currentOrderId = null;
        currentTableNumber = null;
        view.displayTableNumber("");
        view.displayOrderItems(List.of());
        view.displayTotalAmount(0);
        view.displayFinalAmount(0);
        view.displayDiscountAmount(0);
        view.refreshMenuItems();
    }

    /**
     * Lấy Order hiện tại với đầy đủ thông tin.
     */
    public Order getCurrentOrder() {
        if (currentOrderId == null) {
            return null;
        }
        try {
            return orderRepository.findById(String.valueOf(currentOrderId)).orElse(null);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy order hiện tại: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy danh sách số bàn đang được sử dụng (có order đang phục vụ).
     */
    public Set<String> getTablesInUse() {
        try {
            List<Order> servingOrders = orderRepository.findAllServing();
            return servingOrders.stream()
                    .map(Order::getTableNumber)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy danh sách bàn đang dùng: " + e.getMessage());
            return new HashSet<>();
        }
    }

    /**
     * Cập nhật số lượng của một order item.
     */
    public void updateItemQuantity(Long orderItemId, int newQuantity) {
        if (currentOrderId == null) {
            view.showError(msg("ui.order.select_table_first"));
            return;
        }

        if (newQuantity <= 0) {
            view.showError(msg("ui.order.quantity_positive"));
            return;
        }

        try {
            UpdateOrderItemQuantityInput input = new UpdateOrderItemQuantityInput(
                    currentOrderId,
                    orderItemId,
                    newQuantity);
            UpdateOrderItemQuantityOutput output = updateOrderItemQuantityUseCase.execute(input);

            view.displayOrderItems(convertToViewModels(output.getItems()));
            view.displayTotalAmount(output.getTotalAmount());
            view.displayFinalAmount(output.getTotalAmount());
            
            // Refresh menu items để cập nhật tồn kho realtime
            view.refreshMenuItems();
        } catch (DomainException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError(msg("ui.order.update_quantity_failed", e.getMessage()));
        }
    }

    /**
     * Hủy món (cancel) - đánh dấu là canceled, không xóa khỏi order.
     * Dùng cho món đã in phiếu bếp.
     */
    public void cancelItem(Long orderItemId) {
        if (currentOrderId == null) {
            view.showError(msg("ui.order.select_table_first"));
            return;
        }

        try {
            RemoveOrderItemInput input = new RemoveOrderItemInput(currentOrderId, orderItemId);
            RemoveOrderItemOutput output = removeOrderItemUseCase.execute(input);

            view.displayOrderItems(convertToViewModels(output.getItems()));
            view.displayTotalAmount(output.getTotalAmount());
            view.displayFinalAmount(output.getTotalAmount());
            view.showSuccess(msg("ui.order.cancel_item_success"));
            
            // Refresh menu items để cập nhật tồn kho realtime (hoàn lại tồn kho)
            view.refreshMenuItems();
        } catch (DomainException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError(msg("ui.order.cancel_item_failed", e.getMessage()));
        }
    }

    /**
     * Xóa món (delete) - xóa hoàn toàn khỏi order.
     * Chỉ cho món CHƯA in phiếu bếp (khi thêm nhầm).
     */
    public void deleteItem(Long orderItemId) {
        if (currentOrderId == null) {
            view.showError(msg("ui.order.select_table_first"));
            return;
        }

        try {
            DeleteOrderItemInput input = new DeleteOrderItemInput(currentOrderId, orderItemId);
            DeleteOrderItemOutput output = deleteOrderItemUseCase.execute(input);

            view.displayOrderItems(convertToViewModels(output.getItems()));
            view.displayTotalAmount(output.getTotalAmount());
            view.displayFinalAmount(output.getTotalAmount());
            view.showSuccess(msg("ui.order.delete_item_success"));
            
            // Refresh menu items để cập nhật tồn kho realtime (hoàn lại tồn kho)
            view.refreshMenuItems();
        } catch (DomainException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError(msg("ui.order.delete_item_failed", e.getMessage()));
        }
    }

    /**
     * Cập nhật ghi chú của order item.
     */
    public void updateItemNote(Long orderItemId, String note) {
        if (currentOrderId == null) {
            return;
        }

        try {
            UpdateOrderItemNoteInput input = new UpdateOrderItemNoteInput(currentOrderId, orderItemId, note);
            UpdateOrderItemNoteOutput output = updateOrderItemNoteUseCase.execute(input);

            // Update view
            List<OrderItemViewModel> viewModels = convertToViewModels(output.getItems());
            view.displayOrderItems(viewModels);
            view.displayTotalAmount(output.getTotalAmount());
            view.displayFinalAmount(output.getTotalAmount());

            view.showSuccess(msg("ui.order.update_note_success"));
        } catch (Exception e) {
            view.showError(msg("ui.order.update_note_failed", e.getMessage()));
        }
    }

    /**
     * Cập nhật phần trăm giảm giá của order item.
     */
    public com.giadinh.apporderbill.orders.usecase.UpdateOrderItemDiscountUseCase getUpdateOrderItemDiscountUseCase() {
        return updateOrderItemDiscountUseCase;
    }

    public void updateItemDiscount(Long orderItemId, double discountPercent) {
        if (currentOrderId == null) {
            return;
        }

        try {
            UpdateOrderItemDiscountInput input = new UpdateOrderItemDiscountInput(currentOrderId, orderItemId, discountPercent);
            UpdateOrderItemDiscountOutput output = updateOrderItemDiscountUseCase.execute(input);

            // Update view
            List<OrderItemViewModel> viewModels = convertToViewModels(output.getItems());
            view.displayOrderItems(viewModels);
            view.displayTotalAmount(output.getTotalAmount());
            view.displayFinalAmount(output.getTotalAmount());

            view.showSuccess(msg("ui.order.update_discount_success"));
        } catch (Exception e) {
            view.showError(msg("ui.order.update_discount_failed", e.getMessage()));
        }
    }

    /**
     * In lại phiếu bếp (bao gồm cả món đã in).
     */
    public void reprintKitchenTicket() {
        if (currentOrderId == null) {
            view.showError(msg("ui.order.select_order_first"));
            return;
        }

        try {
            PrintKitchenTicketInput input = new PrintKitchenTicketInput(currentOrderId, false, true);
            PrintKitchenTicketOutput output = printKitchenTicketUseCase.execute(input);

            if (output.isPrinted()) {
                view.showSuccess(msg("ui.order.reprint_kitchen_success"));
            } else {
                view.showError(msg("ui.order.print_kitchen_unavailable"));
            }

            // Refresh order details
            refreshCurrentOrder();
        } catch (DomainException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError(msg("ui.order.reprint_kitchen_failed", e.getMessage()));
        }
    }

    /**
     * In các món đã chọn.
     */
    public boolean printSelectedItems(List<Long> orderItemIds) {
        if (currentOrderId == null) {
            view.showError(msg("ui.order.select_order_first"));
            return false;
        }

        if (orderItemIds == null || orderItemIds.isEmpty()) {
            view.showError(msg("ui.order.select_items_first"));
            return false;
        }

        try {
            PrintSelectedItemsInput input = new PrintSelectedItemsInput(currentOrderId, orderItemIds);
            PrintKitchenTicketOutput output = printSelectedItemsUseCase.execute(input);

            if (output.isPrinted()) {
                view.showSuccess(msg("ui.order.print_selected_success", orderItemIds.size()));
            } else {
                view.showError(msg("ui.order.print_kitchen_unavailable"));
                return false;
            }

            // Refresh order details
            refreshCurrentOrder();
            return true;
        } catch (DomainException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError(msg("ui.order.print_selected_failed", e.getMessage()));
        }
        return false;
    }

    private void refreshCurrentOrder() {
        if (currentOrderId != null) {
            try {
                GetOrderDetailsInput input = new GetOrderDetailsInput(currentOrderId);
                GetOrderDetailsOutput output = getOrderDetailsUseCase.execute(input);
                // Convert and display items - this will clear and replace existing items
                List<OrderItemViewModel> viewModels = convertToViewModels(output.getItems());
                view.displayOrderItems(viewModels);
                view.displayTotalAmount(output.getTotalAmount());
                view.displayFinalAmount(output.getTotalAmount());
            } catch (Exception e) {
                // Silent fail on refresh
                System.err.println("Error refreshing order: " + e.getMessage());
            }
        }
    }

    /**
     * Set use cases cho chức năng in lại hóa đơn.
     */
    public void setReprintUseCases(GetTodayPaymentsUseCase getTodayPaymentsUseCase,
            ReprintReceiptUseCase reprintReceiptUseCase) {
        this.getTodayPaymentsUseCase = getTodayPaymentsUseCase;
        this.reprintReceiptUseCase = reprintReceiptUseCase;
    }

    /**
     * Set printer service cho chức năng in phiếu tạm.
     */
    public void setPrinterService(com.giadinh.apporderbill.shared.service.PrinterService printerService) {
        this.printerService = printerService;
    }

    /**
     * In phiếu tạm (draft receipt) - không cần thanh toán, không xóa order.
     */
    public void printDraftReceipt(long discountAmount, Double discountPercent) {
        if (currentOrderId == null) {
            view.showError(msg("ui.order.select_table_and_have_order"));
            return;
        }

        if (printerService == null) {
            view.showError(msg("ui.order.printer_service_not_ready"));
            return;
        }

        try {
            // Tính tổng tiền trước để validate
            CalculateOrderTotalInput calcInput = new CalculateOrderTotalInput(
                    currentOrderId,
                    discountAmount,
                    discountPercent);
            calculateOrderTotalUseCase.execute(calcInput);

            // In phiếu tạm
            printerService.printDraftReceipt(currentOrderId, discountAmount, discountPercent);
            view.showSuccess(msg("ui.order.print_draft_success"));
        } catch (com.giadinh.apporderbill.shared.service.PrinterService.PrinterException e) {
            view.showError(msg("ui.order.print_draft_failed", e.getMessage()));
        } catch (Exception e) {
            view.showError(msg("ui.order.print_draft_failed", e.getMessage()));
        }
    }

    /**
     * Hiển thị dialog chọn hóa đơn để in lại.
     */
    public void showReprintReceiptDialog() {
        if (getTodayPaymentsUseCase == null || reprintReceiptUseCase == null) {
            view.showError(msg("ui.order.reprint_receipt_not_ready"));
            return;
        }

        try {
            List<PaymentSummaryOutput> payments = getTodayPaymentsUseCase.execute();

            if (payments.isEmpty()) {
                view.showError(msg("ui.order.no_receipt_today"));
                return;
            }

            // Delegate to view to show dialog
            view.showReprintReceiptDialog(payments, this::reprintReceipt);
        } catch (Exception e) {
            view.showError(msg("ui.order.load_receipts_failed", e.getMessage()));
        }
    }

    /**
     * Thực hiện in lại hóa đơn.
     */
    public void reprintReceipt(Long paymentId) {
        if (reprintReceiptUseCase == null) {
            view.showError(msg("ui.order.reprint_receipt_not_ready"));
            return;
        }

        try {
            reprintReceiptUseCase.execute(paymentId);
            view.showSuccess(msg("ui.order.reprint_receipt_success", paymentId));
        } catch (Exception e) {
            view.showError(msg("ui.order.reprint_receipt_failed", e.getMessage()));
        }
    }

    private String msg(String key, Object... args) {
        return DomainMessages.formatKey(key, args);
    }
}
