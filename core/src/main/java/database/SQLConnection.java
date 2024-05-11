package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/dbundercooked";
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



    public static void createTable(String tblName) {
        try (Connection conn = getConnection();) {
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS " + tblName +
                "username TEXT(20) PRIMARY KEY NOT NULL DEFAULT user";
            stmt.executeUpdate(sql);
            System.out.println("Database " + tblName + " created successfully");
        } catch (SQLException e) {
            System.out.println("AKO DOYDAS");
        }
    }

}
