package org.nanndeyanenw.loginreward;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;


public class LoginReward extends JavaPlugin {

    private RewardManager rewardManager;

    private Economy economy;

    @Override
    public void onEnable() {
        getCommand("loginreward").setExecutor(new RewardCommandExecutor(this));
        if (!setupEconomy()) { // Vaultセットアップが失敗した場合
            getLogger().severe("エラー：Vaultプラグインが見つかりませんでした。プラグインを無効化します。");
            if (getServer().getPluginManager().getPlugin("Vault") == null) {
                getLogger().severe("エラー：Vaultプラグインが見つかりません。");
            } else {
                getLogger().severe("エラー：Economyサービスプロバイダが見つかりません。");
            }
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.rewardManager = new RewardManager(this);
        rewardManager.loadData();
    }

    @Override
    public void onDisable() {
        rewardManager.saveData();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("エラー：Vaultプラグインが見つかりません。");
            return false;
        }

        economy = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        if (economy == null) {
            getLogger().severe("エラー：Economyサービスプロバイダが見つかりません。");
            return false;
        }
        return true;
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

    //ログインボーナスのインベントリを開くメソッド
    private void openLoginRewardInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, "ログインボーナス");

        player.openInventory(inventory);
    }

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