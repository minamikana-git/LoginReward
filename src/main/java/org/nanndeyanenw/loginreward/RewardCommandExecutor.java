package org.nanndeyanenw.loginreward;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RewardCommandExecutor implements CommandExecutor {

    private final LoginReward loginReward;

    public RewardCommandExecutor(LoginReward loginReward) {
        this.loginReward = loginReward;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        // コマンド処理の実装
        return true; // または必要に応じて適切な戻り値を設定
    }
}

