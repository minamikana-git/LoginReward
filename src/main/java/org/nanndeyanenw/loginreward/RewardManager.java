package org.nanndeyanenw.loginreward;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class RewardManager {
    private final LoginReward plugin;
    private final Map<Player, Integer> consecutiveLogins = new HashMap<>();

    public RewardManager(LoginReward plugin) {
        this.plugin = plugin;
        loadData();
    }

    public void loadData() {
        try {
            Connection connection = plugin.getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT player, days FROM loginRewards");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Player player = plugin.getServer().getPlayer(resultSet.getString("player"));
                int days = resultSet.getInt("days");
                if (player != null) {
                    consecutiveLogins.put(player, days);
                }
            }

            statement.close();
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        try {
            Connection connection = plugin.getDatabase().getConnection();
            PreparedStatement clearStatement = connection.prepareStatement("DELETE FROM loginRewards");
            clearStatement.executeUpdate();

            for (Map.Entry<Player, Integer> entry : consecutiveLogins.entrySet()) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO loginRewards(player, days) VALUES(?, ?)");
                statement.setString(1, entry.getKey().getUniqueId().toString());
                statement.setInt(2, entry.getValue());
                statement.executeUpdate();
                statement.close();
            }

            clearStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Reward getReward(Player player) {
        int days = consecutiveLogins.getOrDefault(player, 0);
        return determineRewardForDays(days);
    }

    private Reward determineRewardForDays(int days) {
        // 連続ログイン日数に基づいて報酬を決定します。
        // この例では単純に日数に基づいて異なるアイテムを提供します。
        // 実際にはもっと複雑な報酬システムを実装することができます。
        if (days == 1) {
            return new Reward(new ItemStack(Material.DIAMOND, 1), "1 day login reward!");
        } else if (days == 2) {
            return new Reward(new ItemStack(Material.EMERALD, 2), "2 days login reward!");
        } else if (days == 3) {
            return new Reward(new ItemStack(Material.GOLD_INGOT, 3), "3 days login reward!");
        } else if (days == 4) {
            return new Reward(new ItemStack(Material.IRON_INGOT, days), days + "4 days login reward!");
        } else if (days == 5) {
            return new Reward(new ItemStack(Material.IRON_INGOT, days), days + "5 days login reward!");
        } else if (days == 6) {
            return new Reward(new ItemStack(Material.IRON_INGOT, days), days + "6 days login reward!");
        } else if (days == 7) {
            return new Reward(new ItemStack(Material.IRON_INGOT, days), days + "7 days login reward!");
        }

        return null;
    }
}


    // その他の関連メソッドをここに追加することができます。




