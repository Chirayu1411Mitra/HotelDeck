package com.hoteldeck;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

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

    // Validation helpers
    private boolean isValidName(String name) {
        return Pattern.matches("[a-zA-Z ]+", name);
    }

    private boolean isValidEmail(String email) {
        return email.contains("@");
    }

    private boolean isValidPhone(String phone) {
        return Pattern.matches("\\d{10}", phone);
    }

    private void loadCustomersFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMER_CSV))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String email = parts[2].trim();
                String phoneNumber = parts[3].trim();
                customers.add(new Customer(id, name, email, phoneNumber));
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
                    isFirstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length < 4) continue;
                int id = Integer.parseInt(data[0].trim());
                String type = data[1].trim();
                double price = Double.parseDouble(data[2].trim());
                boolean isBooked = Boolean.parseBoolean(data[3].trim());
                Room room = new Room(id, type, price);
                room.setBooked(isBooked);
                rooms.add(room);
            }
        } catch (IOException e) {
            System.out.println("Error loading rooms: " + e.getMessage());
        }
    }

    public static void saveRoomsToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Room.csv"))) {
            writer.write("id,type,price,isBooked\n");
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
                    isFirstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length < 5) continue;
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
                    room.setBooked(true);
                }
            }
        } catch (IOException | NumberFormatException | DateTimeParseException e) {
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
        int id;
        while (true) {
            System.out.print("Enter customer ID: ");
            try {
                id = Integer.parseInt(scanner.nextLine());
                if (customerIds.contains(id)) {
                    System.out.println("Customer ID already exists!");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer for ID.");
            }
        }

        String name;
        while (true) {
            System.out.print("Enter customer name: ");
            name = scanner.nextLine();
            if (!isValidName(name)) {
                System.out.println("Invalid name. Only alphabets and spaces allowed.");
            } else {
                break;
            }
        }

        String email;
        while (true) {
            System.out.print("Enter customer email: ");
            email = scanner.nextLine();
            if (!isValidEmail(email)) {
                System.out.println("Invalid email format.");
            } else {
                break;
            }
        }

        String phone;
        while (true) {
            System.out.print("Enter customer phone number: ");
            phone = scanner.nextLine();
            if (!isValidPhone(phone)) {
                System.out.println("Phone number must be 10 digits.");
            } else {
                break;
            }
        }

        customers.add(new Customer(id, name, email, phone));
        customerIds.add(id);
        saveCustomersToCSV();
        System.out.println("Customer added successfully!");
    }


    public void viewCustomers() {
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }
        for (Customer c : customers) {
            System.out.println("ID: " + c.getId() + ", Name: " + c.getName() +
                    ", Email: " + c.getEmail() + ", Phone: " + c.getPhoneNumber());
        }
    }
    public void updateCustomer() {
        System.out.print("Enter customer ID to update: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid integer for ID.");
            return;
        }

        Customer customerToUpdate = null;
        for (Customer customer : customers) {
            if (customer.getId() == id) {
                customerToUpdate = customer;
                break;
            }
        }

        if (customerToUpdate == null) {
            System.out.println("Customer with ID " + id + " not found.");
            return;
        }

        String name;
        while (true) {
            System.out.print("Enter new customer name (current: " + customerToUpdate.getName() + "): ");
            name = scanner.nextLine();
            if (!isValidName(name)) {
                System.out.println("Invalid name. Only alphabets and spaces allowed.");
            } else {
                break;
            }
        }

        String email;
        while (true) {
            System.out.print("Enter new customer email (current: " + customerToUpdate.getEmail() + "): ");
            email = scanner.nextLine();
            if (!isValidEmail(email)) {
                System.out.println("Invalid email format.");
            } else {
                break;
            }
        }

        String phone;
        while (true) {
            System.out.print("Enter new customer phone number (current: " + customerToUpdate.getPhoneNumber() + "): ");
            phone = scanner.nextLine();
            if (!isValidPhone(phone)) {
                System.out.println("Phone number must be 10 digits.");
            } else {
                break;
            }
        }

        customerToUpdate.setName(name);
        customerToUpdate.setEmail(email);
        customerToUpdate.setPhoneNumber(phone);
        saveCustomersToCSV();
        System.out.println("Customer updated successfully!");
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
            saveCustomersToCSV();
            System.out.println("Customer deleted successfully.");
        } else {
            System.out.println("Customer not found.");
        }
    }


    public void addRoom() {
        System.out.print("Enter room ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        if (rooms.stream().anyMatch(room -> room.getId() == id)) {
            System.out.println("Room ID already exists!");
            return;
        }

        System.out.print("Enter room type: ");
        String type = scanner.nextLine();

        System.out.print("Enter room price: ");
        double price = scanner.nextDouble();

        rooms.add(new Room(id, type, price));
        saveRoomsToCSV();
        System.out.println("Room added successfully!");
    }

    public void viewRooms() {
        if (rooms.isEmpty()) {
            System.out.println("No rooms found.");
            return;
        }
        for (Room room : rooms) {
            System.out.println("Room ID: " + room.getId() + ", Type: " + room.getType() +
                    ", Price: " + room.getPrice() + ", Status: " + (room.isBooked() ? "Booked" : "Available"));
        }
    }

    public void bookRoom() {
        // Step 1: Filter and display available rooms
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (!room.isBooked()) { // Check if the room is not booked
                availableRooms.add(room);
            }
        }

        if (availableRooms.isEmpty()) {
            System.out.println("No rooms are currently available for booking.");
            return;
        } else {
            System.out.println("Available rooms:");
            for (Room room : availableRooms) {
                System.out.println("Room ID: " + room.getId() + ", Type: " + room.getType() + ", Price: " + room.getPrice());
            }
        }

        // Step 2: Continue with the booking process
        System.out.print("Enter room ID: ");
        int roomId = scanner.nextInt();
        System.out.print("Enter customer ID: ");
        int customerId = scanner.nextInt();
        scanner.nextLine();

        Customer customer = findCustomerById(customerId);
        Room room = findRoomById(roomId);

        if (customer == null || room == null || room.isBooked()) {
            System.out.println("Invalid booking. Check room/customer ID or room availability.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate checkInDate;
        LocalDate checkOutDate;

        try {
            System.out.print("Enter Check-in Date (yyyy-MM-dd): ");
            checkInDate = LocalDate.parse(scanner.nextLine(), formatter);
            System.out.print("Enter Check-out Date (yyyy-MM-dd): ");
            checkOutDate = LocalDate.parse(scanner.nextLine(), formatter);

            if (!checkOutDate.isAfter(checkInDate)) {
                System.out.println("Check-out must be after check-in.");
                return;
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format.");
            return;
        }

        // Step 3: Create and save the booking
        Booking booking = new Booking(nextBookingId++, room, customer, checkInDate, checkOutDate);
        bookings.add(booking);
        room.setBooked(true);
        saveBookingsToCSV();
        saveRoomsToCSV();

        // Step 4: Calculate and display the billing
        long days = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        double totalCost = days * room.getPrice();
        System.out.println("Room booked successfully!");
        System.out.println("Bill: " + days + " nights × " + room.getPrice() + " = " + totalCost);
    }
    public void cancelBooking() {
        System.out.print("Enter booking ID to cancel: ");
        int bookingId = scanner.nextInt();

        Iterator<Booking> iterator = bookings.iterator();
        while (iterator.hasNext()) {
            Booking b = iterator.next();
            if (b.getId() == bookingId) {
                b.getRoom().setBooked(false);
                iterator.remove();
                saveBookingsToCSV();
                saveRoomsToCSV();
                System.out.println("Booking canceled.");
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

        for (Booking b : bookings) {
            System.out.println("Booking ID: " + b.getId() + ", Room ID: " + b.getRoom().getId() +
                    ", Customer ID: " + b.getCustomer().getId() +
                    ", Check-in: " + b.getCheckInDate() +
                    ", Check-out: " + b.getCheckOutDate());
        }
    }
    public void generateBill() {
        System.out.print("Enter Customer ID for bill generation: ");
        int customerId = -1;
        try {
            customerId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Customer ID format.");
            return;
        }

        Customer customer = findCustomerById(customerId);
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        List<Booking> customerBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getCustomer().getId() == customerId) {
                customerBookings.add(booking);
            }
        }

        if (customerBookings.isEmpty()) {
            System.out.println("No bookings found for this customer.");
            return;
        }

        System.out.println("\n--- BILL SUMMARY ---");
        System.out.println("Customer: " + customer.getName());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Phone: " + customer.getPhoneNumber());
        System.out.println("------------------------");

        double grandTotal = 0.0;

        for (Booking booking : customerBookings) {
            Room room = booking.getRoom();
            LocalDate checkIn = booking.getCheckInDate();
            LocalDate checkOut = booking.getCheckOutDate();
            long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
            double cost = nights * room.getPrice();
            grandTotal += cost;

            System.out.println("Booking ID: " + booking.getId());
            System.out.println("Room ID: " + room.getId());
            System.out.println("Room Type: " + room.getType());
            System.out.println("Price per Night: " + room.getPrice());
            System.out.println("Check-in: " + checkIn);
            System.out.println("Check-out: " + checkOut);
            System.out.println("Nights Stayed: " + nights);
            System.out.println("Subtotal: " + cost);
            System.out.println("------------------------");
        }

        System.out.println("Grand Total: " + grandTotal);
        System.out.println("--- END OF BILL ---\n");
    }

    public void exit() {
        saveCustomersToCSV();
        saveRoomsToCSV();
        saveBookingsToCSV();
        System.out.println("Data saved. Goodbye!");
    }

    private Customer findCustomerById(int id) {
        return customers.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    private Room findRoomById(int id) {
        return rooms.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
    }
}
