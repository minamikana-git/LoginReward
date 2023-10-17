package org.nanndeyanenw.loginreward;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
public class RewardGUI implements Listener {

    public FileConfiguration getPlayerDataConfig() {
        return this.playerData;
    }
    private FileConfiguration playerData;
    private LoginReward plugin;
    private Economy econ; // VaultAPIのEconomy

    public RewardGUI(LoginReward plugin) {
        this.plugin = plugin;
        this.playerData = plugin.getPlayerDataConfig();
        this.plugin = plugin;
        if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
            econ = plugin.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        open(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getView().getTitle().equals("ログインボーナス")) {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.GOLD_INGOT) {
                Player player = (Player) event.getWhoClicked();
                giveReward(player);
                player.closeInventory();
                event.setCancelled(true);
            }
        }
    }

    private void giveReward(Player player) {
        int daysLoggedIn = playerData.getInt(player.getUniqueId().toString() + ".daysLoggedIn", 1); // デフォルトは1日目

        double rewardAmount;
        switch (daysLoggedIn) {
            case 1: rewardAmount = 50; break;
            case 2: rewardAmount = 100; break;
            case 3: rewardAmount = 200; break;
            case 4: rewardAmount = 400; break;
            case 5: rewardAmount = 600; break;
            case 6: rewardAmount = 800; break;
            case 7: rewardAmount = 1000; break;
            default: rewardAmount = 50; break;
        }

        econ.depositPlayer(player, rewardAmount);
        int day = plugin.rewardManager.getConsecutiveDays(player);
        player.sendMessage("あなたは" + day + "日目のログインボーナスを受け取りました。" + rewardAmount + "獲得しました。");

        // daysLoggedInを更新
        daysLoggedIn = (daysLoggedIn >= 7) ? 1 : daysLoggedIn + 1; // 7日目を超えたらリセット
        playerData.set(player.getUniqueId().toString() + ".daysLoggedIn", daysLoggedIn);
        plugin.savePlayerDataConfig();
    }

    public void open(Player player) {
        player.openInventory(createGuiInventory());
    }

    private Inventory createGuiInventory() {
        Inventory inv = Bukkit.createInventory(null, 9, "ログインボーナス");

        ItemStack rewardItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = rewardItem.getItemMeta();
        meta.setDisplayName("ここをクリックしてログインボーナスを受け取る");
        rewardItem.setItemMeta(meta);
        inv.setItem(4, rewardItem);

        return inv;
    }
}