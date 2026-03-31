package com.giadinh.apporderbill.reporting.usecase.dto;

import java.time.LocalDate;

public class GetDailyRevenueInput {
    private final LocalDate date;

    public GetDailyRevenueInput(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }
}

