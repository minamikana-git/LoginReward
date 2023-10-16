package org.nanndeyanenw.loginreward;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
public class RewardGUI implements Listener {

    private LoginReward plugin;

    public RewardGUI(LoginReward plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        open(player);
    }

    public void open(Player player) {
        player.openInventory(createGuiInventory());
    }

    private Inventory createGuiInventory() {
        Inventory inv = Bukkit.createInventory(null, 9, "ログインボーナス"); // 9 slots titled "Welcome Rewards"

        ItemStack rewardItem = new ItemStack(Material.EMERALD); // Example reward item
        ItemMeta meta = rewardItem.getItemMeta();
        meta.setDisplayName("ログインボーナスをゲットしよう");
        rewardItem.setItemMeta(meta);

        inv.setItem(4, rewardItem); // Set the reward item in the center slot

        return inv;
    }
}
