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

import java.text.SimpleDateFormat;
import java.util.Date;

public class RewardGUI implements Listener {

    public FileConfiguration getPlayerDataConfig() {
        return this.playerData;
    }

    private FileConfiguration playerData;
    private LoginReward plugin;
    private Economy econ; // VaultAPIのEconomy

    public RewardGUI(LoginReward plugin) {
        this.playerData = plugin.getPlayerDataConfig();
        this.plugin = plugin;
        if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
            econ = plugin.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!hasReceivedRewardToday(player)) {
            open(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getView().getTitle().equals("ログインボーナス")) {
            event.setCancelled(true); // インベントリ内の移動をキャンセル

            if (event.getCurrentItem() != null) {
                Material itemType = event.getCurrentItem().getType();
                if (itemType == Material.GOLD_INGOT) {
                    Player player = (Player) event.getWhoClicked();
                    if (!hasReceivedRewardToday(player)) {
                        giveReward(player);
                        Bukkit.getLogger().info("Saving data for " + player.getName() + ": " + playerData.getString(player.getUniqueId().toString() + ".lastReceived"));
                    } else {
                        player.closeInventory();
                        player.sendMessage("今日の報酬はすでに受け取っています。");
                    }

                }
            }
        }
    }

    private boolean hasReceivedRewardToday(Player player) {
        String lastReceived = playerData.getString(player.getUniqueId().toString() + ".lastReceived", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());

        Bukkit.getLogger().info("Checking data for " + player.getName());
        Bukkit.getLogger().info("Today: " + today);
        Bukkit.getLogger().info("Last received: " + lastReceived);
        Bukkit.getLogger().info("Today: " + today); //デバッグ用コード
        Bukkit.getLogger().info("Last received: " + lastReceived); //デバッグ用コード

        if (!today.equals(lastReceived)) {
            playerData.set(player.getUniqueId().toString() + ".lastReceived", today);
            plugin.savePlayerDataConfig(); // ここでplayerDataを実際のファイルに保存
            return false;
        }
        return true;
    }

    private void giveReward(Player player) {
        int daysLoggedIn = playerData.getInt(player.getUniqueId().toString() + ".daysLoggedIn", 1); // デフォルトは1日目

        double rewardAmount;
        switch (daysLoggedIn) {
            case 1:
                rewardAmount = 50;
                break;
            case 2:
                rewardAmount = 100;
                break;
            case 3:
                rewardAmount = 200;
                break;
            case 4:
                rewardAmount = 400;
                break;
            case 5:
                rewardAmount = 600;
                break;
            case 6:
                rewardAmount = 800;
                break;
            case 7:
                rewardAmount = 1000;
                break;
            default:
                rewardAmount = 50;
                break;
        }

        econ.depositPlayer(player, rewardAmount);
        int day = plugin.rewardManager.getConsecutiveDays(player);
        player.sendMessage("あなたは" + day + "日目のログインボーナスを受け取りました。" + rewardAmount + "獲得しました。");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        playerData.set(player.getUniqueId().toString() + ".lastReceived", today); // 今日の日付を記録
        // daysLoggedInを更新
        daysLoggedIn = (daysLoggedIn >= 7) ? 1 : daysLoggedIn + 1; // 7日目を超えたらリセット
        playerData.set(player.getUniqueId().toString() + ".daysLoggedIn", daysLoggedIn);
        plugin.savePlayerDataConfig();
    }

    public void open(Player player) {
        player.openInventory(createGuiInventory(player));
    }

    private Inventory createGuiInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, "ログインボーナス");
        int daysLoggedIn = playerData.getInt(player.getUniqueId().toString() + ".daysLoggedIn", 1);
        Bukkit.getLogger().info("Player " + player.getName() + " has logged in for " + daysLoggedIn + " days.");

        for (int i = 0; i < 9; i++) {
            if (i == 0 || i == 8) {
                inv.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, "    ")); // 灰色のステンドグラス
            } else if (i == daysLoggedIn) { // 今日受け取るべき日の報酬
                if (hasReceivedRewardToday(player)) {
                    inv.setItem(i, createItem(Material.BARRIER, "報酬を受け取りました"));
                } else {
                    inv.setItem(i, createItem(Material.GOLD_INGOT, "ここをクリックしてログインボーナスを受け取る"));
                }
            } else if (i < daysLoggedIn) { // 既に受け取った日の報酬
                inv.setItem(i, createItem(Material.BARRIER, "報酬を受け取りました"));
            } else {
                inv.setItem(i, createItem(Material.IRON_INGOT, "まだ取得できません"));
            }
        }
        return inv;
    }


        private ItemStack createItem (Material material, String displayName){
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
            return item;
        }
}
