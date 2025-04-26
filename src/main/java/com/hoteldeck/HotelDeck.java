package com.hoteldeck;

import com.hoteldeck.model.*;
import com.hoteldeck.service.HotelService;

import java.util.Scanner;

public class HotelDeck {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HotelService service = new HotelService();

        // Sample data
        service.addRoom(new Room(101, "Single", 1200, true));
        service.addRoom(new Room(102, "Double", 2000, true));
        service.addRoom(new Room(103, "Suite", 5000, true));
        service.addCustomer(new Customer("C001", "Alice", "alice@email.com"));
        service.addCustomer(new Customer("C002", "Bob", "bob@email.com"));

        System.out.println("Welcome to Hotel Deck Management!");

        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Book Room");
            System.out.println("2. Cancel Room Booking");
            System.out.println("3. Search Room by Type");
            System.out.println("4. Sort Bookings by Date");
            System.out.println("5. View Customers");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter Customer ID: ");
                    String custId = sc.nextLine();
                    System.out.print("Enter Room Number: ");
                    int roomNo = sc.nextInt();
                    service.bookRoom(custId, roomNo);
                    break;
                case 2:
                    System.out.print("Enter Room Number to Cancel Booking: ");
                    int cancelRoomNo = sc.nextInt();
                    service.cancelRoom(cancelRoomNo);
                    break;
                case 3:
                    System.out.print("Enter Room Type (Single/Double/Suite): ");
                    String type = sc.nextLine();
                    service.searchRoomByType(type);
                    break;
                case 4:
                    service.sortBookingsByDate();
                    break;
                case 5:
                    service.viewCustomers();
                    break;
                case 6:
                    System.out.println("Thank you for using Hotel Deck!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
