package org.nanndeyanenw.loginreward;

import org.bukkit.entity.Player;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.sql.Connection;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RewardManager {
    private Connection connection;
    private DataUtil dataUtil;
    private Map<UUID, Double> playerMoney = new HashMap<>();

    private Reward loginReward = new Reward(100, "ログインボーナス!");
    private final Map<Integer, Reward> rewards = new HashMap<>();

    private LoginReward plugin;

    public Reward getLoginReward() {
        return loginReward;
    }

    private static RewardManager instance;
    private Economy econ;

    private RewardManager(LoginReward plugin) {
        this.plugin = plugin;

        this.dataUtil = new DataUtil(plugin);
        this.plugin = plugin;
        if (!setupEconomy()) {
            plugin.getLogger().severe("Vault not found! Disabling plugin...");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            establishConnection();
        }
    }

    private void establishConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            // ここでは適切なデータベースの場所と名前を設定する必要があります
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + File.separator + "playerdata.db");

    } catch (Exception e) {
        e.printStackTrace();
    }
}



    public int getConsecutiveDays(Player player) {
        if (dataUtil.contains(player.getUniqueId() + ".consecutiveDays")) {
            return (int) dataUtil.get(player.getUniqueId() + ".consecutiveDays");
        }
        return 0;
    }

    public Reward getRewardForConsecutiveDays(int days) {
        if (days >= 7) {
            return rewards.get(7);
        }
        return rewards.getOrDefault(days, null);
    }

    public void giveLoginReward(Player player) {

        int consecutiveDays = getConsecutiveDays(player);
        Reward reward = getRewardForConsecutiveDays(consecutiveDays);

        if (reward != null) {
            econ.depositPlayer(player, reward.getAmount());
            player.sendMessage("§a" + reward.getMessage() + " §e" + reward.getAmount() + " 円を獲得しました！");

            // 連続ログイン日数を1増やす (もし7日を超えたら1日目にリセット)
            setConsecutiveDays(player, (consecutiveDays % 7) + 1);
        }
    }

    public void setConsecutiveDays(Player player, int days) {
        UUID playerUUID = player.getUniqueId();
        String path = playerUUID.toString() + ".consecutiveDays";


    }

    public static RewardManager getInstance(LoginReward plugin) {
        if (instance == null) {
            instance = new RewardManager(plugin);
            if (!instance.setupEconomy()) {
                return null;
            }
        }
        return instance;
    }







    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public double getPlayerMoney(UUID uuid) {
        return playerMoney.getOrDefault(uuid, 50.0);
    }



    public Reward getRewardForDay(int day) {
        return switch (day) {
            case 1 -> new Reward(50, "ログインボーナス！: 50NANDE!");
            case 2 -> new Reward(100, "2連続ログインボーナス！: 100NANDE!");
            case 3 -> new Reward(200, "3連続ログインボーナス！: 200NANDE!");
            case 4 -> new Reward(400, "4連続ログインボーナス！: 400NANDE!");
            case 5 -> new Reward(600, "5連続ログインボーナス！: 600NANDE!");
            case 6 -> new Reward(800, "6連続ログインボーナス！: 800NANDE!");
            case 7 -> new Reward(1000, "7連続ログインボーナス！: 1000NANDE!");
            default -> null;
        };
    }

    public boolean hasClaimedReward(Player player) {
        // 現在の連続ログイン日数を取得
        int consecutiveDays = getConsecutiveDays(player);
        // 現在の日の報酬を取得
        Reward rewardForToday = getRewardForConsecutiveDays(consecutiveDays);

        // プレイヤーの所持金を取得
        double playerMoney = getPlayerMoney(player.getUniqueId());

        // 現在の日の報酬を既に受け取っている場合、所持金がその報酬の額より多いかどうかを確認
        if (rewardForToday != null && playerMoney >= rewardForToday.getAmount()) {
            return true;
        }

        return false;
    }

    public void incrementLoginDays(Player player) {
        String playerUUID = player.getUniqueId().toString();

        try {
            // 現在のログイン日数を取得
            PreparedStatement ps = connection.prepareStatement("SELECT login_days FROM player_data WHERE uuid = ?");
            ps.setString(1, playerUUID);
            ResultSet rs = ps.executeQuery();

            int currentDays = 0;
            if (rs.next()) {
                currentDays = rs.getInt("login_days");
            }
            rs.close();
            ps.close();

            // ログイン日数をインクリメント
            int updatedDays = currentDays + 1;

            // 現在の日付を取得
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(date);

            // データを更新または挿入
            PreparedStatement updatePs = connection.prepareStatement("REPLACE INTO player_data (uuid, login_days, last_login_date) VALUES (?, ?, ?)");
            updatePs.setString(1, playerUUID);
            updatePs.setInt(2, updatedDays);
            updatePs.setString(3, formattedDate);
            updatePs.executeUpdate();
            updatePs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
