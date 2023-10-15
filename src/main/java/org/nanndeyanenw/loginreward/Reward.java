package org.nanndeyanenw.loginreward;

import org.bukkit.inventory.ItemStack;

public class Reward {
    private final ItemStack item;
    private final double money;
    private final String message;

    // アイテム報酬用のコンストラクタ
    public Reward(ItemStack item, String message) {
        this.item = item;
        this.money = 0;
        this.message = message;
    }

    // お金報酬用のコンストラクタ
    public Reward(double money, String message) {
        this.item = null;
        this.money = money;
        this.message = message;
    }

    public ItemStack getItem() {
        return item;
    }

    public double getMoney() {
        return money;
    }

    public String getMessage() {
        return message;
    }

    public boolean isItemReward() {
        return item != null;
    }

    public static Reward determineRewardForDays(int days) {

        switch (days) {
            case 1:
                return new Reward(50, "連続ログインボーナス！: 50NANDE!");
            case 2:
                return new Reward(100, "2 連続ログインボーナス！: 100NANDE!");
            case 3:
                return new Reward(200, "3 連続ログインボーナス！: 200NANDE!");
            case 4:
                return new Reward(400, "4 連続ログインボーナス！: 400NANDE!");
            case 5:
                return new Reward(600, "5 連続ログインボーナス！: 600NANDE!");
            case 6:
                return new Reward(800, "6 連続ログインボーナス！: 800NANDE!");
            case 7:
                return new Reward(1000, "7 連続ログインボーナス！: 1000NANDE!");
            default:
                return null;
        }
    }
}

