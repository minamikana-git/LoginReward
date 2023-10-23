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
        if (cmd.getName().equalsIgnoreCase("loginreward")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cこのコマンドはプレイヤーからのみ実行できます。");
                return true;
            }
        }
        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("setDebugDate")) {
            if (!player.hasPermission("loginreward.setdebugdate")) {
                player.sendMessage("§cあなたにはこのコマンドを実行する権限がありません。");
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage("日付をデバッグ用に任意の日付にセットします.");
                debugDate = null;
                return true;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                debugDate = sdf.parse(args[0]);
                sender.sendMessage("日付をセットしました: " + args[0]);

            } catch (ParseException e) {
                sender.sendMessage("間違った使い方です. 使い方 yyyy-MM-dd。");
            }
            return true;
        }
        return false;
    }
}












