package net.hotamachisubaru.loginreward

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class RewardManager private constructor(private val plugin: LoginReward) {
    private val playerMoney: Map<UUID, Double> = HashMap()
    val loginReward = Reward(100, "ログインボーナス!")
    private val rewards: Map<Int, Reward> = HashMap()
    private var dataFile: File? = null
    private var dataConfig: YamlConfiguration? = null
    private var econ: Economy? = null

    init {
        setupYAML()
        if (!setupEconomy()) {
            plugin.logger.severe("Vault not found! Disabling plugin...")
            plugin.server.pluginManager.disablePlugin(plugin)
        }
    }

    private fun setupYAML() {
        dataFile = File(plugin.dataFolder, "config.yml")
        if (!dataFile!!.exists()) {
            try {
                dataFile!!.createNewFile()
            } catch (e: IOException) {
                Bukkit.getLogger().severe("config.ymlの生成に失敗しました。")
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile!!)
    }

    private fun saveYAML() {
        try {
            dataConfig!!.save(dataFile!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getConsecutiveDays(player: Player): Int {
        return dataConfig!!.getInt(player.uniqueId.toString() + ".consecutiveDays", 0)
    }

    fun getRewardForConsecutiveDays(days: Int): Reward {
        return getRewardForDay(days)
    }

    fun giveLoginReward(player: Player) {
        if (isConsecutiveLoginBroken(player)) {
            setConsecutiveDays(player, 1)
        }
        val consecutiveDays = getConsecutiveDays(player)
        val reward = getRewardForConsecutiveDays(consecutiveDays)
        if (reward != null) {
            econ!!.depositPlayer(player, reward.amount.toDouble())
            player.sendMessage("§a" + reward.message + " §e" + reward.amount + " 円を獲得しました！")

            // 連続ログイン日数を1増やす (もし7日を超えたら1日目にリセット)
            setConsecutiveDays(player, consecutiveDays % 7 + 1)
        }

        // ログイン日を更新
        setLastLoginDate(player, LocalDate.now())
    }

    fun setConsecutiveDays(player: Player, days: Int) {
        dataConfig!![player.uniqueId.toString() + ".consecutiveDays"] = days
        saveYAML()
    }

    private fun setupEconomy(): Boolean {
        if (plugin.server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
        val rsp = plugin.server.servicesManager.getRegistration(Economy::class.java) ?: return false
        econ = rsp.provider
        return econ != null
    }

    fun getPlayerMoney(uuid: UUID): Double {
        return playerMoney[uuid] ?: 50.0
    }

    fun getRewardForDay(day: Int): Reward {
        return when (day) {
            1 -> Reward(50, "ログインボーナス！: 50NANDE!")
            2 -> Reward(100, "2連続ログインボーナス！: 100NANDE!")
            3 -> Reward(200, "3連続ログインボーナス！: 200NANDE!")
            4 -> Reward(400, "4連続ログインボーナス！: 400NANDE!")
            5 -> Reward(600, "5連続ログインボーナス！: 600NANDE!")
            6 -> Reward(800, "6連続ログインボーナス！: 800NANDE!")
            7 -> Reward(1000, "7連続ログインボーナス！: 1000NANDE!")
            else -> Reward(1000, "7連続ログインボーナス以上！: 1000NANDE!")
        }
    }

    fun hasClaimedReward(player: Player): Boolean {
        // 現在の連続ログイン日数を取得
        val consecutiveDays = getConsecutiveDays(player)
        // 現在の日の報酬を取得
        val rewardForToday = getRewardForConsecutiveDays(consecutiveDays)

        // プレイヤーの所持金を取得
        val playerMoney = getPlayerMoney(player.uniqueId)

        // 現在の日の報酬を既に受け取っている場合、所持金がその報酬の額より多いかどうかを確認
        return if (rewardForToday != null && playerMoney >= rewardForToday.amount) {
            true
        } else false
    }

    fun incrementLoginDays(player: Player) {
        val playerUUID = player.uniqueId.toString()

        // 現在のログイン日数を取得
        val currentDays = dataConfig!!.getInt("$playerUUID.login_days", 0)

        // ログイン日数をインクリメント
        val updatedDays = currentDays + 1

        // 現在の日付を取得
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val formattedDate = dateFormat.format(date)

        // データをYAMLに保存
        dataConfig!!["$playerUUID.login_days"] = updatedDays
        dataConfig!!["$playerUUID.last_login_date"] = formattedDate

        // ファイルに保存
        saveYAML()
    }

    fun setLastLoginDate(player: Player, date: LocalDate) {
        dataConfig!![player.uniqueId.toString() + ".lastLoginDate"] = date.toString()
    }

    fun getLastLoginDate(player: Player): LocalDate? {
        val dateString = dataConfig!!.getString(player.uniqueId.toString() + ".lastLoginDate", null)
        return if (dateString != null) LocalDate.parse(dateString) else null
    }

    fun isConsecutiveLoginBroken(player: Player): Boolean {
        val lastLoginDate = getLastLoginDate(player)
                ?: return true // 初めてのログインとして扱います
        return lastLoginDate.plusDays(1) != LocalDate.now()
    }

    companion object {
        private var instance: RewardManager? = null
        @JvmStatic
        fun getInstance(plugin: LoginReward): RewardManager? {
            if (instance == null) {
                instance = RewardManager(plugin)
            }
            return instance
        }
    }
}
