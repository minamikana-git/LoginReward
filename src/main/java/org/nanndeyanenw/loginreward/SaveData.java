package org.nanndeyanenw.loginreward;


import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.*;
import java.util.Date;


public class SaveData {

    private LoginReward plugin;
    private Connection connection;

    private Map<UUID, Double> playerMoney = new HashMap<>();
    Statement stmt = connection.createStatement();

    public SaveData(LoginReward plugin) throws SQLException {
        this.plugin = plugin;
        establishConnection();
    }

    public void createTable(String record) {
        String sql = "CREATE TABLE IF NOT EXISTS " + record + " ("
                + "uuid TEXT PRIMARY KEY,"
                + "money REAL,"
                + "login_days INTEGER,"
                + "last_login_date TEXT"
                + ")";
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
            private void establishConnection () {
                try {
                    if (connection != null && !connection.isClosed()) {
                        return;
                    }

                    synchronized (this) {
                        if (connection != null && !connection.isClosed()) {
                            return;
                        }
                        Class.forName("org.sqlite.JDBC");
                        connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + File.separator + "playerdata.db");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void loadData () {
                try {
                    PreparedStatement ps = connection.prepareStatement("SELECT uuid, money FROM player_data");
                    ResultSet rs = ps.executeQuery();

                    playerMoney.clear();
                    while (rs.next()) {
                        UUID uuid = UUID.fromString(rs.getString("uuid"));
                        double money = rs.getDouble("money");
                        playerMoney.put(uuid, money);
                    }
                    rs.close();
                    ps.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            public void saveData () {
                try {
                    for (Map.Entry<UUID, Double> entry : playerMoney.entrySet()) {
                        PreparedStatement ps = connection.prepareStatement("REPLACE INTO player_data (uuid, money) VALUES (?, ?)");
                        ps.setString(1, entry.getKey().toString());
                        ps.setDouble(2, entry.getValue());
                        ps.executeUpdate();
                        ps.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            public void onPlayerLogin(PlayerLoginEvent event){
                Player player = event.getPlayer();
                incrementLoginDays(player);
            }
            public void incrementLoginDays (Player player){
                try {
                    String playerUUID = player.getUniqueId().toString();

                    PreparedStatement ps = connection.prepareStatement("SELECT login_days, last_login_date FROM player_data WHERE uuid = ?");
                    ps.setString(1, playerUUID);
                    ResultSet rs = ps.executeQuery();

                    int currentDays = 0;
                    if (rs.next()) {
                        currentDays = rs.getInt("login_days");
                    }


                    int updatedDays = currentDays + 1;

                    Date date = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDate = dateFormat.format(date);

                    PreparedStatement updatePs = connection.prepareStatement("REPLACE INTO player_data (uuid, login_days, last_login_date) VALUES (?, ?, ?)");
                    updatePs.setString(1, playerUUID);
                    updatePs.setInt(2, updatedDays);
                    updatePs.setString(3, formattedDate);
                    updatePs.executeUpdate();

                    rs.close();
                    ps.close();
                    updatePs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
}

