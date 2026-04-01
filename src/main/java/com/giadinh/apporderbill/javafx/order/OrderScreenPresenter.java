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
    public void openQuickRestockDialog(com.giadinh.apporderbill.menu.usecase.dto.MenuItemOutput menuItem) {
        if (menuItem == null || menuItem.getMenuItemId() == null) {
            view.showError("Không xác định được món để nhập kho.");
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
            view.showError("Số bàn không được để trống");
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
                    view.showError("Không đọc được mã đơn hàng của bàn này. Vui lòng thử tạo order mới hoặc liên hệ quản trị.");
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
            view.showSuccess("Đã mở order cho bàn " + tableNumber);
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError("Lỗi khi mở order: " + e.getMessage());
        }
    }

    /**
     * Xử lý khi người dùng thêm món phát sinh (custom item).
     */
    public void onAddCustomItem() {
        if (currentOrderId == null) {
            view.showError("Vui lòng chọn bàn trước");
            return;
        }

        String itemName = view.getItemName();
        String quantityText = view.getItemQuantity();
        String priceText = view.getItemPrice();

        // Validation
        if (itemName == null || itemName.isBlank()) {
            view.showError("Tên món không được để trống");
            return;
        }
        if (quantityText == null || quantityText.isBlank()) {
            view.showError("Số lượng không được để trống");
            return;
        }
        if (priceText == null || priceText.isBlank()) {
            view.showError("Đơn giá không được để trống");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityText.trim());
            long unitPrice = Long.parseLong(priceText.trim());

            if (quantity <= 0) {
                view.showError("Số lượng phải lớn hơn 0");
                return;
            }
            if (unitPrice <= 0) {
                view.showError("Đơn giá phải lớn hơn 0");
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
            view.showError("Số lượng và đơn giá phải là số hợp lệ");
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        } catch (IllegalStateException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError("Lỗi khi thêm món: " + e.getMessage());
        }
    }

    /**
     * Xử lý khi người dùng thêm món từ menu.
     */
    public void onAddMenuItem(Long menuItemId, int quantity) {
        if (currentOrderId == null) {
            view.showError("Vui lòng chọn bàn trước");
            return;
        }

        if (menuItemId == null) {
            view.showError("Menu item ID không được để trống");
            return;
        }

        if (quantity <= 0) {
            view.showError("Số lượng phải lớn hơn 0");
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
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        } catch (IllegalStateException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError("Lỗi khi thêm món: " + e.getMessage());
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
            view.showError("Lỗi khi làm mới order: " + e.getMessage());
        }
    }

    /**
     * Tính tổng tiền với giảm giá.
     */
    public void calculateTotalWithDiscount(long discountAmount, Double discountPercent) {
        if (currentOrderId == null) {
            view.showError("Vui lòng chọn bàn trước");
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
            view.showError("Lỗi khi tính tổng tiền: " + e.getMessage());
        }
    }

    /**
     * Hủy order.
     */
    public void cancelOrder(String reason) {
        if (currentOrderId == null) {
            view.showError("Không có order nào để hủy");
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

            view.showSuccess("Đã hủy order thành công");
            
            // Refresh menu items để cập nhật tồn kho realtime (hoàn lại tồn kho)
            view.refreshMenuItems();
        } catch (Exception e) {
            view.showError("Lỗi khi hủy order: " + e.getMessage());
        }
    }

    /**
     * In phiếu bếp.
     */
    public void printKitchenTicket(boolean isAddOn) {
        if (currentOrderId == null) {
            view.showError("Vui lòng chọn bàn và có món trong order");
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
                    view.showError("Lỗi in phiếu bếp: " + errorMsg);
                } else {
                    view.showError("In phiếu bếp bị hủy hoặc thất bại. Vui lòng kiểm tra máy in.");
                }
                // Vẫn refresh order vì món đã được đánh dấu là đã in
                refreshOrder();
            }
        } catch (IllegalStateException e) {
            // Không có món cần in
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError("Lỗi khi in phiếu bếp: " + e.getMessage());
        }
    }

    /**
     * Thanh toán order.
     */
    public void checkout(long paidAmount, String paymentMethod, long discountAmount, Double discountPercent) {
        if (currentOrderId == null) {
            view.showError("Vui lòng chọn bàn và có món trong order");
            return;
        }

        try {
            // Tính tổng tiền trước
            CalculateOrderTotalInput calcInput = new CalculateOrderTotalInput(
                    currentOrderId,
                    discountAmount,
                    discountPercent);
            CalculateOrderTotalOutput calcOutput = calculateOrderTotalUseCase.execute(calcInput);

            if (paidAmount < calcOutput.getFinalAmount()) {
                view.showError("Số tiền khách đưa không đủ. Cần: " + calcOutput.getFinalAmount() + " VNĐ");
                return;
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
            } catch (Exception e) {
                // In thất bại - thông báo nhưng thanh toán đã được lưu
                System.err.println("In hóa đơn thất bại: " + e.getMessage());
                view.showError("Thanh toán đã lưu thành công!\n\nNhưng in hóa đơn thất bại: " + e.getMessage() +
                        "\n\nBạn có thể in lại từ lịch sử thanh toán.");
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

        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError("Lỗi khi thanh toán: " + e.getMessage());
        }
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
            view.showError("Vui lòng chọn bàn trước");
            return;
        }

        if (newQuantity <= 0) {
            view.showError("Số lượng phải lớn hơn 0");
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
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError("Lỗi khi cập nhật số lượng: " + e.getMessage());
        }
    }

    /**
     * Hủy món (cancel) - đánh dấu là canceled, không xóa khỏi order.
     * Dùng cho món đã in phiếu bếp.
     */
    public void cancelItem(Long orderItemId) {
        if (currentOrderId == null) {
            view.showError("Vui lòng chọn bàn trước");
            return;
        }

        try {
            RemoveOrderItemInput input = new RemoveOrderItemInput(currentOrderId, orderItemId);
            RemoveOrderItemOutput output = removeOrderItemUseCase.execute(input);

            view.displayOrderItems(convertToViewModels(output.getItems()));
            view.displayTotalAmount(output.getTotalAmount());
            view.displayFinalAmount(output.getTotalAmount());
            view.showSuccess("Đã hủy món");
            
            // Refresh menu items để cập nhật tồn kho realtime (hoàn lại tồn kho)
            view.refreshMenuItems();
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        } catch (IllegalStateException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError("Lỗi khi hủy món: " + e.getMessage());
        }
    }

    /**
     * Xóa món (delete) - xóa hoàn toàn khỏi order.
     * Chỉ cho món CHƯA in phiếu bếp (khi thêm nhầm).
     */
    public void deleteItem(Long orderItemId) {
        if (currentOrderId == null) {
            view.showError("Vui lòng chọn bàn trước");
            return;
        }

        try {
            DeleteOrderItemInput input = new DeleteOrderItemInput(currentOrderId, orderItemId);
            DeleteOrderItemOutput output = deleteOrderItemUseCase.execute(input);

            view.displayOrderItems(convertToViewModels(output.getItems()));
            view.displayTotalAmount(output.getTotalAmount());
            view.displayFinalAmount(output.getTotalAmount());
            view.showSuccess("Đã xóa món khỏi order");
            
            // Refresh menu items để cập nhật tồn kho realtime (hoàn lại tồn kho)
            view.refreshMenuItems();
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        } catch (IllegalStateException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError("Lỗi khi xóa món: " + e.getMessage());
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

            view.showSuccess("Đã cập nhật ghi chú");
        } catch (Exception e) {
            view.showError("Lỗi khi cập nhật ghi chú: " + e.getMessage());
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

            view.showSuccess("Đã cập nhật giảm giá");
        } catch (Exception e) {
            view.showError("Lỗi khi cập nhật giảm giá: " + e.getMessage());
        }
    }

    /**
     * In lại phiếu bếp (bao gồm cả món đã in).
     */
    public void reprintKitchenTicket() {
        if (currentOrderId == null) {
            view.showError("Vui lòng chọn order trước");
            return;
        }

        try {
            PrintKitchenTicketInput input = new PrintKitchenTicketInput(currentOrderId, false, true);
            PrintKitchenTicketOutput output = printKitchenTicketUseCase.execute(input);

            if (output.isPrinted()) {
                view.showSuccess("Đã in lại phiếu bếp");
            } else {
                view.showError("Không thể in phiếu bếp. Vui lòng kiểm tra máy in");
            }

            // Refresh order details
            refreshCurrentOrder();
        } catch (IllegalStateException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError("Lỗi khi in lại phiếu bếp: " + e.getMessage());
        }
    }

    /**
     * In các món đã chọn.
     */
    public void printSelectedItems(List<Long> orderItemIds) {
        if (currentOrderId == null) {
            view.showError("Vui lòng chọn order trước");
            return;
        }

        if (orderItemIds == null || orderItemIds.isEmpty()) {
            view.showError("Vui lòng chọn ít nhất một món");
            return;
        }

        try {
            PrintSelectedItemsInput input = new PrintSelectedItemsInput(currentOrderId, orderItemIds);
            PrintKitchenTicketOutput output = printSelectedItemsUseCase.execute(input);

            if (output.isPrinted()) {
                view.showSuccess("Đã in " + orderItemIds.size() + " món đã chọn");
            } else {
                view.showError("Không thể in phiếu bếp. Vui lòng kiểm tra máy in");
            }

            // Refresh order details
            refreshCurrentOrder();
        } catch (IllegalStateException e) {
            view.showError(e.getMessage());
        } catch (Exception e) {
            view.showError("Lỗi khi in món đã chọn: " + e.getMessage());
        }
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
            view.showError("Vui lòng chọn bàn và có món trong order");
            return;
        }

        if (printerService == null) {
            view.showError("Dịch vụ in chưa được khởi tạo");
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
            view.showSuccess("Đã in phiếu tạm thành công");
        } catch (com.giadinh.apporderbill.shared.service.PrinterService.PrinterException e) {
            view.showError("Lỗi khi in phiếu tạm: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Lỗi khi in phiếu tạm: " + e.getMessage());
        }
    }

    /**
     * Hiển thị dialog chọn hóa đơn để in lại.
     */
    public void showReprintReceiptDialog() {
        if (getTodayPaymentsUseCase == null || reprintReceiptUseCase == null) {
            view.showError("Chức năng in lại hóa đơn chưa được khởi tạo");
            return;
        }

        try {
            List<PaymentSummaryOutput> payments = getTodayPaymentsUseCase.execute();

            if (payments.isEmpty()) {
                view.showError("Chưa có hóa đơn nào trong ngày hôm nay");
                return;
            }

            // Delegate to view to show dialog
            view.showReprintReceiptDialog(payments, this::reprintReceipt);
        } catch (Exception e) {
            view.showError("Lỗi khi tải danh sách hóa đơn: " + e.getMessage());
        }
    }

    /**
     * Thực hiện in lại hóa đơn.
     */
    public void reprintReceipt(Long paymentId) {
        if (reprintReceiptUseCase == null) {
            view.showError("Chức năng in lại hóa đơn chưa được khởi tạo");
            return;
        }

        try {
            reprintReceiptUseCase.execute(paymentId);
            view.showSuccess("Đã gửi lệnh in lại hóa đơn #" + paymentId);
        } catch (Exception e) {
            view.showError("Lỗi khi in lại hóa đơn: " + e.getMessage());
        }
    }
}
