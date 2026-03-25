package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.billing.BillingComponent;
import com.giadinh.apporderbill.billing.usecase.dto.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller cho Billing feature.
 */
@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingComponent billingComponent;

    public BillingController(BillingComponent billingComponent) {
        this.billingComponent = billingComponent;
    }

    @GetMapping("/payments/today")
    public ResponseEntity<List<PaymentSummaryOutput>> getTodayPayments() {
        return ResponseEntity.ok(billingComponent.getTodayPayments());
    }

    @GetMapping("/payments/date/{date}")
    public ResponseEntity<List<PaymentSummaryOutput>> getPaymentsByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.ok(billingComponent.getPaymentsByDate(localDate));
    }

    @GetMapping("/payments/range")
    public ResponseEntity<List<PaymentSummaryOutput>> getPaymentsByDateRange(
            @RequestParam String start, @RequestParam String end) {
        LocalDateTime startDt = LocalDateTime.parse(start);
        LocalDateTime endDt = LocalDateTime.parse(end);
        return ResponseEntity.ok(billingComponent.getPaymentsByDateRange(startDt, endDt));
    }

    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<PaymentDetailOutput> getPaymentDetail(@PathVariable Long paymentId) {
        return ResponseEntity.ok(billingComponent.getPaymentDetail(paymentId));
    }

    @DeleteMapping("/payments/range")
    public ResponseEntity<Void> deletePaymentsByDateRange(
            @RequestParam String start, @RequestParam String end) {
        LocalDateTime startDt = LocalDateTime.parse(start);
        LocalDateTime endDt = LocalDateTime.parse(end);
        billingComponent.deletePaymentsByDateRange(startDt, endDt);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/receipt/print")
    public ResponseEntity<PrintReceiptOutput> printReceipt(@RequestBody PrintReceiptInput input) {
        return ResponseEntity.ok(billingComponent.printReceipt(input));
    }

    @PostMapping("/receipt/reprint/{paymentId}")
    public ResponseEntity<PrintReceiptOutput> reprintReceipt(@PathVariable Long paymentId) {
        return ResponseEntity.ok(billingComponent.reprintReceipt(paymentId));
    }
}
