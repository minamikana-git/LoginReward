package org.nanndeyanenw.loginreward;

import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.UUID;

public class Database {
    private Connection connection;
    private String url;



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
        String sql = "CREATE TABLE IF NOT EXISTS player_data ("
                + "uuid TEXT PRIMARY KEY,"
                + "days INTEGER,"
                + "last_login_date TEXT"
                + ");";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePlayerData(UUID uuid, int days, String lastLoginDate) {
        String sql = "INSERT OR REPLACE INTO player_data (uuid, days, lastLoginDate) VALUES (?, ?, ?)";
        try (Connection conn = connect();
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
        String sql = "SELECT days FROM player_data WHERE uuid = ?";
        try (Connection conn = connect();
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

    public String getLastReceivedDate(UUID uniqueId) {
        String lastReceivedDate = null;

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT last_login_date FROM player_data WHERE uuid = ?");
            ps.setString(1, uniqueId.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lastReceivedDate = rs.getString("last_login_date");
            }

            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lastReceivedDate;
    }

    public FileConfiguration getPlayerData() {

    }
}
