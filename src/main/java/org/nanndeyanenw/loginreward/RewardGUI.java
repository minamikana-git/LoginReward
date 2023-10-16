package org.nanndeyanenw.loginreward;

import net.milkbowl.vault.economy.Economy;
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
    private Economy econ; // VaultAPIのEconomy

    public RewardGUI(LoginReward plugin) {
        this.plugin = plugin;
        if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
            econ = plugin.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        open(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getView().getTitle().equals("Welcome Rewards")) {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.DIAMOND) {
                Player player = (Player) event.getWhoClicked();
                giveReward(player);
                player.closeInventory();
                event.setCancelled(true);
            }
        }
    }

    private void giveReward(Player player) {
        // ここでは報酬のロジックを実装します。
        // 例えば、プレイヤーが何日ログインしたかに基づいて報酬を決定する。

        int daysLoggedIn = 0; // この情報をどこかから取得する必要があります。
        double rewardAmount;

        switch (daysLoggedIn) {
            case 1: rewardAmount = 50; break;
            case 2: rewardAmount = 100; break;
            case 3: rewardAmount = 200; break;
            case 4: rewardAmount = 400; break;
            case 5: rewardAmount = 600; break;
            case 6: rewardAmount = 800; break;
            case 7: rewardAmount = 1000; break;
            default: rewardAmount = 1000; break;
        }

        econ.depositPlayer(player, rewardAmount);
        player.sendMessage("You've received " + rewardAmount + " as your login reward!");
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