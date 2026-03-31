package com.giadinh.apporderbill.orders.usecase;

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
        try {
            if (input == null || input.getOrderId() == null) {
                return new CheckoutOrderOutput(false, "Thiếu thông tin order để thanh toán.", null, null);
            }

            Order order = orderRepository.findById(String.valueOf(input.getOrderId()))
                    .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại."));

            if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.IN_PROGRESS) {
                return new CheckoutOrderOutput(
                        false,
                        "Đơn hàng không thể thanh toán ở trạng thái hiện tại.",
                        null,
                        input.getOrderId());
            }

            long totalAmount = Math.round(order.getTotalAmount());
            long discountAmount = input.getDiscountAmount() == null ? 0L : input.getDiscountAmount();
            long finalAmount = Math.max(0L, totalAmount - discountAmount);
            if (input.getDiscountPercent() != null && input.getDiscountPercent() > 0) {
                long discountByPercent = Math.round(totalAmount * (input.getDiscountPercent() / 100.0));
                finalAmount = Math.max(0L, finalAmount - discountByPercent);
            }
            if (input.getPaidAmount() < finalAmount) {
                return new CheckoutOrderOutput(
                        false,
                        "Số tiền khách đưa không đủ. Cần: " + finalAmount + " VNĐ",
                        null,
                        input.getOrderId());
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

            // Auto loyalty points: 1 point per 10,000 VND paid.
            if (customerUseCases != null) {
                int pointsEarned = (int) (finalAmount / 10_000L);
                customerUseCases.addPointsByPhone(input.getCustomerPhone(), pointsEarned);
            }

            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);

            if (order.getTableId() != null) {
                tableRepository.findById(order.getTableId()).ifPresent(table -> {
                    table.clearTable();
                    tableRepository.save(table);
                });
            }

            return new CheckoutOrderOutput(
                    true,
                    "Thanh toán và giải phóng bàn thành công.",
                    payment.getPaymentId(),
                    input.getOrderId());

        } catch (IllegalArgumentException | IllegalStateException e) {
            return new CheckoutOrderOutput(
                    false,
                    "Lỗi thanh toán: " + e.getMessage(),
                    null,
                    input == null ? null : input.getOrderId());
        } catch (Exception e) {
            return new CheckoutOrderOutput(
                    false,
                    "Đã xảy ra lỗi không xác định khi thanh toán: " + e.getMessage(),
                    null,
                    input == null ? null : input.getOrderId());
        }
    }
}
