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

import java.text.SimpleDateFormat;
import java.util.Date;

public class RewardGUI implements Listener {
    private SaveData saveDataInstance;
    public FileConfiguration getPlayerDataConfig() {
        return this.playerData;
    }

    private FileConfiguration playerData;
    private LoginReward plugin;
    private Economy econ; // VaultAPIのEconomy
    private SaveData saveData;

    public RewardGUI(LoginReward plugin, SaveData saveData) {
        this.saveDataInstance = saveData;
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
            // ログイン日数をインクリメント
            saveDataInstance.incrementLoginDays(player);
            // dataConfigに変更があったので保存
            saveDataInstance.saveDataConfig();
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

    private boolean hasReceivedRewardToday(Player player) {
        String uniqueId = player.getUniqueId().toString();
        if (!playerData.contains(uniqueId + ".lastReceived")) {

        }
        String lastReceived = playerData.getString(uniqueId + ".lastReceived", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());

        return today.equals(lastReceived);
    }

        private double giveReward (Player player){
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
            return rewardAmount; // 報酬の金額を返す
        }


        private void handleRewardForPlayer (Player player) {
            int daysLoggedIn = playerData.getInt(player.getUniqueId().toString() + ".daysLoggedIn", 1); // デフォルトは1日目
            double rewardAmount = 0;
            econ.depositPlayer(player, rewardAmount);
            rewardAmount = giveReward(player);
            player.sendMessage("あなたは" + daysLoggedIn + "日目のログインボーナスを受け取りました。" + rewardAmount + "円を獲得しました！");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            playerData.set(player.getUniqueId().toString() + ".lastReceived", today); // 今日の日付を記録
            // daysLoggedInを更新
            daysLoggedIn = (daysLoggedIn >= 7) ? 1 : daysLoggedIn + 1; // 7日目を超えたらリセット
            playerData.set(player.getUniqueId().toString() + ".daysLoggedIn", daysLoggedIn);
            plugin.savePlayerDataConfig();
            //GUIを再度開くことで、更新を反映させる
            player.closeInventory(); //一度閉じる
            open(player); //GUIを開く。
        }


        public void open (Player player){
            player.openInventory(createGuiInventory(player));
        }

        private Inventory createGuiInventory (Player player){
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

