package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.reporting.ReportingComponent;
import com.giadinh.apporderbill.reporting.usecase.dto.*;
import com.giadinh.apporderbill.web.security.ApiAuthorizationService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST Controller cho Reporting feature.
 */
@RestController
@RequestMapping("/api/v1/reporting")
@Tag(name = "Reporting", description = "Revenue and reporting APIs")
public class ReportingController {

    private final ReportingComponent reportingComponent;
    private final ApiAuthorizationService authorizationService;

    public ReportingController(ReportingComponent reportingComponent, ApiAuthorizationService authorizationService) {
        this.reportingComponent = reportingComponent;
        this.authorizationService = authorizationService;
    }

    @GetMapping("/revenue/daily")
    public ResponseEntity<RevenueOutput> getDailyRevenue(@RequestHeader(value = "X-Username", required = false) String username,
                                                         @RequestParam(required = false) String date) {
        authorizationService.requireView(username, "View Reports");
        LocalDate targetDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        GetDailyRevenueInput input = new GetDailyRevenueInput(targetDate);
        return ResponseEntity.ok(reportingComponent.getDailyRevenue(input));
    }

    @GetMapping("/revenue/weekly")
    public ResponseEntity<RevenueOutput> getWeeklyRevenue(@RequestHeader(value = "X-Username", required = false) String username) {
        authorizationService.requireView(username, "View Reports");
        return ResponseEntity.ok(reportingComponent.getWeeklyRevenue());
    }

    @GetMapping("/revenue/monthly")
    public ResponseEntity<RevenueOutput> getMonthlyRevenue(@RequestHeader(value = "X-Username", required = false) String username) {
        authorizationService.requireView(username, "View Reports");
        return ResponseEntity.ok(reportingComponent.getMonthlyRevenue());
    }

    @GetMapping("/revenue/range")
    public ResponseEntity<RevenueSummaryOutput> getRevenueByDateRange(
            @RequestHeader(value = "X-Username", required = false) String username,
            @RequestParam String start, @RequestParam String end) {
        authorizationService.requireView(username, "View Reports");
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        GetRevenueByDateRangeInput input = new GetRevenueByDateRangeInput(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        return ResponseEntity.ok(reportingComponent.getRevenueByDateRange(input));
    }
}
