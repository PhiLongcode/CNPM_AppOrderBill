package com.giadinh.apporderbill.reporting.usecase.dto;

import java.util.List;

public class RevenueSummaryOutput {
    private final long totalRevenue;
    private final List<RevenueOutput> items;

    public RevenueSummaryOutput(long totalRevenue, List<RevenueOutput> items) {
        this.totalRevenue = totalRevenue;
        this.items = items;
    }

    public long getTotalRevenue() { return totalRevenue; }
    public List<RevenueOutput> getItems() { return items; }
}

