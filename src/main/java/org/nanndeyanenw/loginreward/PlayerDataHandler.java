package org.nanndeyanenw.loginreward;

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
                    e.printStackTrace();
                }
            }
            this.config = YamlConfiguration.loadConfiguration(file);
        }

        public void saveConfig() {
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public FileConfiguration getConfig() {
            return config;
        }
}