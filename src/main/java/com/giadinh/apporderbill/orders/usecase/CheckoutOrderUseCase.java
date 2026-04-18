package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.billing.model.Payment;
import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.customer.model.LoyaltyConfig;
import com.giadinh.apporderbill.customer.model.LoyaltyGift;
import com.giadinh.apporderbill.customer.model.LoyaltyRedeemMenuItem;
import com.giadinh.apporderbill.customer.model.LoyaltyRedeemMode;
import com.giadinh.apporderbill.customer.repository.LoyaltyGiftRepository;
import com.giadinh.apporderbill.customer.repository.LoyaltyRedeemMenuItemRepository;
import com.giadinh.apporderbill.customer.usecase.CustomerUseCases;
import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.model.OrderItem;
import com.giadinh.apporderbill.orders.model.OrderStatus;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.CalculateOrderTotalInput;
import com.giadinh.apporderbill.orders.usecase.dto.CheckoutOrderInput;
import com.giadinh.apporderbill.orders.usecase.dto.CheckoutOrderOutput;
import com.giadinh.apporderbill.table.repository.TableRepository;

import java.util.Objects;

public class CheckoutOrderUseCase {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final TableRepository tableRepository;
    private final CustomerUseCases customerUseCases;
    private final CalculateOrderTotalUseCase calculateOrderTotalUseCase;
    private final MenuItemRepository menuItemRepository;
    private final LoyaltyRedeemMenuItemRepository loyaltyRedeemMenuItemRepository;
    private final LoyaltyGiftRepository loyaltyGiftRepository;

    public CheckoutOrderUseCase(OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            TableRepository tableRepository,
            CustomerUseCases customerUseCases,
            CalculateOrderTotalUseCase calculateOrderTotalUseCase,
            MenuItemRepository menuItemRepository,
            LoyaltyRedeemMenuItemRepository loyaltyRedeemMenuItemRepository,
            LoyaltyGiftRepository loyaltyGiftRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.tableRepository = tableRepository;
        this.customerUseCases = customerUseCases;
        this.calculateOrderTotalUseCase = Objects.requireNonNull(calculateOrderTotalUseCase);
        this.menuItemRepository = menuItemRepository;
        this.loyaltyRedeemMenuItemRepository = loyaltyRedeemMenuItemRepository;
        this.loyaltyGiftRepository = loyaltyGiftRepository;
    }

    public CheckoutOrderOutput execute(CheckoutOrderInput input) {
        if (input == null || input.getOrderId() == null) {
            throw new DomainException(ErrorCode.CHECKOUT_ORDER_ID_REQUIRED);
        }

        try {
            Order order = orderRepository.findById(String.valueOf(input.getOrderId()))
                    .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));

            if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.IN_PROGRESS) {
                throw new DomainException(ErrorCode.CHECKOUT_ORDER_NOT_PAYABLE_STATE);
            }

            LoyaltyRedeemMode mode = input.resolveLoyaltyRedeemMode();
            validateLoyaltyInput(mode, input);

            if (mode == LoyaltyRedeemMode.DISH) {
                applyDishRedeem(order, input);
            }

            order = orderRepository.findById(String.valueOf(input.getOrderId()))
                    .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));

            long orderDiscountInput = input.getDiscountAmount() == null ? 0L : input.getDiscountAmount();
            var calc = calculateOrderTotalUseCase.execute(
                    new CalculateOrderTotalInput(input.getOrderId(), orderDiscountInput, input.getDiscountPercent()));

            long subtotal = calc.getSubtotal();
            long orderDiscountTotal = calc.getDiscountAmount();
            long netAmountBeforeVat = calc.getNetAmountBeforeVat();
            long vatAmount = calc.getVatAmount();
            double vatPercent = calc.getVatPercent();
            long amountAfterVatBeforePoints = calc.getFinalAmount();

            long pointsDiscount = 0L;
            LoyaltyConfig loyaltyConfig = customerUseCases != null
                    ? customerUseCases.getLoyaltyConfig()
                    : LoyaltyConfig.defaults();

            if (mode == LoyaltyRedeemMode.BILL_DISCOUNT
                    && input.getPointsUsed() > 0
                    && customerUseCases != null
                    && input.getCustomerId() != null) {
                pointsDiscount = customerUseCases.redeemPoints(
                        input.getCustomerId(), input.getPointsUsed(),
                        String.valueOf(input.getOrderId()));
            }

            long finalAmount = Math.max(0L, amountAfterVatBeforePoints - pointsDiscount);

            if (input.getPaidAmount() < finalAmount) {
                throw new DomainException(ErrorCode.CHECKOUT_PAID_AMOUNT_INSUFFICIENT, finalAmount);
            }

            Long storedOrderDiscount = orderDiscountTotal > 0 ? orderDiscountTotal : null;

            Payment payment = new Payment(
                    order.getOrderId(),
                    subtotal,
                    finalAmount,
                    input.getPaidAmount(),
                    input.getPaymentMethod() == null ? "CASH" : input.getPaymentMethod(),
                    storedOrderDiscount,
                    input.getDiscountPercent(),
                    input.getCashier() == null ? "SYSTEM" : input.getCashier(),
                    input.getCustomerId(),
                    vatAmount,
                    vatPercent,
                    pointsDiscount,
                    netAmountBeforeVat,
                    amountAfterVatBeforePoints);
            payment = paymentRepository.save(payment);

            if (mode == LoyaltyRedeemMode.GIFT_NON_MONETARY) {
                applyGiftRedeemAfterPayment(input);
            }

            if (customerUseCases != null && input.getCustomerId() != null) {
                int pointsEarned = loyaltyConfig.calcEarnedPoints(finalAmount);
                customerUseCases.addPoints(
                        input.getCustomerId(), pointsEarned,
                        String.valueOf(input.getOrderId()));
            }

            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);

            String tableName = order.getTableNumber();
            if (tableName != null) {
                tableRepository.findByTableName(tableName).ifPresent(table -> {
                    table.clearTable();
                    tableRepository.save(table);
                });
            }

            return new CheckoutOrderOutput(payment.getPaymentId(), input.getOrderId());

        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException(ErrorCode.INTERNAL_ERROR, null, e.getMessage(), null);
        }
    }

    private void validateLoyaltyInput(LoyaltyRedeemMode mode, CheckoutOrderInput input) {
        if (mode == LoyaltyRedeemMode.NONE) {
            return;
        }
        if (input.getCustomerId() == null) {
            throw new DomainException(ErrorCode.LOYALTY_REDEEM_REQUIRES_CUSTOMER);
        }
        if (mode == LoyaltyRedeemMode.DISH) {
            if (input.getLoyaltyRedeemCatalogId() == null) {
                throw new DomainException(ErrorCode.LOYALTY_REDEEM_MODE_INVALID);
            }
        } else if (mode == LoyaltyRedeemMode.GIFT_NON_MONETARY) {
            if (input.getLoyaltyGiftId() == null) {
                throw new DomainException(ErrorCode.LOYALTY_REDEEM_MODE_INVALID);
            }
        }
    }

    private void applyDishRedeem(Order order, CheckoutOrderInput input) {
        if (customerUseCases == null) {
            throw new DomainException(ErrorCode.LOYALTY_REDEEM_MODE_INVALID);
        }
        if (menuItemRepository == null || loyaltyRedeemMenuItemRepository == null) {
            throw new DomainException(ErrorCode.LOYALTY_REDEEM_MODE_INVALID);
        }
        long catalogId = input.getLoyaltyRedeemCatalogId();
        LoyaltyRedeemMenuItem catalog = loyaltyRedeemMenuItemRepository.findById(catalogId)
                .orElseThrow(() -> new DomainException(ErrorCode.LOYALTY_REDEEM_CATALOG_NOT_FOUND));
        if (!catalog.isActive()) {
            throw new DomainException(ErrorCode.LOYALTY_REDEEM_CATALOG_INACTIVE);
        }
        MenuItem menu = menuItemRepository.findById(catalog.getMenuItemId())
                .orElseThrow(() -> new DomainException(ErrorCode.MENU_ITEM_NOT_FOUND));
        if (menu.isStockTracked()) {
            boolean ok = menuItemRepository.decreaseStockAtomic(menu.getId(), 1);
            if (!ok) {
                throw new DomainException(ErrorCode.MENU_ITEM_STOCK_INSUFFICIENT);
            }
        }
        String label = menu.getName() == null ? ("#" + menu.getId()) : menu.getName();
        customerUseCases.redeemPointsForDish(
                input.getCustomerId(),
                catalog.getPointsCost(),
                label,
                String.valueOf(input.getOrderId()));
        OrderItem item = new OrderItem(
                order.getOrderId(),
                String.valueOf(menu.getId()),
                (menu.getName() == null ? "Mon" : menu.getName()) + " (Doi diem)",
                1,
                0.0);
        item.setLoyaltyRedeemPoints(catalog.getPointsCost());
        order.addOrderItem(item);
        orderRepository.save(order);
    }

    private void applyGiftRedeemAfterPayment(CheckoutOrderInput input) {
        if (customerUseCases == null || loyaltyGiftRepository == null) {
            throw new DomainException(ErrorCode.LOYALTY_REDEEM_MODE_INVALID);
        }
        long giftId = input.getLoyaltyGiftId();
        LoyaltyGift gift = loyaltyGiftRepository.findById(giftId)
                .orElseThrow(() -> new DomainException(ErrorCode.LOYALTY_GIFT_NOT_FOUND));
        if (!gift.isActive()) {
            throw new DomainException(ErrorCode.LOYALTY_GIFT_INACTIVE);
        }
        customerUseCases.redeemPointsForGift(
                input.getCustomerId(),
                gift.getPointsCost(),
                gift.getName(),
                String.valueOf(input.getOrderId()));
    }
}
