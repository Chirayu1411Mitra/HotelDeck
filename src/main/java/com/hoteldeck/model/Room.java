package com.hoteldeck.model;

public class Room {
    private int number;
    private String type;
    private double price;
    private boolean isAvailable;

    public Room(int number, String type, double price, boolean isAvailable) {
        this.number = number;
        this.type = type;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    public int getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
