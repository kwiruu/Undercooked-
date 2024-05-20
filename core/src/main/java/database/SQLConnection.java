package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {

    private static final String URL = "jdbc:mysql://localhost:3306";

    private static final String URL2 = "jdbc:mysql://localhost:3306/dbundercooked";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL2, USER, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            try{
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(URL2, USER, PASSWORD);
            } catch (SQLException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("MySQL JDBC Driver not found! Make sure the JDBC driver JAR is in the classpath.");
            e.printStackTrace();
        }
        return conn;
    }
}
