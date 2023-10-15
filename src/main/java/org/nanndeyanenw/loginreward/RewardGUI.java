package org.nanndeyanenw.loginreward;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RewardManager {

    private final LoginRewardPlugin plugin;
    private final DataUtil dataUtil;

    public RewardManager(LoginRewardPlugin plugin) {
        this.plugin = plugin;
        this.dataUtil = new DataUtil(plugin);
    }

    public boolean hasClaimedReward(Player player) {
        return dataUtil.contains(player.getUniqueId().toString() + ".claimed");
    }

    public void claimReward(Player player) {
        int consecutiveDays = 1; // デフォルトは1日
        if(dataUtil.contains(player.getUniqueId().toString() + ".consecutiveDays")) {
            consecutiveDays = (int) dataUtil.get(player.getUniqueId().toString() + ".consecutiveDays");
        }

        Reward reward = getRewardForDay(consecutiveDays);
        if (reward == null) return;

        // 報酬を与える
        Economy econ = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
        econ.depositPlayer(player, reward.getAmount());
        player.sendMessage(reward.getMessage());

        // データを更新
        dataUtil.set(player.getUniqueId().toString() + ".claimed", true);
        dataUtil.set(player.getUniqueId().toString() + ".consecutiveDays", consecutiveDays + 1);
    }

    public void resetClaim(Player player) {
        dataUtil.set(player.getUniqueId().toString() + ".claimed", false);
    }

    public void resetConsecutiveDays(Player player) {
        dataUtil.set(player.getUniqueId().toString() + ".consecutiveDays", 1);
    }

    private Reward getRewardForDay(int day) {
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
}