package com.giadinh.apporderbill.reporting.usecase;

import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.reporting.usecase.dto.GetRevenueByDateRangeInput;
import com.giadinh.apporderbill.reporting.usecase.dto.RevenueSummaryOutput;

import java.time.LocalDate;
import java.time.LocalTime;

public class GetDailyRevenueUseCase {
    private final GetRevenueByDateRangeUseCase byDateRangeUseCase;

    public GetDailyRevenueUseCase(PaymentRepository paymentRepository) {
        this.byDateRangeUseCase = new GetRevenueByDateRangeUseCase(paymentRepository);
    }

    public RevenueSummaryOutput execute(LocalDate date) {
        LocalDate d = date == null ? LocalDate.now() : date;
        return byDateRangeUseCase.execute(
                new GetRevenueByDateRangeInput(d.atStartOfDay(), d.atTime(LocalTime.MAX)));
    }
}

