package org.nanndeyanenw.loginreward;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
