package org.nanndeyanenw.loginreward;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

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
            openLoginRewardInventory(player);
            return true;
        }
        return true;
    }
    //ログインボーナスのインベントリを開くメソッド
    private void openLoginRewardInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null,9,"ログインボーナス");

        player.openInventory(inventory);
    }
}

