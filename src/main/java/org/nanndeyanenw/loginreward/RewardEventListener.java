package org.nanndeyanenw.loginreward;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RewardEventListener implements Listener {

    private final RewardManager rewardManager;

    public RewardEventListener(RewardManager rewardManager) {
        this.rewardManager = rewardManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!rewardManager.hasClaimedReward(player)) {
            new RewardGUI(player, rewardManager).open();
            rewardManager.claimReward(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        rewardManager.resetClaim(player);
    }
}