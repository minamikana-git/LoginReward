package org.nanndeyanenw.loginreward;

import org.bukkit.event.Listener;

public class RewardEventListener implements Listener {

    private final RewardManager rewardManager;

    public RewardEventListener(RewardManager rewardManager) {
        this.rewardManager = rewardManager;
    }
