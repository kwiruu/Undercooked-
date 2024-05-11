package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static database.SQLConnection.getConnection;

public class SQLOperations {
    public static void createTableAccount(String tblName) {
        try (Connection conn = getConnection();) {
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS " + tblName +
                " (userName VARCHAR(20) PRIMARY KEY NOT NULL DEFAULT 'user'," +
                " level INT DEFAULT 1 CHECK (level >= 1 AND level <= 7));";
            stmt.execute(sql);
            System.out.println("Database " + tblName + " created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTableMap(){
        try(Connection conn = getConnection();){
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS tblMap " +
                "(mapId INT PRIMARY KEY AUTO_INCREMENT," +
                "mapName TEXT(20) NOT NULL);";
            stmt.execute(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    public static void insertAccount(String username, int level) {
        try (Connection conn = getConnection();
             PreparedStatement stmnt = conn.prepareStatement(
                 "INSERT INTO tblAccount (username, level) VALUES (? , ?)");
        ) {
            stmnt.setString(1, username);
            stmnt.setInt(2, level);
            stmnt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTableHighScore() {
        try (Connection conn = getConnection();) {
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS tblHighscore (" +
                "    mapId INT NOT NULL," +
                "    userName VARCHAR(20) NOT NULL," +
                "    highScore INT NOT NULL," +
                "    FOREIGN KEY (mapId) REFERENCES tblMap (mapId)," +
                "    FOREIGN KEY (userName) REFERENCES tblAccount (userName)" +
                ");";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}