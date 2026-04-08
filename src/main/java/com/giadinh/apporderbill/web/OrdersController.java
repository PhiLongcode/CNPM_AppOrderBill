package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.orders.OrdersComponent;
import com.giadinh.apporderbill.orders.usecase.dto.*;
import com.giadinh.apporderbill.web.security.ApiAuthorizationService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller cho Orders feature.
 * Mỗi endpoint delegate trực tiếp tới OrdersComponent (Input Port).
 */
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order lifecycle and order item operations")
public class OrdersController {

    private final OrdersComponent ordersComponent;
    private final ApiAuthorizationService authorizationService;

    public OrdersController(OrdersComponent ordersComponent, ApiAuthorizationService authorizationService) {
        this.ordersComponent = ordersComponent;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/open")
    public ResponseEntity<OpenOrCreateOrderOutput> openOrCreateOrder(@RequestHeader(value = "X-Username", required = false) String username,
                                                                     @RequestBody OpenOrCreateOrderInput input) {
        authorizationService.requireOperate(username, "Create Order");
        return ResponseEntity.ok(ordersComponent.openOrCreateOrder(input));
    }

    @PostMapping("/{orderId}/details")
    public ResponseEntity<GetOrderDetailsOutput> getOrderDetails(@RequestHeader(value = "X-Username", required = false) String username,
                                                                 @PathVariable Long orderId) {
        authorizationService.requireView(username, "Create Order");
        return ResponseEntity.ok(ordersComponent.getOrderDetails(new GetOrderDetailsInput(orderId)));
    }

    @PostMapping("/{orderId}/items/menu")
    public ResponseEntity<AddCustomItemOutput> addMenuItemToOrder(@RequestHeader(value = "X-Username", required = false) String username,
                                                                   @RequestBody AddMenuItemInput input) {
        authorizationService.requireOperate(username, "Create Order");
        return ResponseEntity.ok(ordersComponent.addMenuItemToOrder(input));
    }

    @PostMapping("/{orderId}/items/custom")
    public ResponseEntity<AddCustomItemOutput> addCustomItemToOrder(@RequestHeader(value = "X-Username", required = false) String username,
                                                                     @RequestBody AddCustomItemInput input) {
        authorizationService.requireOperate(username, "Create Order");
        return ResponseEntity.ok(ordersComponent.addCustomItemToOrder(input));
    }

    @PutMapping("/items/quantity")
    public ResponseEntity<UpdateOrderItemQuantityOutput> updateOrderItemQuantity(
            @RequestHeader(value = "X-Username", required = false) String username,
            @RequestBody UpdateOrderItemQuantityInput input) {
        authorizationService.requireOperate(username, "Create Order");
        return ResponseEntity.ok(ordersComponent.updateOrderItemQuantity(input));
    }

    @PutMapping("/items/note")
    public ResponseEntity<UpdateOrderItemNoteOutput> updateOrderItemNote(@RequestHeader(value = "X-Username", required = false) String username,
                                                                         @RequestBody UpdateOrderItemNoteInput input) {
        authorizationService.requireOperate(username, "Create Order");
        return ResponseEntity.ok(ordersComponent.updateOrderItemNote(input));
    }

    @PutMapping("/items/discount")
    public ResponseEntity<UpdateOrderItemDiscountOutput> updateOrderItemDiscount(
            @RequestHeader(value = "X-Username", required = false) String username,
            @RequestBody UpdateOrderItemDiscountInput input) {
        authorizationService.requireOperate(username, "Create Order");
        return ResponseEntity.ok(ordersComponent.updateOrderItemDiscount(input));
    }

    @DeleteMapping("/items/delete")
    public ResponseEntity<DeleteOrderItemOutput> deleteOrderItem(@RequestHeader(value = "X-Username", required = false) String username,
                                                                 @RequestBody DeleteOrderItemInput input) {
        authorizationService.requireOperate(username, "Create Order");
        return ResponseEntity.ok(ordersComponent.deleteOrderItem(input));
    }

    @PostMapping("/items/remove")
    public ResponseEntity<RemoveOrderItemOutput> removeOrderItem(@RequestHeader(value = "X-Username", required = false) String username,
                                                                 @RequestBody RemoveOrderItemInput input) {
        authorizationService.requireOperate(username, "Create Order");
        return ResponseEntity.ok(ordersComponent.removeOrderItem(input));
    }

    @PostMapping("/total")
    public ResponseEntity<CalculateOrderTotalOutput> calculateOrderTotal(@RequestHeader(value = "X-Username", required = false) String username,
                                                                         @RequestBody CalculateOrderTotalInput input) {
        authorizationService.requireView(username, "Create Order");
        return ResponseEntity.ok(ordersComponent.calculateOrderTotal(input));
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutOrderOutput> checkoutOrder(@RequestHeader(value = "X-Username", required = false) String username,
                                                             @RequestBody CheckoutOrderInput input) {
        authorizationService.requireOperate(username, "Checkout Order");
        return ResponseEntity.ok(ordersComponent.checkoutOrder(input));
    }

    @PostMapping("/cancel")
    public ResponseEntity<CancelOrderOutput> cancelOrder(@RequestHeader(value = "X-Username", required = false) String username,
                                                         @RequestBody CancelOrderInput input) {
        authorizationService.requireOperate(username, "Create Order");
        return ResponseEntity.ok(ordersComponent.cancelOrder(input));
    }
}
