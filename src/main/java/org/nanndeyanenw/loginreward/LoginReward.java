package org.nanndeyanenw.loginreward;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.*;
import java.sql.*;
import java.sql.Connection;
public class LoginReward extends JavaPlugin implements Listener {


    private Database database;
    private RewardGUI rewardGUI;
    private Map<UUID, Double> playerMoney = new HashMap<>();
    private Connection connection;
    public RewardManager rewardManager;
    private static LoginReward instance;
    public LoginReward(String dbFilename) {
        this.database = new Database(dbFilename);
        initializeTable();
    }
    private void initializeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS players ("
                + "uuid TEXT PRIMARY KEY,"
                + "days INTEGER,"
                + "lastLoginDate TEXT"
                + ");";
        try (Connection conn = database.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getCommand("loginreward").setExecutor(new RewardCommandExecutor(this));
        getCommand("debugdate").setExecutor(new RewardCommandExecutor(this));
        this.rewardManager = RewardManager.getInstance(this);
        if (rewardManager == null) {
            getLogger().severe("エラー：VaultプラグインまたはEconomyサービスプロバイダが見つかりませんでした。プラグインを無効化します。");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            try {
                Class.forName("org.sqlite.JDBC"); // SQLite JDBC ドライバをロード
                connection = DriverManager.getConnection("jdbc:sqlite:" + this.getDataFolder() + File.separator + "player_data.db");
            } catch (SQLException e) {
                e.printStackTrace();
                getLogger().severe("データベースへの接続に失敗しました。");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                getLogger().severe("SQLite JDBC ドライバが見つかりませんでした。");
            }
        }
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

    private int getLoginDays(UUID uuid) {
        String sql = "SELECT days FROM player_data WHERE uuid = ?";
        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("days");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void savePlayerData(UUID uuid, int days, String lastLoginDate) {
        String sql = "INSERT OR REPLACE INTO player_data(uuid, days, lastLoginDate) VALUES (?, ?, ?)";
        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setInt(2, days);
            pstmt.setString(3, lastLoginDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDisable() {
        instance = null;
        if (rewardManager != null) {
        closeConnection();
        }
    }
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        Inventory inventory = Bukkit.createInventory(null, 9, "ログインボーナス");
        int day = rewardManager.getConsecutiveDays(player); // 連続ログイン日数を取得

        for (int i = 1; i <= 7; i++) {
            if (i < day) { // すでに受け取った報酬の場合
                continue; // スキップ
            }
            Reward reward = rewardManager.getRewardForDay(i);
            if (reward != null) {
                ItemStack stack = createItemStack(Material.GOLD_INGOT, "§a" + reward.getMessage(), "§e" + reward.getAmount() + "NANDE!");
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

    public static LoginReward getInstance() {

        return instance;
    }

    public RewardGUI getRewardGUI(){
        return this.rewardGUI;
    }
    public Database getDatabase() {
        return this.database;
    }


}
