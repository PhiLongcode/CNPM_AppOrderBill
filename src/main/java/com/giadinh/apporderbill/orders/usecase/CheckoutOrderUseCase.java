package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.billing.model.Payment;
import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.customer.usecase.CustomerUseCases;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.model.OrderStatus;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.CheckoutOrderInput;
import com.giadinh.apporderbill.orders.usecase.dto.CheckoutOrderOutput;
import com.giadinh.apporderbill.table.repository.TableRepository;

public class CheckoutOrderUseCase {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final TableRepository tableRepository;
    private final CustomerUseCases customerUseCases;

    public CheckoutOrderUseCase(OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            TableRepository tableRepository,
            CustomerUseCases customerUseCases) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.tableRepository = tableRepository;
        this.customerUseCases = customerUseCases;
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

            long totalAmount = Math.round(order.getTotalAmount());
            long discountAmount = input.getDiscountAmount() == null ? 0L : input.getDiscountAmount();
            long finalAmount = Math.max(0L, totalAmount - discountAmount);
            if (input.getDiscountPercent() != null && input.getDiscountPercent() > 0) {
                long discountByPercent = Math.round(totalAmount * (input.getDiscountPercent() / 100.0));
                finalAmount = Math.max(0L, finalAmount - discountByPercent);
            }
            if (input.getPaidAmount() < finalAmount) {
                throw new DomainException(ErrorCode.CHECKOUT_PAID_AMOUNT_INSUFFICIENT, finalAmount);
            }

            Payment payment = new Payment(
                    order.getOrderId(),
                    totalAmount,
                    finalAmount,
                    input.getPaidAmount(),
                    input.getPaymentMethod() == null ? "CASH" : input.getPaymentMethod(),
                    input.getDiscountAmount(),
                    input.getDiscountPercent(),
                    input.getCashier() == null ? "SYSTEM" : input.getCashier());
            payment = paymentRepository.save(payment);

            if (customerUseCases != null) {
                int pointsEarned = (int) (finalAmount / 10_000L);
                customerUseCases.addPointsByPhone(input.getCustomerPhone(), pointsEarned);
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
}
