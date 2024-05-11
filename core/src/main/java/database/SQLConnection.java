package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/dbUndercooked";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found! Make sure the JDBC driver JAR is in the classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failed! Check URL, username, and password.");
            e.printStackTrace();
        }
        return conn;
    }
}
