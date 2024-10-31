package net.hotamachisubaru.loginreward;


import net.hotamachisubaru.loginreward.DataUtil;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class RewardGUI implements Listener {

    private PlayerDataHandler playerDataHandler;
    private int daysLoggedIn;
    public Map<String, Object> playerDataMap; //

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

        // 現在の日付を取得
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        if (!config.contains(pathBase)) {
            config.set(pathBase + ".lastReceived", today);
            config.set(pathBase + ".daysLoggedIn", 1);

            // 最後のログイン日を追加
            config.set(pathBase + ".lastLoginDate", today);

            // playerDataMapへのロード
            playerDataMap.put(pathBase + ".lastReceived", today);
            playerDataMap.put(pathBase + ".daysLoggedIn", 1);
            playerDataMap.put(pathBase + ".lastLoginDate", today);

            playerDataHandler.saveConfig();
        } else {
            // playerDataMapへのロード
            playerDataMap.put(pathBase + ".lastReceived", config.getString(pathBase + ".lastReceived"));
            playerDataMap.put(pathBase + ".daysLoggedIn", config.getInt(pathBase + ".daysLoggedIn"));
            playerDataMap.put(pathBase + ".lastLoginDate", config.getString(pathBase + ".lastLoginDate"));
        }
    }



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loadPlayerData(player);

        // 最後のログイン日を更新
        updateLastLoginDate(player);

        // ログインボーナスを受け取っていない場合のみopenメソッドを実行
        if (!hasReceivedRewardToday(player)) {
            open(player);
        }
    }

    private boolean hasLoggedToday(Player player) {
        String uniqueId = player.getUniqueId().toString();
        String pathBase = uniqueId + ".lastLoginDate";

        String lastLogin = (String) playerDataMap.get(pathBase);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());

        return today.equals(lastLogin);
    }


    public int getDaysLoggedIn(Player player) {
        FileConfiguration config = playerDataHandler.getConfig();
        String path = player.getUniqueId().toString() + ".daysLoggedIn";
        return config.getInt(path, 1); // デフォルトは1日目
    }

    public int incrementLoginDays(Player player) {
        String path = player.getUniqueId().toString() + ".daysLoggedIn";
        int currentDays = (int) playerDataMap.getOrDefault(path, 1);

        if (currentDays >= 7) {
            currentDays = 0; // 7日間の後は1日目にリセットするため
        }

        playerDataMap.put(path, currentDays + 1);
        playerDataHandler.getConfig().set(path, currentDays + 1); // config.ymlにも保存
        playerDataHandler.saveConfig(); // 変更を保存

        return currentDays + 1;
    }

    private void updateLastLoginDate(Player player) {
        String uniqueId = player.getUniqueId().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        playerDataMap.put(uniqueId + ".lastLoginDate", today); // playerDataMapを更新
        playerDataHandler.getConfig().set(uniqueId + ".lastLoginDate", today); // ymlファイルを更新
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) throws ParseException {
        if (event.getView().getTitle().equals("ログインボーナス")) { // このブレースを追加
            event.setCancelled(true);
            if (event.getCurrentItem() != null) {
                Material itemType = event.getCurrentItem().getType();
                if (itemType == Material.GOLD_INGOT) {
                    Player player = (Player) event.getWhoClicked();
                    if (!hasReceivedRewardToday(player)) {
                        handleRewardForPlayer(player);
                        event.getClickedInventory().setItem(event.getSlot(), createItem(Material.BARRIER, "報酬を受け取りました"));
                    } else {
                        player.closeInventory();
                        player.sendMessage("今日の報酬はすでに受け取っています。");
                    }
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

        return today.equals(lastReceived);
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



            return rewardAmount; // 報酬の金額を返す
        }


    public void handleRewardForPlayer(Player player) throws ParseException {
        FileConfiguration config = playerDataHandler.getConfig();
        String pathBase = player.getUniqueId().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
        String today = sdf.format(new Date());

        String lastReceivedDateStr = playerDataMap.get(pathBase + ".lastReceived").toString();
        Date lastReceivedDate = null;

        if (!lastReceivedDateStr.isEmpty()) {
            lastReceivedDate = sdf.parse(lastReceivedDateStr);
        }

        if (lastReceivedDate == null || !sdf.format(lastReceivedDate).equals(today)) {
            double rewardAmount = giveReward(player);
            int daysLoggedIn = getDaysLoggedIn(player);
            player.sendMessage("あなたは" + daysLoggedIn + "日目のログインボーナスを受け取りました。" + rewardAmount + "円を獲得しました！");

            // lastReceived日付を更新
            playerDataMap.put(pathBase + ".lastReceived", today);
            config.set(pathBase + ".lastReceived", today);
            playerDataHandler.saveConfig();

            player.closeInventory();
            open(player);
        }
    }


    public void open (Player player){
            player.openInventory(createGuiInventory(player));
        }

        private Inventory createGuiInventory (Player player){
            Inventory inv = Bukkit.createInventory(null, 9, "ログインボーナス");
            plugin.getLogger().info("playerDataMap: " + playerDataMap);
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

