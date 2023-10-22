package org.nanndeyanenw.loginreward;


import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RewardGUI implements Listener {

    private PlayerDataHandler playerDataHandler;
    private int daysLoggedIn;
     this.playerDataHandler = new PlayerDataHandler(plugin.getDataFolder(), "player_data.yml");
}
    private Map<String, Object> playerDataMap;


    private Connection connection;


    public void saveDataConfig(){
        saveData.saveData();
    }
    private LoginReward plugin;
    private Economy econ; // VaultAPIのEconomy

    public RewardGUI(LoginReward plugin, SaveData saveData) {
        this.saveData = saveData;
        this.plugin = plugin;
        connect();
        if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
            econ = plugin.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        }
    }

    public void loadPlayerData(Player player) {
        FileConfiguration config = playerDataHandler.getConfig();
        String pathBase = player.getUniqueId().toString();
        if (!config.contains(pathBase + ".lastReceived")) {
            config.set(pathBase + ".lastReceived", "");
            config.set(pathBase + ".daysLoggedIn", 1);
            playerDataHandler.saveConfig();
        }
    }



    public int getDaysLoggedIn(Player player) {
        String path = player.getUniqueId().toString() + ".daysLoggedIn";
        return playerDataHandler.getConfig().getInt(path, 1);
    }

        @EventHandler
        public void onPlayerJoin (PlayerJoinEvent event){
            Player player = event.getPlayer();
            if (!hasReceivedRewardToday(player)) {
                open(player);
                // ログイン日数をインクリメント
                dataUtil.incrementLoginDays(player);
                // dataConfigに変更があったので保存
                saveData.saveData();
            }
        }


        @EventHandler
        public void onInventoryClick (InventoryClickEvent event) throws ParseException {
            if (event.getClickedInventory() != null && event.getView().getTitle().equals("ログインボーナス")) {
                event.setCancelled(true); // インベントリ内の移動をキャンセル

                if (event.getCurrentItem() != null) {
                    Material itemType = event.getCurrentItem().getType();
                    if (itemType == Material.GOLD_INGOT) {
                        Player player = (Player) event.getWhoClicked();
                        if (!hasReceivedRewardToday(player)) {
                            handleRewardForPlayer(player);

                            // ここでバリアブロックに更新します。
                            event.getClickedInventory().setItem(event.getSlot(), createItem(Material.BARRIER, "報酬を受け取りました"));
                        } else {
                            player.closeInventory();
                            player.sendMessage("今日の報酬はすでに受け取っています。");
                        }
                    }
                }
            }
        }

        private boolean hasReceivedRewardToday (Player player){
            String uniqueId = player.getUniqueId().toString();
            String lastReceived = (String) playerDataMap.get(uniqueId + ".lastReceived");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());

            return today.equals(lastReceived);
        }

        private double giveReward (Player player){
            int daysLoggedIn = (Integer) playerDataMap.getOrDefault(player.getUniqueId().toString() + ".daysLoggedIn", 1); // デフォルトは1日目
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
            return rewardAmount; // 報酬の金額を返す
        }


    private void handleRewardForPlayer(Player player) throws ParseException {
        daysLoggedIn = (Integer) playerDataMap.getOrDefault(player.getUniqueId().toString() + ".daysLoggedIn", 1);// デフォルトは1日目
        double rewardAmount = 0;
        rewardAmount = giveReward(player);
        player.sendMessage("あなたは" + daysLoggedIn + "日目のログインボーナスを受け取りました。" + rewardAmount + "円を獲得しました！");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastReceivedDateStr = (String) playerDataMap.get(player.getUniqueId().toString() + ".lastReceived");
        Date lastReceivedDate = sdf.parse(lastReceivedDateStr);

        Calendar cal = Calendar.getInstance();

        // この部分を追加
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = cal.getTime();
        if (lastReceivedDate.before(yesterday)) {
            daysLoggedIn = (daysLoggedIn >= 7) ? 1 : daysLoggedIn + 1; // 7日目を超えたらリセット
        }

        cal.add(Calendar.DAY_OF_MONTH, 1); // カレンダーを今日の日付に戻す


        String pathBase = player.getUniqueId().toString();
        playerDataHandler.getConfig().set(pathBase + ".lastReceived", sdf.format(cal.getTime()));
        playerDataHandler.getConfig().set(pathBase + ".daysLoggedIn", daysLoggedIn);
        playerDataHandler.saveConfig();


        // GUIを再度開くことで、更新を反映させる
        player.closeInventory(); // 一度閉じる
        open(player); // GUIを開く
    }

        public void open (Player player){
            player.openInventory(createGuiInventory(player));
        }

        private Inventory createGuiInventory (Player player){
            Inventory inv = Bukkit.createInventory(null, 9, "ログインボーナス");
            daysLoggedIn = (Integer) playerDataMap.getOrDefault(player.getUniqueId().toString() + ".daysLoggedIn", 1);
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

