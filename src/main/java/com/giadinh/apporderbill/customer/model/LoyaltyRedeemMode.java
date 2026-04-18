package com.giadinh.apporderbill.customer.model;

/**
 * Loyalty redeem mode for one checkout (exactly one mode per bill).
 */
public enum LoyaltyRedeemMode {
    NONE,
    BILL_DISCOUNT,
    DISH,
    GIFT_NON_MONETARY
}
