package org.nanndeyanenw.loginreward;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RewardCommandExecutor implements CommandExecutor {

    private final RewardManager rewardManager;

    private final PlayerDataHandler playerDataHandler;
    private Date debugDate = null;
    private final LoginReward plugin;


    private final DataUtil dataUtilInstance;

    public RewardCommandExecutor(LoginReward plugin) {
        this.plugin = plugin;
        this.rewardManager = RewardManager.getInstance(plugin);
        this.playerDataHandler = plugin.getPlayerDataHandler();
        this.dataUtilInstance = plugin.getDataUtil;  // ここで初期化する
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーからのみ実行できます。");
            return true;
        } else {
            SimpleDateFormat sdf;
            if (cmd.getName().equalsIgnoreCase("setDebugDate")) {
                if (!player.hasPermission("loginreward.setdebugdate")) {
                    player.sendMessage("§cあなたにはこのコマンドを実行する権限がありません。");
                    return true;
                } else if (args.length == 0) {
                    sender.sendMessage("日付をデバッグ用に任意の日付にセットします.");
                    this.debugDate = null;
                    return true;
                } else {
                    sdf = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                        this.debugDate = sdf.parse(args[0]);
                        sender.sendMessage("日付をセットしました: " + args[0]);
                    } catch (ParseException var9) {
                        sender.sendMessage("間違った使い方です. 使い方 yyyy-MM-dd。");
                    }

                    return true;
                }
            } else {
                if (cmd.getName().equalsIgnoreCase("loginreward")) {
                    if (!this.rewardManager.hasClaimedReward(player)) {
                        this.plugin.getRewardGUI().open(player);
                    } else {
                        player.sendMessage("今日の報酬はすでに受け取っています。");
                    }
                }

                return true;
            }
        }
    }
}














