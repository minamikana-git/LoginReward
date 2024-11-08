package net.hotamachisubaru.loginreward

import net.hotamachisubaru.loginreward.RewardManager.Companion.getInstance
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RewardCommandExecutor(private val plugin: LoginReward) : CommandExecutor {
    private val rewardManager: RewardManager?
    private val playerDataHandler: PlayerDataHandler
    private var debugDate: Date? = null
    private val dataUtilInstance: DataUtil

    init {
        rewardManager = getInstance(plugin)
        playerDataHandler = plugin.playerDataHandler
        dataUtilInstance = plugin.getDataUtil // ここで初期化する
    }

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        return if (sender !is Player) {
            sender.sendMessage("§cこのコマンドはプレイヤーからのみ実行できます。")
            true
        } else {
            val sdf: SimpleDateFormat
            if (cmd.name.equals("setDate", ignoreCase = true)) {
                if (!sender.hasPermission("loginreward.setdate")) {
                    sender.sendMessage("§cあなたにはこのコマンドを実行する権限がありません。")
                    true
                } else if (args.size == 0) {
                    sender.sendMessage("日付をデバッグ用に任意の日付にセットします.")
                    debugDate = null
                    true
                } else {
                    sdf = SimpleDateFormat("yyyy-MM-dd")
                    try {
                        debugDate = sdf.parse(args[0])
                        sender.sendMessage("日付をセットしました: " + args[0])
                    } catch (var9: ParseException) {
                        sender.sendMessage("間違った使い方です. 使い方 yyyy-MM-dd。")
                    }
                    true
                }
            } else {
                if (cmd.name.equals("loginreward", ignoreCase = true)) {
                    if (!rewardManager!!.hasClaimedReward(sender)) {
                        plugin.rewardGUI.open(sender)
                    } else {
                        sender.sendMessage("今日の報酬はすでに受け取っています。")
                    }
                }
                true
            }
        }
    }
}
