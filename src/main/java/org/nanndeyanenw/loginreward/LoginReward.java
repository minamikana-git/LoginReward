package org.nanndeyanenw.loginreward;


import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class LoginReward extends JavaPlugin {

    private RewardManager rewardManager;

    private Economy economy;
    private File dataFile;

    @Override
    public void onEnable() {
       if (!setupEconomy()) { // 起動時のVault関係があるかどうか
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
        getCommand("loginreward").setExecutor(RewardCommandExecutor(this));
        rewardManager.loadData();
    }

    @Override
    public void onDisable() {
        rewardManager.saveData();
    }

    private boolean setupEconomy() {
        if (!setupEconomy()) { // 起動時のVault関係があるかどうか
            getLogger().severe("エラー：Vaultプラグインが見つかりませんでした。プラグインを無効化します。");

            if (getServer().getPluginManager().getPlugin("Vault") == null) {
                getLogger().severe("エラー：Vaultプラグインが見つかりません。");
            } else {
                getLogger().severe("エラー：Economyサービスプロバイダが見つかりません。");
            }
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
    }
}