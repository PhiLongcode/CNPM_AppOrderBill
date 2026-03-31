package com.giadinh.apporderbill.reporting.usecase.dto;

import java.time.LocalDateTime;

public class GetRevenueByDateRangeInput {
    private final LocalDateTime start;
    private final LocalDateTime end;

    public GetRevenueByDateRangeInput(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }
}

