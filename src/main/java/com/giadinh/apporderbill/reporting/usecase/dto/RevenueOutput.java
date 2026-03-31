package com.giadinh.apporderbill.reporting.usecase.dto;

public class RevenueOutput {
    private final String period;
    private final long amount;

    public RevenueOutput(String period, long amount) {
        this.period = period;
        this.amount = amount;
    }

    public String getPeriod() { return period; }
    public long getAmount() { return amount; }
}

