package net.hotamachisubaru.loginreward

import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.io.IOException

class DataUtil(private val plugin: LoginReward) {
    var dataUtil: DataUtil? = null

    init {
        setup()
    }

    private fun setup() {
        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdir()
        }
        file = File(plugin.dataFolder, "config.yml")
        if (!file!!.exists()) {
            try {
                file!!.createNewFile()
            } catch (e: IOException) {
                Bukkit.getLogger().severe("config.ymlを生成できませんでした。")
            }
        }
    }

    operator fun contains(path: String?): Boolean {
        return config!!.contains(path!!)
    }

    companion object {
        private var config: FileConfiguration? = null
        private var file: File? = null
        fun setConfig(config: FileConfiguration?) {
            Companion.config = config
        }

        fun setFile(file: File?) {
            Companion.file = file
        }

        fun getDaysLoggedIn(player: Player): Int {
            return dataConfig!!.getInt(player.uniqueId.toString() + ".daysLoggedIn", 0)
        }

        fun incrementDaysLoggedIn(player: Player) {
            val currentDays = getDaysLoggedIn(player)
            dataConfig!![player.uniqueId.toString() + ".daysLoggedIn"] = currentDays + 1
            try {
                dataConfig!!.save(dataFile!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun resetDaysLoggedIn(player: Player) {
            dataConfig!![player.uniqueId.toString() + ".daysLoggedIn"] = 0
            try {
                dataConfig!!.save(dataFile!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        var dataConfig: YamlConfiguration? = null
        var dataFile: File? = null
        operator fun set(path: String?, value: Any?) {
            config!![path!!] = value
            save()
        }

        operator fun get(path: String?): Any {
            return config!![path!!]!!
        }

        fun save() {
            try {
                config!!.save(file!!)
            } catch (e: IOException) {
                Bukkit.getLogger().severe("設定ファイルを保存できませんでした。")
            }
        }

        fun getDouble(path: String?): Double {
            return config!!.getDouble(path!!)
        }

        fun getConfigurationSection(path: String?): ConfigurationSection {
            return config!!.getConfigurationSection(path!!)!!
        }
    }
}
