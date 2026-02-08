package bankify;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

//	private static final String URL =
//			"jdbc:mysql://localhost:3306/bankifyDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
//    private static final String USER = "test"; // change current database username
//    private static final String PASSWORD ="password"; // change current database password

    private static final String URL = "jdbc:mysql://db4free.net:3306/bankifydb?useSSL=false&allowPublicKeyRetrieval" +
            "=true&serverTimezone=UTC";
    private static final String USER = "bankify_root";
    private static final String PASSWORD = "bankifyDB";

    public static Connection getConnection() {
    	System.out.println("getConnection() method CALLED");
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            try (java.sql.Statement stmt = con.createStatement()) {
                stmt.execute("SET time_zone = '+06:30'");
            }
            System.out.println("Database connected successfully");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
}
