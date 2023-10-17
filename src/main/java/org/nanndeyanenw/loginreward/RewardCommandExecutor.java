package org.nanndeyanenw.loginreward;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RewardCommandExecutor implements CommandExecutor {

    private final RewardManager rewardManager;
    private final LoginReward plugin;

    public RewardCommandExecutor(LoginReward plugin) {
        this.plugin = plugin;
        this.rewardManager = RewardManager.getInstance(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // 新しいコマンド "debugdate" を追加
            if (cmd.getName().equalsIgnoreCase("debugdate")) {
                if (!player.hasPermission("loginreward.debugdate")){
                    player.sendMessage("§cあなたにはこのコマンドを実行する権限がありません。");
                    return true;
                }

                player.sendMessage("§a時刻をアップデートしました。");

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String today = sdf.format(new Date());
                String lastReceived = plugin.getPlayerDataConfig().getString(player.getUniqueId().toString() + ".lastReceived", "Not Found");

                player.sendMessage("今日の日付: " + today);
                player.sendMessage("最後に受け取った日付: " + lastReceived);
                return true;
            }
            if (!rewardManager.hasClaimedReward(player)) {
                // GUIを表示
                new RewardGUI(plugin).open(player);
            } else {
                player.sendMessage("今日の報酬はすでに受け取っています。");
            }
            return true;
        }
        return false;
    }
}
