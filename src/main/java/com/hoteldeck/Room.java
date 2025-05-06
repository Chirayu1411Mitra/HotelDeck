package com.hoteldeck;

public class Room {
    private int id;
    private String type;
    private double price;
    private boolean isBooked; // Tracks if the room is booked

    // Constructor
    public Room(int id, String type, double price) {
        this.id = id;
        this.type = type;
        this.price = price;
        this.isBooked = false; // Default: not booked
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", isBooked=" + isBooked +
                '}';
    }
}