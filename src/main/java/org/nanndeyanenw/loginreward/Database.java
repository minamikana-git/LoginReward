package org.nanndeyanenw.loginreward;

import java.sql.*;
import java.util.UUID;

public class Database {
    private Connection connection;
    private String url;

    private Database database;

    public Database(String filename) {
        this.url = "jdbc:sqlite:" + filename;
    }

    public Connection connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url);
        }
        return connection;
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void initializeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS players ("
                + "uuid TEXT PRIMARY KEY,"
                + "days INTEGER,"
                + "lastLoginDate TEXT"
                + ");";
        try (Connection conn = database.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePlayerData(UUID uuid, int days, String lastLoginDate) {
        String sql = "INSERT OR REPLACE INTO players (uuid, days, lastLoginDate) VALUES (?, ?, ?)";
        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setInt(2, days);
            pstmt.setString(3, lastLoginDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getLoginDays(UUID uuid) {
        String sql = "SELECT days FROM players WHERE uuid = ?";
        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("days");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
