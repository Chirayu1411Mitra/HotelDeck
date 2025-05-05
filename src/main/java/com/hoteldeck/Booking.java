package com.hoteldeck;

import java.time.LocalDate;

public class Booking {
    private int bookingId;
    private int customerId;
    private int roomNumber;
    private LocalDate checkIn;
    private LocalDate checkOut;

    public Booking(int bookingId, int customerId, int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public int getBookingId() { return bookingId; }
    public int getCustomerId() { return customerId; }
    public int getRoomNumber() { return roomNumber; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }

    @Override
    public String toString() {
        return "Booking #" + bookingId + ": Room " + roomNumber + " for"+
                " from " + checkIn + " to " + checkOut;
    }
}
