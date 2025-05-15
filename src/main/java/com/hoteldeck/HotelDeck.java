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
        customers = mergeSortCustomers(customers);

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

    public void saveRoomsToCSV() {
        rooms = mergeSortRooms(rooms);

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
                    if (id >= nextBookingId) {
                        nextBookingId = id + 1;
                    }
                }
            }
        } catch (IOException | NumberFormatException | DateTimeParseException e) {
            System.out.println("Error loading bookings: " + e.getMessage());
        }
    }

    private void saveBookingsToCSV() {
        bookings.sort(Comparator.comparingInt(Booking::getId));

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
        customers = mergeSortCustomers(customers);

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

        Customer customerToUpdate = findCustomerById(id);

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
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid integer for ID.");
            return;
        }

        Customer customerToRemove = findCustomerById(id);
        if (customerToRemove != null) {
            customers.remove(customerToRemove);
            customerIds.remove(id);
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

        String type;
        while (true) {
            System.out.print("Enter room type (Single/Double/Deluxe): ");
            type = scanner.nextLine().trim();
            if (type.equalsIgnoreCase("Single") || type.equalsIgnoreCase("Double") || type.equalsIgnoreCase("Deluxe")) {
                // Normalize input (e.g., "single" → "Single")
                type = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
                break;
            } else {
                System.out.println("Invalid room type. Please enter either 'Single', 'Double', or 'Deluxe'.");
            }
        }

        double price;
        while (true) {
            System.out.print("Enter room price: ");
            try {
                price = Double.parseDouble(scanner.nextLine());
                if (price <= 0) {
                    System.out.println("Price must be positive.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid price. Please enter a valid number.");
            }
        }

        rooms.add(new Room(id, type, price));
        saveRoomsToCSV();
        System.out.println("Room added successfully!");
    }


    public void viewRooms() {
        if (rooms.isEmpty()) {
            System.out.println("No rooms found.");
            return;
        }
        rooms = mergeSortRooms(rooms);

        for (Room room : rooms) {
            System.out.println("Room ID: " + room.getId() + ", Type: " + room.getType() +
                    ", Price: " + room.getPrice() + ", Status: " + (room.isBooked() ? "Booked" : "Available"));
        }
    }

    public void deleteRoomById() {
        System.out.print("Enter Room ID to delete: ");
        String idStr = scanner.nextLine().trim();

        try {
            int id = Integer.parseInt(idStr);

            Room roomToRemove = findRoomById(id);
            if (roomToRemove != null) {
                rooms.remove(roomToRemove);
                System.out.println("Room deleted successfully.");
                rooms = mergeSortRooms(rooms);
                saveRoomsToCSV();
            } else {
                System.out.println("Room with ID " + id + " not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Room ID format.");
        }
    }


    public void bookRoom() {
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (!room.isBooked()) {
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

        Booking booking = new Booking(nextBookingId++, room, customer, checkInDate, checkOutDate);
        bookings.add(booking);
        room.setBooked(true);
        saveBookingsToCSV();
        saveRoomsToCSV();

        long days = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        double totalCost = days * room.getPrice();
        System.out.println("Room booked successfully!");
        System.out.println("Bill: " + days + " nights " + room.getPrice() + " = " + totalCost);
    }

    public void cancelBooking() {
        System.out.print("Enter booking ID to cancel: ");
        int bookingId = scanner.nextInt();
        scanner.nextLine();

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
        bookings = mergeSortBookings(bookings);

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

        boolean hasBookings = false;
        double totalBill = 0;
        for (Booking b : bookings) {
            if (b.getCustomer().getId() == customerId) {
                long days = java.time.temporal.ChronoUnit.DAYS.between(b.getCheckInDate(), b.getCheckOutDate());
                double cost = days * b.getRoom().getPrice();
                System.out.println("Booking ID: " + b.getId() + ", Room ID: " + b.getRoom().getId() +
                        ", Stay: " + days + " nights, Cost: " + cost);
                totalBill += cost;
                hasBookings = true;
            }
        }

        if (!hasBookings) {
            System.out.println("No bookings found for this customer.");
        } else {
            System.out.println("Total bill for Customer ID " + customerId + ": " + totalBill);
        }
    }

    public void exit() {
        saveCustomersToCSV();
        saveRoomsToCSV();
        saveBookingsToCSV();
        System.out.println("Data saved. Goodbye!");
    }

    private Customer findCustomerById(int id) {
        customers = mergeSortCustomers(customers);
        int left = 0;
        int right = customers.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Customer midCustomer = customers.get(mid);

            if (midCustomer.getId() == id) {
                return midCustomer;
            } else if (midCustomer.getId() < id) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null;
    }

    private Room findRoomById(int id) {
        rooms = mergeSortRooms(rooms);
        int left = 0;
        int right = rooms.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Room midRoom = rooms.get(mid);

            if (midRoom.getId() == id) {
                return midRoom;
            } else if (midRoom.getId() < id) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null;
    }

    // Merge sort for customers
    private LinkedList<Customer> mergeSortCustomers(LinkedList<Customer> list) {
        if (list.size() <= 1) {
            return list;
        }

        LinkedList<Customer> left = new LinkedList<>();
        LinkedList<Customer> right = new LinkedList<>();
        int middle = list.size() / 2;

        int count = 0;
        for (Customer c : list) {
            if (count < middle) {
                left.add(c);
            } else {
                right.add(c);
            }
            count++;
        }

        left = mergeSortCustomers(left);
        right = mergeSortCustomers(right);

        return mergeCustomers(left, right);
    }

    private LinkedList<Customer> mergeCustomers(LinkedList<Customer> left, LinkedList<Customer> right) {
        LinkedList<Customer> result = new LinkedList<>();
        while (!left.isEmpty() && !right.isEmpty()) {
            if (left.getFirst().getId() <= right.getFirst().getId()) {
                result.add(left.removeFirst());
            } else {
                result.add(right.removeFirst());
            }
        }
        result.addAll(left);
        result.addAll(right);
        return result;
    }

    // Merge sort for rooms
    private LinkedList<Room> mergeSortRooms(LinkedList<Room> list) {
        if (list.size() <= 1) {
            return list;
        }

        LinkedList<Room> left = new LinkedList<>();
        LinkedList<Room> right = new LinkedList<>();
        int middle = list.size() / 2;

        int count = 0;
        for (Room r : list) {
            if (count < middle) {
                left.add(r);
            } else {
                right.add(r);
            }
            count++;
        }

        left = mergeSortRooms(left);
        right = mergeSortRooms(right);

        return mergeRooms(left, right);
    }

    private LinkedList<Room> mergeRooms(LinkedList<Room> left, LinkedList<Room> right) {
        LinkedList<Room> result = new LinkedList<>();
        while (!left.isEmpty() && !right.isEmpty()) {
            if (left.getFirst().getId() <= right.getFirst().getId()) {
                result.add(left.removeFirst());
            } else {
                result.add(right.removeFirst());
            }
        }
        result.addAll(left);
        result.addAll(right);
        return result;
    }
    private LinkedList<Booking> mergeSortBookings(LinkedList<Booking> list) {
        if (list.size() <= 1) {
            return list;
        }

        LinkedList<Booking> left = new LinkedList<>();
        LinkedList<Booking> right = new LinkedList<>();
        int middle = list.size() / 2;

        int count = 0;
        for (Booking b : list) {
            if (count < middle) {
                left.add(b);
            } else {
                right.add(b);
            }
            count++;
        }

        left = mergeSortBookings(left);
        right = mergeSortBookings(right);

        return mergeBookings(left, right);
    }

    private LinkedList<Booking> mergeBookings(LinkedList<Booking> left, LinkedList<Booking> right) {
        LinkedList<Booking> result = new LinkedList<>();
        while (!left.isEmpty() && !right.isEmpty()) {
            if (left.getFirst().getId() <= right.getFirst().getId()) {
                result.add(left.removeFirst());
            } else {
                result.add(right.removeFirst());
            }
        }
        result.addAll(left);
        result.addAll(right);
        return result;
    }


}

