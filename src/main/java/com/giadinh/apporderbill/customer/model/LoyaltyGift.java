package com.giadinh.apporderbill.customer.model;

/** Non-monetary gift redeemable for points. */
public class LoyaltyGift {
    private Long id;
    private String name;
    private int pointsCost;
    private boolean active;

    public LoyaltyGift(Long id, String name, int pointsCost, boolean active) {
        this.id = id;
        this.name = name;
        this.pointsCost = pointsCost;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPointsCost() { return pointsCost; }
    public void setPointsCost(int pointsCost) { this.pointsCost = pointsCost; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
