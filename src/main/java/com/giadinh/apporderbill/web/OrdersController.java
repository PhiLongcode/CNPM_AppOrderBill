package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.orders.OrdersComponent;
import com.giadinh.apporderbill.orders.usecase.dto.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller cho Orders feature.
 * Mỗi endpoint delegate trực tiếp tới OrdersComponent (Input Port).
 */
@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final OrdersComponent ordersComponent;

    public OrdersController(OrdersComponent ordersComponent) {
        this.ordersComponent = ordersComponent;
    }

    @PostMapping("/open")
    public ResponseEntity<OpenOrCreateOrderOutput> openOrCreateOrder(@RequestBody OpenOrCreateOrderInput input) {
        return ResponseEntity.ok(ordersComponent.openOrCreateOrder(input));
    }

    @PostMapping("/{orderId}/details")
    public ResponseEntity<GetOrderDetailsOutput> getOrderDetails(@PathVariable Long orderId) {
        return ResponseEntity.ok(ordersComponent.getOrderDetails(new GetOrderDetailsInput(orderId)));
    }

    @PostMapping("/{orderId}/items/menu")
    public ResponseEntity<AddCustomItemOutput> addMenuItemToOrder(@RequestBody AddMenuItemInput input) {
        return ResponseEntity.ok(ordersComponent.addMenuItemToOrder(input));
    }

    @PostMapping("/{orderId}/items/custom")
    public ResponseEntity<AddCustomItemOutput> addCustomItemToOrder(@RequestBody AddCustomItemInput input) {
        return ResponseEntity.ok(ordersComponent.addCustomItemToOrder(input));
    }

    @PutMapping("/items/quantity")
    public ResponseEntity<UpdateOrderItemQuantityOutput> updateOrderItemQuantity(
            @RequestBody UpdateOrderItemQuantityInput input) {
        return ResponseEntity.ok(ordersComponent.updateOrderItemQuantity(input));
    }

    @PutMapping("/items/note")
    public ResponseEntity<UpdateOrderItemNoteOutput> updateOrderItemNote(@RequestBody UpdateOrderItemNoteInput input) {
        return ResponseEntity.ok(ordersComponent.updateOrderItemNote(input));
    }

    @PutMapping("/items/discount")
    public ResponseEntity<UpdateOrderItemDiscountOutput> updateOrderItemDiscount(
            @RequestBody UpdateOrderItemDiscountInput input) {
        return ResponseEntity.ok(ordersComponent.updateOrderItemDiscount(input));
    }

    @DeleteMapping("/items/delete")
    public ResponseEntity<DeleteOrderItemOutput> deleteOrderItem(@RequestBody DeleteOrderItemInput input) {
        return ResponseEntity.ok(ordersComponent.deleteOrderItem(input));
    }

    @PostMapping("/items/remove")
    public ResponseEntity<RemoveOrderItemOutput> removeOrderItem(@RequestBody RemoveOrderItemInput input) {
        return ResponseEntity.ok(ordersComponent.removeOrderItem(input));
    }

    @PostMapping("/total")
    public ResponseEntity<CalculateOrderTotalOutput> calculateOrderTotal(@RequestBody CalculateOrderTotalInput input) {
        return ResponseEntity.ok(ordersComponent.calculateOrderTotal(input));
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutOrderOutput> checkoutOrder(@RequestBody CheckoutOrderInput input) {
        return ResponseEntity.ok(ordersComponent.checkoutOrder(input));
    }

    @PostMapping("/cancel")
    public ResponseEntity<CancelOrderOutput> cancelOrder(@RequestBody CancelOrderInput input) {
        return ResponseEntity.ok(ordersComponent.cancelOrder(input));
    }
}
