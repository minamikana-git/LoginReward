package org.nanndeyanenw.loginreward;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RewardManager {
    private final LoginReward plugin;

    private final File dataFile;

    private final Map<UUID, Integer> consecutiveLogins = new HashMap<>(); // Playerの代わりにUUIDを使用
    private final Map<UUID, LocalDate> lastLoginDates = new HashMap<>(); // Playerの代わりにUUIDを使用


    public RewardManager(LoginReward plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "loginRewards.yml");
        loadData();
    }
    public void loadData() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        for (String uuidString : config.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            int days = config.getInt(uuidString + ".days");
            String lastLoginStr = config.getString(uuidString + ".lastLogin");
            LocalDate lastLogin = LocalDate.parse(lastLoginStr);
            consecutiveLogins.put(uuid, days);
            lastLoginDates.put(uuid, lastLogin);
        }
    }

    public void saveData() {
        YamlConfiguration config = new YamlConfiguration();
        for (UUID uuid : consecutiveLogins.keySet()) {
            config.set(uuid.toString() + ".days", consecutiveLogins.get(uuid));
            config.set(uuid.toString() + ".lastLogin", lastLoginDates.get(uuid).toString());
        }
        try {
            config.save(dataFile); // dataFile に保存
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void updateLoginDate(Player player) {
        UUID playerUUID = player.getUniqueId();
        LocalDate today = LocalDate.now();

        if (lastLoginDates.containsKey(playerUUID)) {
            LocalDate lastLoginDate = lastLoginDates.get(playerUUID);

            if (today.isAfter(lastLoginDate.plusDays(1))) {
                consecutiveLogins.put(playerUUID, 1);
            } else {
                consecutiveLogins.put(playerUUID, consecutiveLogins.getOrDefault(playerUUID, 0) + 1);
            }
        } else {
            consecutiveLogins.put(playerUUID, 1);
        }
        lastLoginDates.put(playerUUID, today);
    }

}





