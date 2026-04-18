package com.giadinh.apporderbill.customer.model;

/** Menu item redeemable for points (catalog row). */
public class LoyaltyRedeemMenuItem {
    private Long id;
    private int menuItemId;
    private int pointsCost;
    private boolean active;

    public LoyaltyRedeemMenuItem(Long id, int menuItemId, int pointsCost, boolean active) {
        this.id = id;
        this.menuItemId = menuItemId;
        this.pointsCost = pointsCost;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getMenuItemId() { return menuItemId; }
    public void setMenuItemId(int menuItemId) { this.menuItemId = menuItemId; }
    public int getPointsCost() { return pointsCost; }
    public void setPointsCost(int pointsCost) { this.pointsCost = pointsCost; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
