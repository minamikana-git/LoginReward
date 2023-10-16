package org.nanndeyanenw.loginreward;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;
import org.bukkit.Sound;

public class RewardGUI implements Listener {

    private LoginReward plugin;

    public RewardGUI(LoginReward plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // TODO: Show the GUI to the player
    }

    // TODO: Create the GUI logic

    public void open(Player player) {
        // TODO: Open the GUI for the player
        // 例: player.openInventory(createGuiInventory());
    }

}
