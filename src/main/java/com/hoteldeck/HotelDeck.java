package com.hoteldeck;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class HotelDeck {
    private HashMap<Integer, Customer> customers = new HashMap<>();
    private HashMap<Integer, Room> rooms = new HashMap<>();
    private ArrayList<Booking> bookings = new ArrayList<>();
    private int nextBookingId = 1;
    private Scanner scanner = new Scanner(System.in);

    private static final String CUSTOMER_CSV = "customers.csv";

    public HotelDeck() {
        loadCustomersFromCSV();
    }

    // Load customers from customers.csv
    private void loadCustomersFromCSV() {
        File file = new File(CUSTOMER_CSV);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length < 4) continue;

                int id = Integer.parseInt(data[0].trim());
                String name = data[1].trim();
                String email = data[2].trim();
                String phone = data[3].trim();
                customers.put(id, new Customer(id, name, email, phone));
            }
        } catch (IOException e) {
            System.out.println("Error loading customers from CSV: " + e.getMessage());
        }
    }

    // Save customers to customers.csv
    private void saveCustomersToCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CUSTOMER_CSV))) {
            bw.write("id,name,email,phone");
            bw.newLine();
            for (Customer c : customers.values()) {
                String line = c.getId() + "," + c.getName() + "," + c.getEmail() + "," + c.getPhone();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving customers to CSV: " + e.getMessage());
        }
    }

    // Add a customer
    public void addCustomer() {
        System.out.print("Enter Customer ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
// Error h
        if (customers.containsKey(id)) {
            System.out.println("Customer already exists.");
            return;
        }

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine();

        customers.put(id, new Customer(id, name, email, phone));
        saveCustomersToCSV();
        System.out.println("Customer added successfully!");
    }

    // View customers
    public void viewCustomers() {
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }
        for (Customer customer : customers.values()) {
            System.out.println(customer);
        }
    }

    // Update a customer
    public void updateCustomer() {
        System.out.print("Enter Customer ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        if (!customers.containsKey(id)) {
            System.out.println("Customer not found.");
            return;
        }

        Customer c = customers.get(id);
        System.out.print("Enter new Name: ");
        c.setName(scanner.nextLine());
        System.out.print("Enter new Email: ");
        c.setEmail(scanner.nextLine());
        System.out.print("Enter new Phone: ");
        c.setPhone(scanner.nextLine());

        saveCustomersToCSV();
        System.out.println("Customer updated!");
    }

    // Delete a customer
    public void deleteCustomer() {
        System.out.print("Enter Customer ID to delete: ");
        int id = scanner.nextInt();

        if (customers.remove(id) != null) {
            saveCustomersToCSV();
            System.out.println("Customer deleted.");
        } else {
            System.out.println("Customer not found.");
        }
    }

    // Add a room
    public void addRoom() {
        System.out.print("Enter Room Number: ");
        int roomNum = scanner.nextInt();
        scanner.nextLine();

        if (rooms.containsKey(roomNum)) {
            System.out.println("Room already exists.");
            return;
        }

        System.out.print("Enter Room Type: ");
        String type = scanner.nextLine();
        System.out.print("Enter Price per Night: ");
        double price = scanner.nextDouble();

        rooms.put(roomNum, new Room(roomNum, type, price));
        System.out.println("Room added successfully!");
    }

    // View rooms
    public void viewRooms() {
        if (rooms.isEmpty()) {
            System.out.println("No rooms found.");
            return;
        }

        for (Room room : rooms.values()) {
            System.out.println(room);
        }
    }

    // Book a room
    public void bookRoom() {
        System.out.print("Enter Customer ID: ");
        int customerId = scanner.nextInt();
        System.out.print("Enter Room Number: ");
        int roomNum = scanner.nextInt();

        if (!customers.containsKey(customerId)) {
            System.out.println("Customer does not exist.");
            return;
        }

        Room room = rooms.get(roomNum);
        if (room == null) {
            System.out.println("Room does not exist.");
            return;
        }

        if (room.isBooked()) {
            System.out.println("Room is already booked.");
            return;
        }

        System.out.print("Enter Check-in Date (YYYY-MM-DD): ");
        LocalDate checkIn = LocalDate.parse(scanner.next());
        System.out.print("Enter Check-out Date (YYYY-MM-DD): ");
        LocalDate checkOut = LocalDate.parse(scanner.next());

        room.setBooked(true);
        bookings.add(new Booking(nextBookingId++, customerId, roomNum, checkIn, checkOut));
        System.out.println("Room booked successfully!");
    }

    // Cancel a booking
    public void cancelBooking() {
        System.out.print("Enter Booking ID to cancel: ");
        int id = scanner.nextInt();
        Booking toRemove = null;

        for (Booking booking : bookings) {
            if (booking.getBookingId() == id) {
                rooms.get(booking.getRoomNumber()).setBooked(false);
                toRemove = booking;
                break;
            }
        }

        if (toRemove != null) {
            bookings.remove(toRemove);
            System.out.println("Booking canceled.");
        } else {
            System.out.println("Booking not found.");
        }
    }

    // View bookings
    public void viewBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings yet.");
            return;
        }

        for (Booking booking : bookings) {
            System.out.println(booking);
        }
    }
}

