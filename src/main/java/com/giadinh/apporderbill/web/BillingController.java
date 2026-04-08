package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.billing.BillingComponent;
import com.giadinh.apporderbill.billing.usecase.dto.*;
import com.giadinh.apporderbill.web.security.ApiAuthorizationService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller cho Billing feature.
 */
@RestController
@RequestMapping("/api/v1/billing")
@Tag(name = "Billing", description = "Payments and receipt operations")
public class BillingController {

    private final BillingComponent billingComponent;
    private final ApiAuthorizationService authorizationService;

    public BillingController(BillingComponent billingComponent, ApiAuthorizationService authorizationService) {
        this.billingComponent = billingComponent;
        this.authorizationService = authorizationService;
    }

    @GetMapping("/payments/today")
    public ResponseEntity<List<PaymentSummaryOutput>> getTodayPayments(@RequestHeader(value = "X-Username", required = false) String username) {
        authorizationService.requireView(username, "Checkout Order");
        return ResponseEntity.ok(billingComponent.getTodayPayments());
    }

    @GetMapping("/payments/date/{date}")
    public ResponseEntity<List<PaymentSummaryOutput>> getPaymentsByDate(@RequestHeader(value = "X-Username", required = false) String username,
                                                                        @PathVariable String date) {
        authorizationService.requireView(username, "Checkout Order");
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.ok(billingComponent.getPaymentsByDate(localDate));
    }

    @GetMapping("/payments/range")
    public ResponseEntity<List<PaymentSummaryOutput>> getPaymentsByDateRange(
            @RequestHeader(value = "X-Username", required = false) String username,
            @RequestParam String start, @RequestParam String end) {
        authorizationService.requireView(username, "Checkout Order");
        LocalDateTime startDt = LocalDateTime.parse(start);
        LocalDateTime endDt = LocalDateTime.parse(end);
        return ResponseEntity.ok(billingComponent.getPaymentsByDateRange(startDt, endDt));
    }

    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<PaymentDetailOutput> getPaymentDetail(@RequestHeader(value = "X-Username", required = false) String username,
                                                                @PathVariable Long paymentId) {
        authorizationService.requireView(username, "Checkout Order");
        return ResponseEntity.ok(billingComponent.getPaymentDetail(paymentId));
    }

    @DeleteMapping("/payments/range")
    public ResponseEntity<Void> deletePaymentsByDateRange(
            @RequestHeader(value = "X-Username", required = false) String username,
            @RequestParam String start, @RequestParam String end) {
        authorizationService.requireOperate(username, "Checkout Order");
        LocalDateTime startDt = LocalDateTime.parse(start);
        LocalDateTime endDt = LocalDateTime.parse(end);
        billingComponent.deletePaymentsByDateRange(startDt, endDt);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/receipt/print")
    public ResponseEntity<PrintReceiptOutput> printReceipt(@RequestHeader(value = "X-Username", required = false) String username,
                                                           @RequestBody PrintReceiptInput input) {
        authorizationService.requireView(username, "Print Receipt");
        return ResponseEntity.ok(billingComponent.printReceipt(input));
    }

    @PostMapping("/receipt/reprint/{paymentId}")
    public ResponseEntity<PrintReceiptOutput> reprintReceipt(@RequestHeader(value = "X-Username", required = false) String username,
                                                             @PathVariable Long paymentId) {
        authorizationService.requireView(username, "Print Receipt");
        return ResponseEntity.ok(billingComponent.reprintReceipt(paymentId));
    }
}
