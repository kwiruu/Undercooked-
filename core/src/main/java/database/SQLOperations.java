package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static database.SQLConnection.getConnection;

public class SQLOperations {

    public static void createDatabase(){
        try (Connection conn = getConnection();){
            Statement stmt = conn.createStatement();
            String sql = "CREATE DATABASE IF NOT EXISTS dbUndercooked;";
            stmt.execute(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
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


    public static void insertAccount(String username) {
        try (Connection conn = getConnection();
             PreparedStatement stmnt = conn.prepareStatement(
                 "INSERT INTO tblAccount (username, level) VALUES (? , 1)");
        ) {
            stmnt.setString(1, username);
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

    public static boolean userSignIn(String userName) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM tblAccount WHERE userName = ?");
        ) {
            stmt.setString(1, userName);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                System.out.println("User signed in successfully. Welcome ma nigga");
            } else {
                insertAccount(userName);
                System.out.println("New account created. Proceeding to the game...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void levelUp(String username) {
        String sql = "UPDATE tblAccount SET level = level + 1 WHERE userName = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("User level updated successfully.");
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int findLevel(String username) {
        int level = 0;
        String sql = "SELECT level FROM tblAccount WHERE userName = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                level = rs.getInt("level");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return level;
    }

    public static UserInfo getInfo(String username) {
        int level = 0;
        String userName = "";
        String sql = "SELECT * FROM tblAccount WHERE userName = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                level = rs.getInt("level");
                userName = rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new UserInfo(level,userName);
    }

    public static List<HighScore> getTopHighScores(int mapId) {
        List<HighScore> highScores = new ArrayList<>();
        String sql = "SELECT userName, highScore FROM tblHighscore WHERE mapId = ? ORDER BY highScore DESC LIMIT 10";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mapId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String userName = rs.getString("userName");
                int highScore = rs.getInt("highScore");
                highScores.add(new HighScore(userName, highScore));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return highScores;
    }

    public static void insertScore(String username,int mapId,int score){
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO tblHighscore (mapId, userName, highScore)  VALUES " +
                     "(? , ?, ?)"
             )) {
            stmt.setInt(1, mapId);
            stmt.setString(2,username);
            stmt.setInt(3,score);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
