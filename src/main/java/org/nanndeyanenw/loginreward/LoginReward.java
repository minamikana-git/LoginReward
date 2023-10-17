package org.nanndeyanenw.loginreward;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.*;
public class LoginReward extends JavaPlugin {

    private File dataFile;
    private YamlConfiguration dataConfig;

    private RewardGUI rewardGUI;
    private Map<UUID, Double> playerMoney = new HashMap<>();

    private RewardManager rewardManager;

    public LoginReward() {
    }

    @Override
    public void onEnable() {
        rewardGUI = new RewardGUI(this);
        getServer().getPluginManager().registerEvents(rewardGUI,this);
        getCommand("loginreward").setExecutor(new RewardCommandExecutor(this));
        this.rewardManager = RewardManager.getInstance(this);
        if (rewardManager == null) {
            getLogger().severe("エラー：VaultプラグインまたはEconomyサービスプロバイダが見つかりませんでした。プラグインを無効化します。");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        rewardManager.loadData();
    }
    @Override
    public void onDisable() {
        if (rewardManager != null) {
            rewardManager.saveData();
        }
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("loginreward")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("§cこのコマンドはプレイヤーからのみ実行できます。");
                return true;
            }
            // プレイヤーとしての処理を続ける
            Player player = (Player) commandSender;
            openLoginRewardInventory(player);
            return true;
        }
        return true;
    }


    public void savePlayerDataConfig() {
        if (dataConfig == null || dataFile == null) {
            return;
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException ex) {
            plugin.getLogger().severe("Could not save config to " + dataFile);
        }
    }
    //ログインボーナスのインベントリを開くメソッド
    private void openLoginRewardInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, "ログインボーナス");
        int day = rewardManager.getConsecutiveDays(player); // 連続ログイン日数を取得

        for (int i = 1; i <= 7; i++) {
            if (i < day) { // すでに受け取った報酬の場合
                continue; // スキップ
            }
            Reward reward = rewardManager.getRewardForDay(i);
            if (reward != null) {
                ItemStack stack = createItemStack(Material.GOLD_NUGGET, "§a" + reward.getMessage(), "§e" + reward.getAmount() + "NANDE!");
                inventory.addItem(stack);
            }
        }

        player.openInventory(inventory);
    }

    private ItemStack createItemStack(Material material, String displayName, String lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(displayName);
        List<String> lores = new ArrayList<>();
        lores.add(lore);
        meta.setLore(lores);
        stack.setItemMeta(meta);
        return stack;
    }

}