package org.nanndeyanenw.loginreward;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class SaveData {

    private LoginReward plugin;
    private FileConfiguration dataConfig;
    private DataUtil dataUtil;
    private Map<UUID,Double> playerMoney;

    public SaveData(LoginReward plugin,FileConfiguration dataConfig, DataUtil dataUtil, Map<UUID,Double>playerMoney){
        this.plugin = plugin;
        this.dataConfig = dataConfig;
        this.dataUtil = dataUtil;
        this.playerMoney = playerMoney;
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

    public void saveData() {
        for (UUID uuid : playerMoney.keySet()) {
            dataUtil.set("playerMoney." + uuid.toString(), playerMoney.get(uuid)); // DataUtil から dataUtil に変更
        }
        // 変更をディスクに保存する
        try {
            dataUtil.save(dataFile);  // DataUtil から dataUtil に変更
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data to " + dataFile);
            e.printStackTrace();
        }
    }

    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        // ログイン日数をインクリメント
        incrementLoginDays(player);

        // dataConfigに変更があったので保存
        saveDataConfig();
    }

    public static void incrementLoginDays(Player player) {
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
