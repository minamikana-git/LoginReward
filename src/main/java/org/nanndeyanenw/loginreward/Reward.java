package org.nanndeyanenw.loginreward;

import org.bukkit.inventory.ItemStack;

public class Reward {
    private final ItemStack item;
    private final String message;

    public Reward(ItemStack item, String message) {
        this.item = item;
        this.message = message;
    }

    public ItemStack getItem() {
        return item;
    }

    public String getMessage() {
        return message;
    }
}
