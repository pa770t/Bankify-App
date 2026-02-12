package bankify;

import bankify.dao.AdminDao;
import bankify.DBConnection;

import java.io.Console;
import java.sql.Connection;
import java.util.Scanner;

// Compile this file:
// javac -d bin -cp "src/lib/*:src" src/bankify/CreateAdmin.java

// Run this file:
// java -cp "bin:src/lib/*" bankify.CreateAdmin

public class CreateAdmin {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Console console = System.console();

        // 1. Establish Database Connection
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            System.out.println("Error: Could not connect to database!");
            return;
        }

        AdminDao dao = new AdminDao(conn);

        System.out.println("=== Admin Registration ===");

        // 2. Collect Input Data
        String name = null;
        do {
            System.out.print("Enter Full Name: ");
            name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Name must not be empty!");
            }
        } while (name.isEmpty());

        String email = null;
        do {
            System.out.print("Enter Email: ");
            email = scanner.nextLine().trim();

            if (email.isEmpty()) {
                System.out.println("Email must not be empty!");
            } else if (!email.contains("@") || !email.contains(".")) {
                System.out.println("Please enter a valid email address!");
            }
        } while (email.isEmpty() || !email.contains("@") || !email.contains("."));

        // Suggest valid roles based on your Enum
        String role = null;
        do {
            System.out.print("Enter Role (STAFF, MANAGER, CEO, SYSTEM): ");
            role = scanner.nextLine().toUpperCase().trim();

            if (role.isEmpty()) {
                System.out.println("Role must not be empty!");
            } else if (!role.equals("STAFF") && !role.equals("MANAGER") && !role.equals("CEO") && !role.equals(
                    "SYSTEM")) {
                System.out.println("Role must be one of these 'STAFF', 'MANAGER', 'CEO', 'SYSTEM'");
            }
        } while (!role.equals("STAFF") && !role.equals("MANAGER") && !role.equals("CEO") && !role.equals("SYSTEM"));

        // Suggest valid genders
        String gender = null;
        do {
            System.out.print("Enter Gender (MALE, FEMALE, LGBTQ+): ");
            gender = scanner.nextLine().toUpperCase().trim();

            if (gender.isEmpty()) {
                System.out.println("Gender must not be empty!");
            } else if (!gender.equals("MALE") && !gender.equals("FEMALE") && !gender.equals("LGBTQ+")) {
                System.out.println("Gender must be one of these 'MALE', 'FEMALE', 'LGBTQ+'");
            }
        } while (gender.isEmpty() || !gender.equals("MALE") && !gender.equals("FEMALE") && !gender.equals("LGBTQ+"));

        String phone = null;
        do {
            System.out.print("Enter Phone: ");
            phone = scanner.nextLine().trim();

            if (phone.trim().isEmpty()) {
                System.out.println("Phone number must not be empty!");
            } else if (!phone.trim().matches("\\d+")) {
                System.out.println("Phone number must contain digits only!");
            } else if (phone.length() != 10) {
                System.out.println("Phone number must be exactly 10 digits!");
            } else if (!phone.startsWith("9")) {
                System.out.println("Phone number must starts with 9!");
            }
        } while (phone.isEmpty() || !phone.trim().matches("\\d+") || phone.length() != 10 || !phone.startsWith("9"));

        String address = null;
        do {
            System.out.print("Enter Address: ");
            address = scanner.nextLine().trim();

            if (address.isEmpty()) {
                System.out.println("Address must not be empty!");
            }
        } while (address.isEmpty());

        // 3. Secure Password Handling
        String password = "";
        do {
            if (console != null) {
                // Securely read password (masked input)
                char[] passwordChars = console.readPassword("Enter Password: ");
                password = new String(passwordChars).trim();
            } else {
                // Fallback for IDEs where Console might be null
                System.out.print("Enter Password (visible): ");
                password = scanner.nextLine().trim();
            }

            if (password.isEmpty()) {
                System.out.println("Password must not be empty!");
            }
        } while (password.isEmpty());

        System.out.println("\nCreating admin...");
        
        boolean success = dao.createAdmin(name, role, gender, email, phone, address, password);

        if (success) {
            System.out.println("Success! New Admin has been registered.");
        } else {
            System.out.println("Error: Could not save admin to database.");
            System.out.println("Check if the email is already in use.");
        }

        scanner.close();
    }
}
