package com.giadinh.apporderbill.orders.usecase.dto;

public class CalculateOrderTotalOutput {
    private final long subtotal;
    private final long discountAmount;
    /** Tiền sau giảm giá đơn hàng, làm cơ sở tính VAT (trước thuế). */
    private final long netAmountBeforeVat;
    private final double vatPercent;
    private final long vatAmount;
    /** Tiền sau VAT, trước giảm đổi điểm (tổng phải trả nếu không đổi điểm). */
    private final long finalAmount;

    public CalculateOrderTotalOutput(long subtotal, long discountAmount, long netAmountBeforeVat,
            double vatPercent, long vatAmount, long finalAmount) {
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.netAmountBeforeVat = netAmountBeforeVat;
        this.vatPercent = vatPercent;
        this.vatAmount = vatAmount;
        this.finalAmount = finalAmount;
    }

    public long getSubtotal() { return subtotal; }
    public long getDiscountAmount() { return discountAmount; }
    public long getNetAmountBeforeVat() { return netAmountBeforeVat; }
    public double getVatPercent() { return vatPercent; }
    public long getVatAmount() { return vatAmount; }
    public long getFinalAmount() { return finalAmount; }
}

