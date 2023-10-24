package org.nanndeyanenw.loginreward;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.*;

public class LoginReward extends JavaPlugin implements Listener {
    private PlayerDataHandler playerDataHandler;
    private DataUtil dataUtilInstance;

    public DataUtil getDataUtil;

    public Map<String, Object> playerDataMap = new HashMap<>();

    private RewardGUI rewardGUI;
    private Map<UUID, Double> playerMoney = new HashMap<>();
    public RewardManager rewardManager;
    private static LoginReward instance;
    private RewardGUI plugin;

    @Override
    public void onEnable() {
        saveDefaultConfig();// config.ymlが存在しない場合、デフォルトをコピー
        dataUtilInstance = new DataUtil(this);
        instance = this;
        getServer().getPluginManager().registerEvents(new RewardGUI(this, dataUtilInstance), this);
        getCommand("setdebugdate").setExecutor(new RewardCommandExecutor(this));
        getCommand("loginreward").setExecutor(new RewardCommandExecutor(this));
        this.rewardManager = RewardManager.getInstance(this);
        playerDataHandler = new PlayerDataHandler(getDataFolder(), "config.yml");


        if (rewardManager == null) {
            getLogger().severe("エラー：VaultプラグインまたはEconomyサービスプロバイダが見つかりませんでした。プラグインを無効化します。");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            this.rewardGUI = new RewardGUI(this, dataUtilInstance);
        }

    }

    public PlayerDataHandler getPlayerDataHandler() {
        return playerDataHandler;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        int currentDays = getLoginDays(uuid);
        int updatedDays = currentDays + 1;

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(date);

        savePlayerData(uuid, updatedDays, formattedDate);
        }



    public String getLastLoginDate(UUID uuid) {
        String uniqueId = uuid.toString();
        String path = uniqueId + ".lastLoginDate";

        // playerDataMapから最後のログイン日を取得
        String lastLoginDate = (String) playerDataMap.get(path);

        if (lastLoginDate != null && !lastLoginDate.isEmpty()) {
            return lastLoginDate;
        }

        // playerDataMapに情報がない場合はnullを返す
        return null;

    }


    private int getLoginDays(UUID uuid) {
        FileConfiguration config = getConfig();
        return config.getInt(uuid + ".days", 0);  // デフォルト値は0
    }

    private void savePlayerData(UUID uuid, int days, String lastLoginDate) {
        FileConfiguration config = getConfig();
        config.set(uuid + ".days", days);
        config.set(uuid + ".lastLoginDate", lastLoginDate);
        saveConfig();  // 変更をconfig.ymlに保存
    }




    @Override
    public void onDisable() {
        instance = null;
        if (rewardManager != null) {

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

    //ログインボーナスのインベントリを開くメソッド
    private void openLoginRewardInventory(Player player) {
        if (this.rewardManager != null) {
            Inventory inventory = Bukkit.createInventory((InventoryHolder)null, 9, "ログインボーナス");
            int day = this.rewardManager.getConsecutiveDays(player);

            for(int i = 1; i <= 7; ++i) {
                if (i >= day) {
                    Reward reward = this.rewardManager.getRewardForDay(i);
                    if (reward != null) {
                        ItemStack stack = this.createItemStack(Material.GOLD_INGOT, "§a" + reward.getMessage(), "§e" + reward.getAmount() + "NANDE!");
                        inventory.addItem(new ItemStack[]{stack});
                    }
                }
            }

            player.openInventory(inventory);
        }
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

    public static LoginReward getInstance() {

        return instance;
    }

    public RewardGUI getRewardGUI() {

        return this.rewardGUI;
    }

}
