package net.hotamachisubaru.loginreward

import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

class PlayerDataHandler(dataFolder: File?, fileName: String?) {
    private val file: File
    @JvmField
    val config: FileConfiguration

    init {
        file = File(dataFolder, fileName)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                Bukkit.getLogger().severe("ファイルを作成できませんでした。")
            }
        }
        config = YamlConfiguration.loadConfiguration(file)
    }

    fun saveConfig() {
        try {
            config.save(file)
        } catch (e: IOException) {
            Bukkit.getLogger().severe("設定ファイルを保存できませんでした。")
        }
    }
}