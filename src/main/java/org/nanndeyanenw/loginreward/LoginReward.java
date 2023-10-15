package org.nanndeyanenw.loginreward;


import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;


public class LoginReward extends JavaPlugin {

    private RewardManager rewardManager;

    private Economy economy;

    @Override
    public void onEnable() {
        getCommand("loginreward").setExecutor(new RewardCommandExecutor(this));
        if (!setupEconomy()) { // Vaultセットアップが失敗した場合
            getLogger().severe("エラー：Vaultプラグインが見つかりませんでした。プラグインを無効化します。");
            if (getServer().getPluginManager().getPlugin("Vault") == null) {
                getLogger().severe("エラー：Vaultプラグインが見つかりません。");
            } else {
                getLogger().severe("エラー：Economyサービスプロバイダが見つかりません。");
            }
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.rewardManager = new RewardManager(this);
        rewardManager.loadData();
    }
    @Override
    public void onDisable() {
        rewardManager.saveData();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("エラー：Vaultプラグインが見つかりません。");
            return false;
        }

        economy = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        if (economy == null) {
            getLogger().severe("エラー：Economyサービスプロバイダが見つかりません。");
            return false;
        }
        return true;
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
            return true; // または必要に応じて適切な戻り値を設定
        }
        return true;
    }

    //ログインボーナスのインベントリを開くメソッド
    private void openLoginRewardInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null,9,"ログインボーナス");

        player.openInventory(inventory);
    }
}