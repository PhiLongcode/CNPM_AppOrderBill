package com.giadinh.apporderbill.orders.usecase.dto;

public class OpenOrCreateOrderInput {
    private final String tableNumber;
    private final String operator;

    public OpenOrCreateOrderInput(String tableNumber, String operator) {
        this.tableNumber = tableNumber;
        this.operator = operator;
    }

    public String getTableNumber() { return tableNumber; }
    public String getOperator() { return operator; }
}

