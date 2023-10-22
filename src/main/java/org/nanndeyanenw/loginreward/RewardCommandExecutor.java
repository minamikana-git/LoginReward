package org.nanndeyanenw.loginreward;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RewardCommandExecutor implements CommandExecutor {

    private final RewardManager rewardManager;
    private LoginReward plugin = new LoginReward();
    private final PlayerDataHandler playerDataHandler;

    DataUtil dataUtilInstance = plugin.getDataUtil;

    public RewardCommandExecutor(LoginReward plugin) {
        this.plugin = plugin;
        this.rewardManager = RewardManager.getInstance(plugin);
        this.playerDataHandler = plugin.getPlayerDataHandler();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!rewardManager.hasClaimedReward(player)) {
                // GUIを表示
                new RewardGUI(plugin, dataUtilInstance).open(player);
            } else {
                player.sendMessage("You have already claimed your reward today!");
            }
        }

             Player player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("debugdate")) {
                player.sendMessage("§a時刻をアップデートしました。");

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String today = sdf.format(new Date());

                // ymlから最後に受け取った日付を取得
                String lastReceived = playerDataHandler.getConfig().getString(player.getUniqueId().toString() + ".lastReceived", "未受取");

                player.sendMessage("今日の日付: " + today);
                player.sendMessage("最後に受け取った日付: " + lastReceived);
                return true;
            }

        if (!rewardManager.hasClaimedReward(player)) {
            // GUIを表示
            plugin.getRewardGUI().open(player);
        } else {
            player.sendMessage("今日の報酬はすでに受け取っています。");
        }

        return true;


    }
}

