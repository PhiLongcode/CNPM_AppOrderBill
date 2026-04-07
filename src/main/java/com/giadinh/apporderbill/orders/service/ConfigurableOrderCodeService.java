package com.giadinh.apporderbill.orders.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Default format: ORDER + ddMMyy + 5-digit sequence.
 * Change pattern pieces here without affecting persisted numeric order id.
 */
public class ConfigurableOrderCodeService implements OrderCodeService {
    private final String prefix;
    private final DateTimeFormatter dateFormatter;
    private final int sequenceDigits;

    public ConfigurableOrderCodeService() {
        this("ORDER", DateTimeFormatter.ofPattern("ddMMyy"), 5);
    }

    public ConfigurableOrderCodeService(String prefix, DateTimeFormatter dateFormatter, int sequenceDigits) {
        this.prefix = (prefix == null || prefix.isBlank()) ? "ORDER" : prefix;
        this.dateFormatter = dateFormatter == null ? DateTimeFormatter.ofPattern("ddMMyy") : dateFormatter;
        this.sequenceDigits = Math.max(1, sequenceDigits);
    }

    @Override
    public String generate(String orderId, LocalDateTime orderDate) {
        if (orderId == null || orderId.isBlank()) {
            return "";
        }
        LocalDateTime dt = orderDate == null ? LocalDateTime.now() : orderDate;
        String datePart = dt.format(dateFormatter);
        long numeric;
        try {
            numeric = Math.abs(Long.parseLong(orderId));
        } catch (Exception e) {
            numeric = Math.abs(orderId.hashCode());
        }
        long mod = (long) Math.pow(10, sequenceDigits);
        long seq = mod > 0 ? numeric % mod : numeric;
        String seqPart = String.format("%0" + sequenceDigits + "d", seq);
        return prefix + datePart + seqPart;
    }
}
