package bankify;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

//	private static final String URL =
//			"jdbc:mysql://localhost:3306/bankifyDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
//    private static final String USER = "test"; // change current database username
//    private static final String PASSWORD ="password"; // change current database password

    private static final String URL = "jdbc:mysql://sql.freedb.tech:3306/freedb_bankifyDB?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String USER = "freedb_bankify_root"; // change current database username
    private static final String PASSWORD ="R32nF5&4GsSHM?a"; // change current database password

    public static Connection getConnection() {
    	System.out.println("getConnection() method CALLED");
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
}
