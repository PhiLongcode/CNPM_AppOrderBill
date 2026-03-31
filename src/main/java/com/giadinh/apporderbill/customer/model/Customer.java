package com.giadinh.apporderbill.customer.model;

public class Customer {
    private Long id;
    private String name;
    private String phone;
    private int points;

    public Customer(Long id, String name, String phone, int points) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.points = points;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public int getPoints() { return points; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPoints(int points) { this.points = points; }
}

