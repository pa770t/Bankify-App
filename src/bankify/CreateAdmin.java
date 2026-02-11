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
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        // Suggest valid roles based on your Enum
        System.out.print("Enter Role (STAFF, MANAGER, CEO, SYSTEM): ");
        String role = scanner.nextLine().toUpperCase();

        // Suggest valid genders
        System.out.print("Enter Gender (MALE, FEMALE, LGBTQ+): ");
        String gender = scanner.nextLine().toUpperCase();

        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Enter Address: ");
        String address = scanner.nextLine();

        // 3. Secure Password Handling
        String password = "";
        if (console != null) {
            // Securely read password (masked input)
            char[] passwordChars = console.readPassword("Enter Password: ");
            password = new String(passwordChars);
        } else {
            // Fallback for IDEs where Console might be null
            System.out.print("Enter Password (visible): ");
            password = scanner.nextLine();
        }

        System.out.println("\nCreating admin...");

        // 4. Call DAO to Hash Password and Insert
        // Note: Make sure your AdminDao.createAdmin method exists and accepts these parameters
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
