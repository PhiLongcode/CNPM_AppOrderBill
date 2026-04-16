package com.giadinh.apporderbill.customer.model;

/**
 * Cấu hình quy tắc tích điểm khách hàng.
 * Đọc từ bảng settings trong DB để có thể thay đổi mà không cần sửa code.
 *
 * Mặc định:
 *   - Tích: mỗi 10.000đ → 1 điểm (earnUnitAmount=10000, pointsPerUnit=1)
 *   - Đổi: 100 điểm → 5.000đ giảm giá (redeemPoints=100, redeemValue=5000)
 */
public class LoyaltyConfig {

    /** Số tiền (VNĐ) tương ứng với 1 đơn vị điểm khi tích */
    private long earnUnitAmount;
    /** Số điểm nhận được cho mỗi đơn vị (earnUnitAmount) */
    private int pointsPerUnit;
    /** Số điểm cần để đổi 1 phần thưởng */
    private int redeemPointsRequired;
    /** Giá trị (VNĐ) giảm giá khi đổi đủ redeemPointsRequired điểm */
    private long redeemValue;

    public LoyaltyConfig(long earnUnitAmount, int pointsPerUnit, int redeemPointsRequired, long redeemValue) {
        this.earnUnitAmount = earnUnitAmount;
        this.pointsPerUnit = pointsPerUnit;
        this.redeemPointsRequired = redeemPointsRequired;
        this.redeemValue = redeemValue;
    }

    /** Tính số điểm kiếm được từ số tiền đã trả */
    public int calcEarnedPoints(long finalAmount) {
        if (earnUnitAmount <= 0) return 0;
        return (int) ((finalAmount / earnUnitAmount) * pointsPerUnit);
    }

    /** Tính số tiền giảm giá tương ứng với số điểm muốn dùng */
    public long calcRedeemDiscount(int pointsToUse) {
        if (redeemPointsRequired <= 0 || pointsToUse <= 0) return 0L;
        long units = pointsToUse / redeemPointsRequired;
        return units * redeemValue;
    }

    /** Số điểm tối đa có thể dùng để không vượt quá maxDiscount */
    public int maxPointsForDiscount(long maxDiscount) {
        if (redeemPointsRequired <= 0 || redeemValue <= 0) return 0;
        long units = maxDiscount / redeemValue;
        return (int) (units * redeemPointsRequired);
    }

    public long getEarnUnitAmount() { return earnUnitAmount; }
    public int getPointsPerUnit() { return pointsPerUnit; }
    public int getRedeemPointsRequired() { return redeemPointsRequired; }
    public long getRedeemValue() { return redeemValue; }

    public void setEarnUnitAmount(long earnUnitAmount) { this.earnUnitAmount = earnUnitAmount; }
    public void setPointsPerUnit(int pointsPerUnit) { this.pointsPerUnit = pointsPerUnit; }
    public void setRedeemPointsRequired(int redeemPointsRequired) { this.redeemPointsRequired = redeemPointsRequired; }
    public void setRedeemValue(long redeemValue) { this.redeemValue = redeemValue; }

    /** Cấu hình mặc định theo yêu cầu nghiệp vụ: 10.000đ/điểm, 100 điểm = 5.000đ */
    public static LoyaltyConfig defaults() {
        return new LoyaltyConfig(10_000L, 1, 100, 5_000L);
    }
}
