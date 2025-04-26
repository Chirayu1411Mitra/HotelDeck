package com.hoteldeck.model;

import java.time.LocalDate;

public class Booking {
    private Customer customer;
    private Room room;
    private LocalDate bookingDate;

    public Booking(Customer customer, Room room, LocalDate bookingDate) {
        this.customer = customer;
        this.room = room;
        this.bookingDate = bookingDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }
}
