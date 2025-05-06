package com.hoteldeck;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;


public class HotelDeck {
    private LinkedList<Customer> customers = new LinkedList<>();
    private Set<Integer> customerIds = new HashSet<>();
    private static LinkedList<Room> rooms = new LinkedList<>();
    private LinkedList<Booking> bookings = new LinkedList<>();
    private int nextBookingId = 1;
    private Scanner scanner = new Scanner(System.in);

    private static final String CUSTOMER_CSV = "customers.csv";
    private static final String ROOM_CSV = "Room.csv";
    private static final String BOOKING_CSV = "Booking.csv";

    public HotelDeck() {
        loadCustomersFromCSV();
        loadRoomsFromCSV();
        loadBookingsFromCSV();
    }

    private void loadCustomersFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMER_CSV))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }
                String[] parts = line.split(",");

                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String email = parts[2].trim();
                    String phoneNumber = parts[3].trim();
                    customers.add(new Customer(id, name , email,phoneNumber ));
                    customerIds.add(id);

            }
        } catch (IOException e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
    }

    private void saveCustomersToCSV() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMER_CSV))) {
            writer.write("id,name,email,phoneNumber\n");
            for (Customer customer : customers) {
                writer.write(customer.getId() + "," + customer.getName() + "," +
                        customer.getEmail() + "," + customer.getPhoneNumber());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving customers: " + e.getMessage());
        }
    }

    private void loadRoomsFromCSV() {
        File file = new File(ROOM_CSV);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header row
                    continue;
                }

                String[] data = line.split(",");
                if (data.length < 4) {
                    System.out.println("Skipping invalid line (not enough data): " + line);
                    continue; // Skip lines that don't have enough data
                }

                try {
                    int id = Integer.parseInt(data[0].trim());
                    String type = data[1].trim();
                    double price = Double.parseDouble(data[2].trim());
                    boolean isBooked = Boolean.parseBoolean(data[3].trim());

                    // Add room to the LinkedList
                    Room room = new Room(id, type, price);
                    room.setBooked(isBooked);
                    rooms.add(room);
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid line (number format error): " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading rooms: " + e.getMessage());
        }
    }

    public static void saveRoomsToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Room.csv"))) {
            writer.write("id,type,price,isBooked\n");  // ✅ Add header

            for (Room room : rooms) {
                writer.write(room.getId() + "," + room.getType() + "," + room.getPrice() + "," + room.isBooked() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving room data to CSV: " + e.getMessage());
        }
    }

    private void loadBookingsFromCSV() {
        File file = new File(BOOKING_CSV);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header row
                    continue;
                }

                String[] data = line.split(",");
                if (data.length < 5) {
                    System.out.println("Skipping invalid line (not enough data): " + line);
                    continue; // Skip lines that don't have enough data
                }

                try {
                    int id = Integer.parseInt(data[0].trim());
                    int roomId = Integer.parseInt(data[1].trim());
                    int customerId = Integer.parseInt(data[2].trim());
                    LocalDate checkInDate = LocalDate.parse(data[3].trim());
                    LocalDate checkOutDate = LocalDate.parse(data[4].trim());

                    Room room = findRoomById(roomId);
                    Customer customer = findCustomerById(customerId);

                    if (room != null && customer != null) {
                        Booking booking = new Booking(id, room, customer, checkInDate, checkOutDate);
                        bookings.add(booking);
                        room.setBooked(true); // Mark room as booked
                    } else {
                        System.out.println("Invalid room or customer for booking: " + line);
                    }
                } catch (NumberFormatException | DateTimeParseException e) {
                    System.out.println("Skipping invalid line (format error): " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading bookings: " + e.getMessage());
        }
    }


    private void saveBookingsToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKING_CSV))) {
            writer.write("id,roomId,customerId,checkInDate,checkOutDate\n");
            for (Booking booking : bookings) {
                writer.write(booking.getId() + "," + booking.getRoom().getId() + "," +
                        booking.getCustomer().getId() + "," + booking.getCheckInDate() + "," +
                        booking.getCheckOutDate());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings: " + e.getMessage());
        }
    }

    public void addCustomer() {
        System.out.print("Enter customer ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Clear buffer

        if (customerIds.contains(id)) {
            System.out.println("Customer ID already exists!");
            return;
        }

        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();

        System.out.print("Enter customer email: ");
        String email = scanner.nextLine();

        System.out.print("Enter customer phone number: ");
        String phoneNumber = scanner.nextLine();

        customers.add(new Customer(id, name, email, phoneNumber)); // Add to LinkedList
        customerIds.add(id); // Add ID to HashSet for duplicate tracking
        saveCustomersToCSV();
        System.out.println("Customer added successfully!");
    }

    public void viewCustomers() {
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        customers.forEach(customer ->
                System.out.println("ID: " + customer.getId() +
                        ", Name: " + customer.getName() +
                        ", Email: " + customer.getEmail() +
                        ", Phone: " + customer.getPhoneNumber()));
    }

    public void updateCustomer() {
        System.out.print("Enter customer ID to update: ");
        int customerId = scanner.nextInt();
        scanner.nextLine(); // Clear the input buffer

        Customer customer = findCustomerById(customerId); // Find the customer by ID
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        // Display existing customer details
        System.out.println("Current Details: ");
        System.out.println("Name: " + customer.getName());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Phone Number: " + customer.getPhoneNumber());

        // Prompt user for updated details
        System.out.print("Enter new name (Leave empty to keep current): ");
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) {
            customer.setName(newName); // Update name if provided
        }

        System.out.print("Enter new email (Leave empty to keep current): ");
        String newEmail = scanner.nextLine();
        if (!newEmail.isEmpty()) {
            customer.setEmail(newEmail); // Update email if provided
        }

        System.out.print("Enter new phone number (Leave empty to keep current): ");
        String newPhoneNumber = scanner.nextLine();
        if (!newPhoneNumber.isEmpty()) {
            customer.setPhoneNumber(newPhoneNumber); // Update phone number if provided
        }

        System.out.println("Customer updated successfully!");
        System.out.println("Updated Details: ");
        System.out.println("Name: " + customer.getName());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Phone Number: " + customer.getPhoneNumber());
    }

    public void deleteCustomer() {
        System.out.print("Enter customer ID to delete: ");
        int id = scanner.nextInt();

        boolean removed = customers.removeIf(customer -> {
            if (customer.getId() == id) {
                customerIds.remove(id);
                return true;
            }
            return false;
        });

        if (removed) {
            System.out.println("Customer deleted successfully.");
        } else {
            System.out.println("Customer not found.");
        }
    }

    private Customer findCustomerById(int id) {
        for (Customer customer : customers) {
            if (customer.getId() == id) {
                return customer;
            }
        }
        return null;
    }

    public void addRoom() {
        System.out.print("Enter room ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Clear buffer

        System.out.print("Enter room type: ");
        String type = scanner.nextLine();

        System.out.print("Enter room price: ");
        double price = scanner.nextDouble();

        // Check for duplicates
        if (rooms.stream().anyMatch(room -> room.getId() == id)) {
            System.out.println("Room ID already exists!");
        } else {
            rooms.add(new Room(id, type, price));
            saveRoomsToCSV();// Add room to LinkedList
            System.out.println("Room added successfully.");
        }
    }

    public void viewRooms() {
        if (rooms.isEmpty()) {
            System.out.println("No rooms found.");
            return;
        }

        rooms.forEach(room -> {
            String status = room.isBooked() ? "Booked" : "Available";
            System.out.println("Room ID: " + room.getId() +
                    ", Type: " + room.getType() +
                    ", Price: " + room.getPrice() +
                    ", Status: " + status);
        });

    }



    public void bookRoom() {
        System.out.print("Enter room ID: ");
        int roomId = scanner.nextInt();
        System.out.print("Enter customer ID: ");
        int customerId = scanner.nextInt();
        scanner.nextLine(); // Clear buffer

        Customer customer = findCustomerById(customerId);
        Room room = findRoomById(roomId);

        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        if (room == null) {
            System.out.println("Room not found.");
            return;
        }

        if (room.isBooked()) {
            System.out.println("Room is already booked!");
            return;
        }

        // Input for check-in and check-out dates
        LocalDate checkInDate = null;
        LocalDate checkOutDate = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            System.out.print("Enter Check-in Date (yyyy-MM-dd): ");
            checkInDate = LocalDate.parse(scanner.nextLine(), formatter);

            System.out.print("Enter Check-out Date (yyyy-MM-dd): ");
            checkOutDate = LocalDate.parse(scanner.nextLine(), formatter);

            // Validate check-out date is after check-in date
            if (!checkOutDate.isAfter(checkInDate)) {
                System.out.println("Check-out date must be after check-in date. Booking failed.");
                return;
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format! Booking failed.");
            return;
        }

        // Create and store booking
        Booking booking = new Booking(nextBookingId++, room, customer, checkInDate, checkOutDate);
        bookings.add(booking); // Add booking to list
        room.setBooked(true);
        saveBookingsToCSV();  // <-- Save booking
        saveRoomsToCSV(); // Mark room as booked
        System.out.println("Room booked successfully!");
    }
    private Room findRoomById(int id) {
        for (Room room : rooms) {
            if (room.getId() == id) {
                return room;
            }
        }
        return null;
    }

    public void cancelBooking() {
        System.out.print("Enter booking ID to cancel: ");
        int bookingId = scanner.nextInt();

        Iterator<Booking> iterator = bookings.iterator();
        while (iterator.hasNext()) {
            Booking booking = iterator.next();
            if (booking.getId() == bookingId) {
                booking.getRoom().setBooked(false); // Mark room as available
                iterator.remove();
                saveBookingsToCSV();  // <-- Save booking
                saveRoomsToCSV(); // Remove booking
                System.out.println("Booking canceled successfully.");
                return;
            }
        }

        System.out.println("Booking not found.");
    }

    public void viewBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        bookings.forEach(booking -> System.out.println(
                "Booking ID: " + booking.getId() +
                        ", Room ID: " + booking.getRoom().getId() +
                        ", Customer ID: " + booking.getCustomer().getId() +
                        ", Check-in Date: " + booking.getCheckInDate() +
                        ", Check-out Date: " + booking.getCheckOutDate()
        ));
    }

    public void exit() {
        saveCustomersToCSV();
        saveRoomsToCSV();
        saveBookingsToCSV();
        System.out.println("Data saved. Exiting...");
    }
}
