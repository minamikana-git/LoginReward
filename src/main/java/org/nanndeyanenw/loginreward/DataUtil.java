package org.nanndeyanenw.loginreward;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class DataUtil {

    private static FileConfiguration config;
    private static File file;

    public static void setConfig(FileConfiguration config) {
        DataUtil.config = config;
    }

    public static void setFile(File file) {
        DataUtil.file = file;
    }


    public static int getDaysLoggedIn(Player player) {
        return dataConfig.getInt(player.getUniqueId().toString() + ".daysLoggedIn", 0);
    }

    public static void incrementDaysLoggedIn(Player player) {
        int currentDays = getDaysLoggedIn(player);
        dataConfig.set(player.getUniqueId().toString()+".daysLoggedIn",currentDays + 1);
        try {
            dataConfig.save(dataFile);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resetDaysLoggedIn(Player player){
        dataConfig.set(player.getUniqueId().toString() + ".daysLoggedIn", 0);
        try {
            dataConfig.save(dataFile);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static YamlConfiguration dataConfig;
    public static File dataFile;

    private final LoginReward plugin;

    public DataUtil(LoginReward plugin) {
        this.plugin = plugin;
        setup();
    }

    private void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        file = new File(plugin.getDataFolder(), "playerdata.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static void set(String path, Object value) {
        config.set(path, value);
        save();
    }
    public static Object get(String path) {
        return config.get(path);
    }

    public boolean contains(String path) {
        return config.contains(path);
    }

    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static double getDouble(String path) {
        return config.getDouble(path);
    }

    public static ConfigurationSection getConfigurationSection(String path) {
        return config.getConfigurationSection(path);
    }


}
