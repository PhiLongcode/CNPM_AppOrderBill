package com.giadinh.apporderbill.billing.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.billing.usecase.dto.PaymentDetailOutput;
import com.giadinh.apporderbill.billing.usecase.dto.PaymentDetailOutput.PaymentItemOutput;
import com.giadinh.apporderbill.customer.model.Customer;
import com.giadinh.apporderbill.customer.usecase.CustomerUseCases;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.repository.OrderRepository;

import java.util.List;

public class GetPaymentDetailUseCase {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final Object menuItemRepository;
    private final CustomerUseCases customerUseCases;

    public GetPaymentDetailUseCase(PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            Object menuItemRepository,
            CustomerUseCases customerUseCases) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.customerUseCases = customerUseCases;
    }

    public PaymentDetailOutput execute(Long paymentId) {
        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new DomainException(ErrorCode.BILL_NOT_FOUND));

        Order order = orderRepository.findById(payment.getOrderId()).orElse(null);
        String table = order != null ? order.getTableId() : "?";

        // Map order items to PaymentItemOutput
        List<PaymentItemOutput> items = List.of();
        if (order != null && order.getItems() != null) {
            items = order.getItems().stream()
                    .map(item -> {
                        String discountText = buildDiscountText(item.getDiscountPercent(), item.getDiscountAmount());
                        long lineTotal = Math.round(item.getLineTotal());
                        return new PaymentItemOutput(
                                item.getMenuItemName(),
                                item.getQuantity(),
                                "",
                                Math.round(item.getPrice()),
                                discountText,
                                lineTotal);
                    })
                    .toList();
        }

        String customerName = null;
        String customerPhone = null;
        Long custId = payment.getCustomerId();
        if (custId == null && order != null) {
            custId = order.getCustomerId();
        }
        if (custId != null && customerUseCases != null) {
            Customer c = customerUseCases.getCustomerById(custId);
            if (c != null) {
                customerName = c.getName();
                customerPhone = c.getPhone();
            }
        }

        long netBeforeVat = payment.getNetAmountBeforeVat();
        long amountAfterVatBeforePoints = payment.getAmountAfterVatBeforePoints();
        if (netBeforeVat == 0 && amountAfterVatBeforePoints == 0 && payment.getTotalAmount() > 0) {
            long d = payment.getDiscountAmount() == null ? 0L : payment.getDiscountAmount();
            netBeforeVat = Math.max(0L, payment.getTotalAmount() - d);
            if (payment.getVatAmount() > 0) {
                amountAfterVatBeforePoints = netBeforeVat + payment.getVatAmount();
            } else {
                amountAfterVatBeforePoints = payment.getFinalAmount() + payment.getPointsDiscountAmount();
            }
        }

        return new PaymentDetailOutput(
                payment.getPaymentId(),
                payment.getOrderId(),
                table,
                payment.getTotalAmount(),
                payment.getFinalAmount(),
                payment.getPaidAmount(),
                payment.getPaymentMethod(),
                payment.getDiscountAmount(),
                payment.getDiscountPercent(),
                payment.getCashier(),
                payment.getPaidAt(),
                items,
                customerName,
                customerPhone,
                payment.getVatAmount(),
                payment.getVatPercent(),
                payment.getPointsDiscountAmount(),
                netBeforeVat,
                amountAfterVatBeforePoints);
    }

    private String buildDiscountText(Double pct, Double amt) {
        if ((pct == null || pct == 0) && (amt == null || amt == 0)) return "-";
        if (pct != null && pct > 0 && (amt == null || amt == 0)) return pct + "%";
        if (amt != null && amt > 0 && (pct == null || pct == 0)) return "-" + Math.round(amt);
        return pct + "% / -" + Math.round(amt != null ? amt : 0);
    }
}

