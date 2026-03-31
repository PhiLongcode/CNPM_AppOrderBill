package com.giadinh.apporderbill.reporting;

import com.giadinh.apporderbill.reporting.usecase.dto.GetDailyRevenueInput;
import com.giadinh.apporderbill.reporting.usecase.dto.GetRevenueByDateRangeInput;
import com.giadinh.apporderbill.reporting.usecase.dto.RevenueOutput;
import com.giadinh.apporderbill.reporting.usecase.dto.RevenueSummaryOutput;

public interface ReportingComponent {
    RevenueOutput getDailyRevenue(GetDailyRevenueInput input);
    RevenueOutput getWeeklyRevenue();
    RevenueOutput getMonthlyRevenue();
    RevenueSummaryOutput getRevenueByDateRange(GetRevenueByDateRangeInput input);
}

