package com.giadinh.apporderbill.orders;

import com.giadinh.apporderbill.orders.usecase.dto.*;

public interface OrdersComponent {
    OpenOrCreateOrderOutput openOrCreateOrder(OpenOrCreateOrderInput input);
    GetOrderDetailsOutput getOrderDetails(GetOrderDetailsInput input);
    AddCustomItemOutput addMenuItemToOrder(AddMenuItemInput input);
    AddCustomItemOutput addCustomItemToOrder(AddCustomItemInput input);
    UpdateOrderItemQuantityOutput updateOrderItemQuantity(UpdateOrderItemQuantityInput input);
    UpdateOrderItemNoteOutput updateOrderItemNote(UpdateOrderItemNoteInput input);
    UpdateOrderItemDiscountOutput updateOrderItemDiscount(UpdateOrderItemDiscountInput input);
    DeleteOrderItemOutput deleteOrderItem(DeleteOrderItemInput input);
    RemoveOrderItemOutput removeOrderItem(RemoveOrderItemInput input);
    CalculateOrderTotalOutput calculateOrderTotal(CalculateOrderTotalInput input);
    CheckoutOrderOutput checkoutOrder(CheckoutOrderInput input);
    CancelOrderOutput cancelOrder(CancelOrderInput input);
}

