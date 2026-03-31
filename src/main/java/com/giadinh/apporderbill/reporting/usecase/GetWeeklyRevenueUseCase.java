package com.giadinh.apporderbill.reporting.usecase;

import com.giadinh.apporderbill.reporting.usecase.dto.GetRevenueByDateRangeInput;
import com.giadinh.apporderbill.reporting.usecase.dto.RevenueSummaryOutput;

import java.time.LocalDate;
import java.time.LocalTime;

public class GetWeeklyRevenueUseCase {
    private final GetRevenueByDateRangeUseCase byDateRangeUseCase;

    public GetWeeklyRevenueUseCase(GetRevenueByDateRangeUseCase byDateRangeUseCase) {
        this.byDateRangeUseCase = byDateRangeUseCase;
    }

    public RevenueSummaryOutput execute(LocalDate anyDayInWeek) {
        LocalDate day = anyDayInWeek == null ? LocalDate.now() : anyDayInWeek;
        LocalDate start = day.minusDays(day.getDayOfWeek().getValue() - 1L);
        LocalDate end = start.plusDays(6);
        return byDateRangeUseCase.execute(
                new GetRevenueByDateRangeInput(start.atStartOfDay(), end.atTime(LocalTime.MAX)));
    }
}

