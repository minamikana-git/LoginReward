package org.nanndeyanenw.loginreward;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Collections;

import static org.bukkit.Bukkit.getServer;

public class RewardGUI implements Listener {

    private Economy economy;

    public RewardGUI(Economy economy) {
        this.economy = economy;
    }
    private final String title = "ログイン報酬";
    private final int size = 9 * 3; // 3 rows, modify as needed

    public void openGUI(Player player, int consecutiveDays) {
        Inventory gui = Bukkit.createInventory(null, size, title);

        for (int i = 1; i <= 7; i++) {
            ItemStack rewardItem;
            rewardItem = null;

            if (i <= consecutiveDays) {
                Reward reward = determineRewardForDays(i);
                if (reward != null) {
                    rewardItem = new ItemStack(Material.DIAMOND, i);
                    ItemMeta meta = rewardItem.getItemMeta();
                    meta.setDisplayName("日の " + i + "報酬");
                    meta.setLore(Collections.singletonList(reward.getMessage()));
                    rewardItem.setItemMeta(meta);
                }
            } else {
                rewardItem = new ItemStack(Material.BARRIER);
                ItemMeta meta = rewardItem.getItemMeta();
                meta.setDisplayName("未解除");
                rewardItem.setItemMeta(meta);
            }

            gui.setItem(i - 1, rewardItem);
        }
        player.openInventory(gui);
    }
    @EventHandler
    public void handleClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || !event.getView().getTitle().equals(title)) {
            return;
        }
        event.setCancelled(true);
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
        if (slot >= 0 && slot < 7) {
            int consecutiveDays = slot + 1;
            player.sendMessage("ログイン報酬をゲットしました。" + consecutiveDays);
            // ここで報酬をプレイヤーに付与
            Reward reward = determineRewardForDays(consecutiveDays);
            if (reward != null && reward.getMoney() > 0) {
                double moneyAmount = reward.getMoney();
                economy.depositPlayer(player, moneyAmount); // Vaultを使用してお金をプレイヤーに追加

                player.sendMessage("報酬を受け取りました！: " + moneyAmount + "NANDE!");
            }

            player.closeInventory();
        }
    }
    private boolean setupEconomy(Player player) {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            player.sendMessage("Vaultプラグインが見つかりません。");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            player.sendMessage("Economyサービスプロバイダが見つかりません。");
            return false;
        }
        economy = rsp.getProvider(); // Economyプロバイダを設定
        return economy != null;
    }

    private Economy getEconomy() {
        return economy;
    }

    public static Reward determineRewardForDays(int days) {
        return switch (days) {
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
