package com.giadinh.apporderbill.orders;

import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.*;
import com.giadinh.apporderbill.orders.usecase.dto.*;
import com.giadinh.apporderbill.table.repository.TableRepository;

public class OrdersComponentImpl implements OrdersComponent {
    private final OpenOrCreateOrderUseCase openOrCreateOrderUseCase;
    private final GetOrderDetailsUseCase getOrderDetailsUseCase;
    private final AddMenuItemToOrderUseCase addMenuItemToOrderUseCase;
    private final AddCustomItemToOrderUseCase addCustomItemToOrderUseCase;
    private final UpdateOrderItemQuantityUseCase updateOrderItemQuantityUseCase;
    private final UpdateOrderItemNoteUseCase updateOrderItemNoteUseCase;
    private final UpdateOrderItemDiscountUseCase updateOrderItemDiscountUseCase;
    private final DeleteOrderItemUseCase deleteOrderItemUseCase;
    private final RemoveOrderItemUseCase removeOrderItemUseCase;
    private final CalculateOrderTotalUseCase calculateOrderTotalUseCase;
    private final CheckoutOrderUseCase checkoutOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    public OrdersComponentImpl(OrderRepository orderRepository, MenuItemRepository menuItemRepository,
            PaymentRepository paymentRepository) {
        this.openOrCreateOrderUseCase = new OpenOrCreateOrderUseCase(orderRepository, menuItemRepository);
        this.getOrderDetailsUseCase = new GetOrderDetailsUseCase(orderRepository, menuItemRepository);
        this.addMenuItemToOrderUseCase = new AddMenuItemToOrderUseCase(orderRepository, menuItemRepository);
        this.addCustomItemToOrderUseCase = new AddCustomItemToOrderUseCase(orderRepository);
        this.updateOrderItemQuantityUseCase = new UpdateOrderItemQuantityUseCase(orderRepository, menuItemRepository);
        this.updateOrderItemNoteUseCase = new UpdateOrderItemNoteUseCase(orderRepository, menuItemRepository);
        this.updateOrderItemDiscountUseCase = new UpdateOrderItemDiscountUseCase(orderRepository, menuItemRepository);
        this.deleteOrderItemUseCase = new DeleteOrderItemUseCase(orderRepository, menuItemRepository);
        this.removeOrderItemUseCase = new RemoveOrderItemUseCase(orderRepository, menuItemRepository);
        this.calculateOrderTotalUseCase = new CalculateOrderTotalUseCase(orderRepository);
        this.checkoutOrderUseCase = new CheckoutOrderUseCase(orderRepository, paymentRepository, new TableRepository() {
            public java.util.Optional<com.giadinh.apporderbill.table.model.Table> findById(String id){return java.util.Optional.empty();}
            public java.util.Optional<com.giadinh.apporderbill.table.model.Table> findByTableName(String n){return java.util.Optional.empty();}
            public java.util.List<com.giadinh.apporderbill.table.model.Table> findAll(){return java.util.List.of();}
            public void save(com.giadinh.apporderbill.table.model.Table t){}
            public void delete(String id){}
        }, null);
        this.cancelOrderUseCase = new CancelOrderUseCase(orderRepository, menuItemRepository, null);
    }
    public OpenOrCreateOrderOutput openOrCreateOrder(OpenOrCreateOrderInput input){ return openOrCreateOrderUseCase.execute(input); }
    public GetOrderDetailsOutput getOrderDetails(GetOrderDetailsInput input){ return getOrderDetailsUseCase.execute(input); }
    public AddCustomItemOutput addMenuItemToOrder(AddMenuItemInput input){ return addMenuItemToOrderUseCase.execute(input); }
    public AddCustomItemOutput addCustomItemToOrder(AddCustomItemInput input){ return addCustomItemToOrderUseCase.execute(input); }
    public UpdateOrderItemQuantityOutput updateOrderItemQuantity(UpdateOrderItemQuantityInput input){ return updateOrderItemQuantityUseCase.execute(input); }
    public UpdateOrderItemNoteOutput updateOrderItemNote(UpdateOrderItemNoteInput input){ return updateOrderItemNoteUseCase.execute(input); }
    public UpdateOrderItemDiscountOutput updateOrderItemDiscount(UpdateOrderItemDiscountInput input){ return updateOrderItemDiscountUseCase.execute(input); }
    public DeleteOrderItemOutput deleteOrderItem(DeleteOrderItemInput input){ return deleteOrderItemUseCase.execute(input); }
    public RemoveOrderItemOutput removeOrderItem(RemoveOrderItemInput input){ return removeOrderItemUseCase.execute(input); }
    public CalculateOrderTotalOutput calculateOrderTotal(CalculateOrderTotalInput input){ return calculateOrderTotalUseCase.execute(input); }
    public CheckoutOrderOutput checkoutOrder(CheckoutOrderInput input){ return checkoutOrderUseCase.execute(input); }
    public CancelOrderOutput cancelOrder(CancelOrderInput input){ return cancelOrderUseCase.execute(input); }
}

