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
        getCommand("loginreward").setExecutor(new RewardCommandExecutor(this));


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
}
