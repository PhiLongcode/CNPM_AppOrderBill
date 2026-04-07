package com.giadinh.apporderbill.table.usecase.dto;

/** Success payload for transfer-by-table-id flow. Failures use {@link com.giadinh.apporderbill.shared.error.DomainException}. */
public class TransferTableOutput {
    private final String message;

    public TransferTableOutput(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
