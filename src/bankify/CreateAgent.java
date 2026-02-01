package bankify;

import bankify.dao.AccountDao;
import bankify.dao.AgentDao;

import java.io.Console;
import java.sql.Connection;
import java.util.Scanner;

//Compile this file
//javac -d bin -cp "src/lib/*:src" src/bankify/CreateAgent.java

//Run this file
//java -cp "bin:src/lib/*" bankify.CreateAgent

public class CreateAgent {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Console console = System.console();
        Connection conn = DBConnection.getConnection();

        if (conn == null) {
            System.out.println("Could not connect to database!");
            return;
        }

        AgentDao dao = new AgentDao(conn);

        System.out.println("=== Agent Registration ===");

        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Role: ");
        String role = scanner.nextLine();

        System.out.print("Enter Gender: ");
        String gender = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Enter Address: ");
        String address = scanner.nextLine();


        String password = new String();
        if (console != null) {
            // Read password without echoing characters to screen
            char[] passwordChars = console.readPassword("Enter Password: ");
            password = new String(passwordChars);
        }

        System.out.println("\nCreating agent...");
        Agent created_agent = dao.createAgent(name, role, gender, email, phone, address, password);

        if (created_agent != null) {
            AccountDao accountDao = new AccountDao(conn);
            accountDao.createAccount(created_agent);
            System.out.println("Success! Agent ID: " + created_agent.getAgentId());
        } else {
            System.out.println("Error: Could not save agent to database.");
        }

        scanner.close();
    }
}