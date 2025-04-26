package com.hoteldeck.service;

import com.hoteldeck.model.*;

import java.time.LocalDate;
import java.util.*;

public class HotelService {
    private Map<Integer, Room> rooms = new HashMap<>();
    private List<Booking> bookings = new ArrayList<>();
    private Map<String, Customer> customers = new TreeMap<>(); // TreeMap for sorted customers

    public void addRoom(Room room) {
        rooms.put(room.getNumber(), room);
    }

    public void addCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    public void bookRoom(String customerId, int roomNumber) {
        Room room = rooms.get(roomNumber);
        if (room != null && room.isAvailable()) {
            Customer customer = customers.get(customerId);
            if (customer != null) {
                Booking booking = new Booking(customer, room, LocalDate.now());
                bookings.add(booking);
                room.setAvailable(false);
                System.out.println("Room booked successfully for " + customer.getName());
            } else {
                System.out.println("Customer ID not found.");
            }
        } else {
            System.out.println("Room not available or does not exist.");
        }
    }

    public void cancelRoom(int roomNumber) {
        Room room = rooms.get(roomNumber);
        if (room != null && !room.isAvailable()) {
            room.setAvailable(true);
            bookings.removeIf(b -> b.getRoom().getNumber() == roomNumber);
            System.out.println("Room booking cancelled successfully.");
        } else {
            System.out.println("Room not booked or does not exist.");
        }
    }

    public void searchRoomByType(String type) {
        boolean found = false;
        for (Room room : rooms.values()) {
            if (room.getType().equalsIgnoreCase(type) && room.isAvailable()) {
                System.out.println("Room No: " + room.getNumber() + ", Price: " + room.getPrice());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No available rooms of type " + type + ".");
        }
    }

    public void sortBookingsByDate() {
        bookings.sort(Comparator.comparing(Booking::getBookingDate));
        if (bookings.isEmpty()) {
            System.out.println("No bookings yet.");
        } else {
            System.out.println("--- Sorted Bookings ---");
            for (Booking booking : bookings) {
                System.out.println("Customer: " + booking.getCustomer().getName() +
                        ", Room: " + booking.getRoom().getNumber() +
                        ", Date: " + booking.getBookingDate());
            }
        }
    }

    public void viewCustomers() {
        if (customers.isEmpty()) {
            System.out.println("No customers registered yet.");
        } else {
            System.out.println("--- Customer List ---");
            for (Customer customer : customers.values()) {
                System.out.println("ID: " + customer.getId() + ", Name: " + customer.getName() +
                        ", Email: " + customer.getEmail());
            }
        }
    }
}
