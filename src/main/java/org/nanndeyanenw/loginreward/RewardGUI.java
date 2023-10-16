package org.nanndeyanenw.loginreward;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals("Welcome Rewards")) {
            event.setCancelled(true); // Prevents taking items from the GUI

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() == Material.DIAMOND) {
                // TODO: Give the player money here
                player.closeInventory();
                player.sendMessage("You received your reward!");
            }
        }
    }

    public void open(Player player) {
        player.openInventory(createGuiInventory());
    }

    private Inventory createGuiInventory() {
        Inventory inv = Bukkit.createInventory(null, 9, "Welcome Rewards"); // 9 slots titled "Welcome Rewards"

        ItemStack rewardItem = new ItemStack(Material.DIAMOND); // Example reward item
        ItemMeta meta = rewardItem.getItemMeta();
        meta.setDisplayName("Click to get your reward!");
        rewardItem.setItemMeta(meta);

        inv.setItem(4, rewardItem); // Set the reward item in the center slot

        return inv;
    }
}