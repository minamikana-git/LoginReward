package org.nanndeyanenw.loginreward;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RewardGUI implements Listener {

    private final String title = "ログイン報酬";
    private final int size = 9 * 3; // 3 rows, modify as needed

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, size, title);

        // For demonstration, let's add a simple reward for each day
        for (int i = 1; i <= 7; i++) {
            ItemStack rewardItem = new ItemStack(Material.DIAMOND, i);
            ItemMeta meta = rewardItem.getItemMeta();
            meta.setDisplayName("日の " + i + "報酬");
            rewardItem.setItemMeta(meta);

            gui.setItem(i - 1, rewardItem); // placing in slots 0-6
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void handleClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || !event.getView().getTitle().equals(title)) {
            return;
        }

        event.setCancelled(true); // prevent taking items from the GUI

        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();

        // Let's pretend we're giving a reward for the first 7 slots only
        if (slot >= 0 && slot < 7) {
            player.sendMessage("ログイン報酬をゲットしました。" + (slot + 1));



            player.closeInventory();
        }
    }
}

