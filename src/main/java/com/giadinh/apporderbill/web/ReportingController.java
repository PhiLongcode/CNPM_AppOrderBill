package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.reporting.ReportingComponent;
import com.giadinh.apporderbill.reporting.usecase.dto.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST Controller cho Reporting feature.
 */
@RestController
@RequestMapping("/api/reporting")
public class ReportingController {

    private final ReportingComponent reportingComponent;

    public ReportingController(ReportingComponent reportingComponent) {
        this.reportingComponent = reportingComponent;
    }

    @GetMapping("/revenue/daily")
    public ResponseEntity<RevenueOutput> getDailyRevenue(@RequestParam(required = false) String date) {
        LocalDate targetDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        GetDailyRevenueInput input = new GetDailyRevenueInput(targetDate);
        return ResponseEntity.ok(reportingComponent.getDailyRevenue(input));
    }

    @GetMapping("/revenue/weekly")
    public ResponseEntity<RevenueOutput> getWeeklyRevenue() {
        return ResponseEntity.ok(reportingComponent.getWeeklyRevenue());
    }

    @GetMapping("/revenue/monthly")
    public ResponseEntity<RevenueOutput> getMonthlyRevenue() {
        return ResponseEntity.ok(reportingComponent.getMonthlyRevenue());
    }

    @GetMapping("/revenue/range")
    public ResponseEntity<RevenueSummaryOutput> getRevenueByDateRange(
            @RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        GetRevenueByDateRangeInput input = new GetRevenueByDateRangeInput(startDate, endDate);
        return ResponseEntity.ok(reportingComponent.getRevenueByDateRange(input));
    }
}
