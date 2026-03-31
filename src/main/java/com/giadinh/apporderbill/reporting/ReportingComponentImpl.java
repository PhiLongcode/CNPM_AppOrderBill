package com.giadinh.apporderbill.reporting;

import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.reporting.usecase.GetDailyRevenueUseCase;
import com.giadinh.apporderbill.reporting.usecase.GetMonthlyRevenueUseCase;
import com.giadinh.apporderbill.reporting.usecase.GetRevenueByDateRangeUseCase;
import com.giadinh.apporderbill.reporting.usecase.GetWeeklyRevenueUseCase;
import com.giadinh.apporderbill.reporting.usecase.dto.GetDailyRevenueInput;
import com.giadinh.apporderbill.reporting.usecase.dto.GetRevenueByDateRangeInput;
import com.giadinh.apporderbill.reporting.usecase.dto.RevenueOutput;
import com.giadinh.apporderbill.reporting.usecase.dto.RevenueSummaryOutput;

import java.time.LocalDate;

public class ReportingComponentImpl implements ReportingComponent {
    private final GetDailyRevenueUseCase getDailyRevenueUseCase;
    private final GetWeeklyRevenueUseCase getWeeklyRevenueUseCase;
    private final GetMonthlyRevenueUseCase getMonthlyRevenueUseCase;
    private final GetRevenueByDateRangeUseCase getRevenueByDateRangeUseCase;

    public ReportingComponentImpl(PaymentRepository paymentRepository) {
        this.getDailyRevenueUseCase = new GetDailyRevenueUseCase(paymentRepository);
        this.getRevenueByDateRangeUseCase = new GetRevenueByDateRangeUseCase(paymentRepository);
        this.getWeeklyRevenueUseCase = new GetWeeklyRevenueUseCase(getRevenueByDateRangeUseCase);
        this.getMonthlyRevenueUseCase = new GetMonthlyRevenueUseCase(getRevenueByDateRangeUseCase);
    }

    @Override
    public RevenueOutput getDailyRevenue(GetDailyRevenueInput input) {
        RevenueSummaryOutput s = getDailyRevenueUseCase.execute(input == null ? null : input.getDate());
        return new RevenueOutput("daily", s.getTotalRevenue());
    }

    @Override
    public RevenueOutput getWeeklyRevenue() {
        RevenueSummaryOutput s = getWeeklyRevenueUseCase.execute(LocalDate.now());
        return new RevenueOutput("weekly", s.getTotalRevenue());
    }

    @Override
    public RevenueOutput getMonthlyRevenue() {
        RevenueSummaryOutput s = getMonthlyRevenueUseCase.execute(LocalDate.now());
        return new RevenueOutput("monthly", s.getTotalRevenue());
    }

    @Override
    public RevenueSummaryOutput getRevenueByDateRange(GetRevenueByDateRangeInput input) {
        return getRevenueByDateRangeUseCase.execute(input);
    }
}

