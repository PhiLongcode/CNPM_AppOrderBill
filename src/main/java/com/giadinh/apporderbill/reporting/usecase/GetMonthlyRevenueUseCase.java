package com.giadinh.apporderbill.reporting.usecase;

import com.giadinh.apporderbill.reporting.usecase.dto.GetRevenueByDateRangeInput;
import com.giadinh.apporderbill.reporting.usecase.dto.RevenueSummaryOutput;

import java.time.LocalDate;
import java.time.LocalTime;

public class GetMonthlyRevenueUseCase {
    private final GetRevenueByDateRangeUseCase byDateRangeUseCase;

    public GetMonthlyRevenueUseCase(GetRevenueByDateRangeUseCase byDateRangeUseCase) {
        this.byDateRangeUseCase = byDateRangeUseCase;
    }

    public RevenueSummaryOutput execute(LocalDate anyDayInMonth) {
        LocalDate day = anyDayInMonth == null ? LocalDate.now() : anyDayInMonth;
        LocalDate start = day.withDayOfMonth(1);
        LocalDate end = day.withDayOfMonth(day.lengthOfMonth());
        return byDateRangeUseCase.execute(
                new GetRevenueByDateRangeInput(start.atStartOfDay(), end.atTime(LocalTime.MAX)));
    }
}

