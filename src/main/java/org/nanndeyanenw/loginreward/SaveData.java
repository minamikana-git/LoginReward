package org.nanndeyanenw.loginreward;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class SaveData {

    private static LoginReward plugin;
    private static FileConfiguration dataConfig;
    private static DataUtil dataUtil;
    private static Map<UUID,Double> playerMoney;

    private static File dataFile;

    public SaveData(LoginReward plugin,FileConfiguration dataConfig, DataUtil dataUtil, Map<UUID,Double>playerMoney, File dataFile){
        this.plugin = plugin;
        this.dataConfig = dataConfig;
        this.dataUtil = dataUtil;
        this.playerMoney = playerMoney;
        this.dataFile = dataFile;

    }
    public static void saveDataConfig() {
        try {
            dataConfig.save(dataFile); // dataFile が適切に初期化されていることを確認
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player data to " + dataFile);
            e.printStackTrace();
        }
    }

    public void loadData() {
        if (dataUtil.contains("playerMoney")) {
            playerMoney.clear();
            for (String uuidString : dataUtil.getConfigurationSection("playerMoney").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                double money = dataUtil.getDouble("playerMoney." + uuidString);
                playerMoney.put(uuid, money);
            }
        }
    }

    public static void saveData() {
        for (UUID uuid : playerMoney.keySet()) {
            dataUtil.set("playerMoney." + uuid.toString(), playerMoney.get(uuid));
        }
        // 変更をディスクに保存する
        dataUtil.save();  // 引数なしで呼び出す
    }

    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        // ログイン日数をインクリメント
        incrementLoginDays(player);

        // dataConfigに変更があったので保存
        saveDataConfig();
    }

    public void incrementLoginDays(Player player) {
        // プレイヤーのUUIDをキーとして使用
        String playerUUID = player.getUniqueId().toString();

        // 既にデータがある場合はその値を取得、なければ0を返す
        int currentDays = dataConfig.getInt(playerUUID + ".days", 0);

        // ログイン日数をインクリメント
        int updatedDays = currentDays + 1;

        // 更新したログイン日数をdataConfigに保存
        dataConfig.set(playerUUID + ".days", updatedDays);

        // 今回のログインの日付も保存
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(date);
        dataConfig.set(playerUUID + ".lastLoginDate", formattedDate);
    }
}
