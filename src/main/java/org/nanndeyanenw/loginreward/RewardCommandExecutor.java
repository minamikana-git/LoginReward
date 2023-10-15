package org.nanndeyanenw.loginreward;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
public class RewardCommandExecutor implements CommandExecutor {

    private final LoginReward loginReward;

    public RewardCommandExecutor(LoginReward loginReward) {
        this.loginReward = loginReward;
    }



    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("loginreward")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("§cこのコマンドはプレイヤーからのみ実行できます。");
                return true;
            }
            // プレイヤーとしての処理を続ける
            Player player = (Player) commandSender;
            // ここでプレイヤーに対する処理を実行
            return true; // または必要に応じて適切な戻り値を設定
        }
        return true;
    }
}

