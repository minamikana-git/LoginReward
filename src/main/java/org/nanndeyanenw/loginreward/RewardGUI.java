package org.nanndeyanenw.loginreward;


import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
    private Map<String, Object> playerDataMap;

    private LoginReward plugin;
    private Economy econ; // VaultAPIのEconomy


    public RewardGUI(LoginReward plugin, DataUtil dataUtil) {
        this.plugin = plugin;
        File dataFolder = plugin.getDataFolder();  // この行を追加
        this.playerDataHandler = new PlayerDataHandler(dataFolder, "config.yml"); // 引数を修正
        this.playerDataMap = new HashMap<>();
        if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
            econ = plugin.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        }
    }


    public void loadPlayerData(Player player) {

        FileConfiguration config = playerDataHandler.getConfig();
        String pathBase = player.getUniqueId().toString();
        if (!config.contains(pathBase)) {
            config.set(pathBase + ".lastReceived", "");
            config.set(pathBase + ".daysLoggedIn", 1);
            playerDataHandler.saveConfig();

            // playerDataMapへのロード
            playerDataMap.put(pathBase + ".lastReceived", "");
            playerDataMap.put(pathBase + ".daysLoggedIn", 1);
        } else {
            // playerDataMapへのロード
            playerDataMap.put(pathBase + ".lastReceived", config.getString(pathBase + ".lastReceived"));
            playerDataMap.put(pathBase + ".daysLoggedIn", config.getInt(pathBase + ".daysLoggedIn"));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loadPlayerData(player);
        if (!hasReceivedRewardToday(player)) {
            open(player);
            // ログイン日数をインクリメント
            incrementLoginDays(player);
            // ymlに変更があったら保存
            playerDataHandler.saveConfig();
        }
    }


    public int getDaysLoggedIn(Player player) {
        FileConfiguration config = playerDataHandler.getConfig();
        String path = player.getUniqueId().toString() + ".daysLoggedIn";
        return config.getInt(path, 1); // デフォルトは1日目
    }





        @EventHandler(priority = EventPriority.HIGHEST)
        public void onInventoryClick (InventoryClickEvent event) throws ParseException {
            if (event.getView().getTitle().equals("ログインボーナス")) {
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
            }
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


    private boolean hasReceivedRewardToday(Player player){
        String uniqueId = player.getUniqueId().toString();
        String pathBase = uniqueId + ".lastReceived";

        String lastReceived = (String) playerDataMap.get(pathBase);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());

        if (lastReceived == null || lastReceived.isEmpty() || !today.equals(lastReceived)) {
            // プレイヤーが今日報酬を受け取っていない場合
            if (lastReceived == null || lastReceived.isEmpty()) {
                playerDataMap.put(pathBase, today);
                // 外部にデータを保存
                playerDataHandler.saveConfig();
            }
            return false; // 今日の報酬は受け取っていない
        }
        return true; // 今日の報酬は既に受け取っている
        }

        private double giveReward (Player player){
            String uniqueId = player.getUniqueId().toString();
            int daysLoggedIn = (Integer) playerDataMap.getOrDefault(uniqueId + ".daysLoggedIn", 1); // デフォルトは1日目

            // 報酬金額を計算
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
                default: // 7日目以降
                    rewardAmount = 1000;
                    break;
            }

            // プレイヤーに報酬を付与
            econ.depositPlayer(player, rewardAmount);

            // 連続ログイン日数を1日増やし、保存
            playerDataMap.put(uniqueId + ".daysLoggedIn", daysLoggedIn + 1);

            return rewardAmount; // 報酬の金額を返す
        }


    private void handleRewardForPlayer(Player player) throws ParseException {
        FileConfiguration config = playerDataHandler.getConfig();
        String pathBase = player.getUniqueId().toString();

        daysLoggedIn = config.getInt(pathBase + ".daysLoggedIn", 1); // デフォルトは1日目
        double rewardAmount = giveReward(player);
        player.sendMessage("あなたは" + daysLoggedIn + "日目のログインボーナスを受け取りました。" + rewardAmount + "円を獲得しました！");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastReceivedDateStr = config.getString(pathBase + ".lastReceived");
        Date lastReceivedDate = sdf.parse(lastReceivedDateStr);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = cal.getTime();
        if (lastReceivedDate.before(yesterday)) {
            daysLoggedIn = (daysLoggedIn >= 7) ? 1 : daysLoggedIn + 1; // 7日目を超えたらリセット
        }
        cal.add(Calendar.DAY_OF_MONTH, 1); // カレンダーを今日の日付に戻す

        playerDataMap.put(pathBase + ".lastReceived", sdf.format(cal.getTime()));
        playerDataMap.put(pathBase + ".daysLoggedIn", daysLoggedIn);

        config.set(pathBase + ".lastReceived", sdf.format(cal.getTime()));
        config.set(pathBase + ".daysLoggedIn", daysLoggedIn);

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
            System.out.println("playerDataMap: " + playerDataMap);
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


    public void incrementLoginDays(Player player) {
        String path = player.getUniqueId().toString() + ".daysLoggedIn";
        int currentDays = playerDataHandler.getConfig().getInt(path, 1);
        playerDataHandler.getConfig().set(path, currentDays + 1);
    }

    private void updateLastLoginDate(Player player){
        String uniqueId = player.getUniqueId().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        playerDataMap.put(uniqueId + ".lastLoginDate", today);
    }

    private void updateConsecutiveLoginDays(Player player){
        String uniqueId = player.getUniqueId().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        String lastLoginDateStr = (String) playerDataMap.get(uniqueId + ".lastLoginDate");
        if (lastLoginDateStr != null && !lastLoginDateStr.isEmpty()) {
            try {
                Date lastLoginDate = sdf.parse(lastLoginDateStr);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(lastLoginDate);
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date nextExpectedLoginDate = calendar.getTime();
                Date currentDate = sdf.parse(today);
                if (currentDate.equals(nextExpectedLoginDate)) {
                    int daysLoggedIn = (Integer) playerDataMap.getOrDefault(uniqueId + ".daysLoggedIn", 1);
                    playerDataMap.put(uniqueId + ".daysLoggedIn", daysLoggedIn + 1);
                } else if (currentDate.after(nextExpectedLoginDate)) {
                    playerDataMap.put(uniqueId + ".daysLoggedIn", 1); // 1にリセット
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        updateLastLoginDate(player); // 最後にログインした日をアップデート。
    }



}

