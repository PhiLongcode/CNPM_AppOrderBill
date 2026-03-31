package com.giadinh.apporderbill.reporting.usecase;

import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.reporting.usecase.dto.GetRevenueByDateRangeInput;
import com.giadinh.apporderbill.reporting.usecase.dto.RevenueOutput;
import com.giadinh.apporderbill.reporting.usecase.dto.RevenueSummaryOutput;

import java.util.List;

public class GetRevenueByDateRangeUseCase {
    private final PaymentRepository paymentRepository;

    public GetRevenueByDateRangeUseCase(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public RevenueSummaryOutput execute(GetRevenueByDateRangeInput input) {
        var payments = paymentRepository.findByPaidAtBetween(input.getStart(), input.getEnd());
        long total = payments.stream().mapToLong(p -> p.getFinalAmount()).sum();
        List<RevenueOutput> items = payments.stream()
                .map(p -> new RevenueOutput(
                        p.getPaidAt() == null ? "" : p.getPaidAt().toLocalDate().toString(),
                        p.getFinalAmount()))
                .toList();
        return new RevenueSummaryOutput(total, items);
    }
}

