package com.hoteldeck;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        HotelDeck hotel = new HotelDeck();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Hotel Deck Menu ---");
            System.out.println("1. Add Customer");
            System.out.println("2. View Customers");
            System.out.println("3. Update Customer");
            System.out.println("4. Delete Customer");
            System.out.println("5. Add Room");
            System.out.println("6. View Rooms");
            System.out.println("7. Book Room");
            System.out.println("8. Cancel Booking");
            System.out.println("9. View Bookings");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1 -> hotel.addCustomer();
                case 2 -> hotel.viewCustomers();
                case 3 -> hotel.updateCustomer();
                case 4 -> hotel.deleteCustomer();
                case 5 -> hotel.addRoom();
                case 6 -> hotel.viewRooms();
                case 7 -> hotel.bookRoom();
                case 8 -> hotel.cancelBooking();
                case 9 -> hotel.viewBookings();
                case 0 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }
}
