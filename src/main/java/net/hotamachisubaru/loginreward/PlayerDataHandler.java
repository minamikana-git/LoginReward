package net.hotamachisubaru.loginreward;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PlayerDataHandler {
        private File file;
        private FileConfiguration config;

        public PlayerDataHandler(File dataFolder, String fileName) {
            this.file = new File(dataFolder, fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Bukkit.getLogger().severe("ファイルを作成できませんでした。");
                }
            }
            this.config = YamlConfiguration.loadConfiguration(file);
        }


    public void saveConfig() {
            try {
                config.save(file);
            } catch (IOException e) {
                Bukkit.getLogger().severe("設定ファイルを保存できませんでした。");
            }
        }

        public FileConfiguration getConfig() {
            return config;
        }
}