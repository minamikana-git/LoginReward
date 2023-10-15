package org.nanndeyanenw.loginreward;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RewardManager {
    private final LoginReward plugin;
    private final Map<UUID, Integer> consecutiveLogins = new HashMap<>(); // UUIDを使用してプレイヤーを識別
    private final File dataFile;

    public RewardManager(LoginReward plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "loginRewards.yml");
        loadData();
    }

    public void loadData() {
        if (!dataFile.exists()) return;

        FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        for (String uuidString : dataConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            int days = dataConfig.getInt(uuidString + ".days");
            LocalDate lastLogin = LocalDate.parse(dataConfig.getString(uuidString + ".lastLoginDate"));

            if (lastLogin.isBefore(LocalDate.now().minusDays(1))) {
                days = 1;  // 日数をリセット
            }
            consecutiveLogins.put(uuid, days);
        }
    }

    public void saveData() {
        FileConfiguration dataConfig = new YamlConfiguration();

        for (Map.Entry<UUID, Integer> entry : consecutiveLogins.entrySet()) {
            dataConfig.set(entry.getKey().toString() + ".days", entry.getValue());
            dataConfig.set(entry.getKey().toString() + ".lastLoginDate", LocalDate.now().toString());
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ... 他のメソッド ...
}


    // その他の関連メソッドをここに追加することができます。




