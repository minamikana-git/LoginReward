package org.nanndeyanenw.loginreward;

import org.bukkit.entity.Player;
import static org.nanndeyanenw.loginreward.Reward.determineRewardForDays;

public class RewardGUI {
    private final Player player;
    private final RewardManager rewardManager;

    public RewardGUI(Player player, RewardManager rewardManager) {
        this.player = player;
        this.rewardManager = rewardManager;
    }

    public void open() {
            private final Player player;
            private final RewardManager rewardManager;

            public RewardGUI(Player player, RewardManager rewardManager) {
                this.player = player;
                this.rewardManager = rewardManager;
            }

            public void open() {
                // GUIを開くロジックをここに書く
                if (event.getClickedInventory() == null || !event.getView().getTitle().equals(title)) {
                    return;
                }
                event.setCancelled(true);
                int slot = event.getSlot();
                Player player = (Player) event.getWhoClicked();
                if (slot >= 0 && slot < 7) {
                    int consecutiveDays = slot + 1;
                    player.sendMessage("ログイン報酬をゲットしました。" + consecutiveDays);
                    // ここで報酬をプレイヤーに付与
                    Reward reward = determineRewardForDays(consecutiveDays);
                    if (reward != null && reward.getMoney() > 0) {
                        double moneyAmount = reward.getMoney();
                        economy.depositPlayer(player, moneyAmount); // Vaultを使用してお金をプレイヤーに追加

                        player.sendMessage("報酬を受け取りました！: " + moneyAmount + "NANDE!");
                    }
                }
                player.closeInventory();
            }
            public static Reward determineRewardForDays(int days) {
                return switch (days) {
                    case 1 -> new Reward(50, "ログインボーナス！: 50NANDE!");
                    case 2 -> new Reward(100, "2連続ログインボーナス！: 100NANDE!");
                    case 3 -> new Reward(200, "3連続ログインボーナス！: 200NANDE!");
                    case 4 -> new Reward(400, "4連続ログインボーナス！: 400NANDE!");
                    case 5 -> new Reward(600, "5連続ログインボーナス！: 600NANDE!");
                    case 6 -> new Reward(800, "6連続ログインボーナス！: 800NANDE!");
                    case 7 -> new Reward(1000, "7連続ログインボーナス！: 1000NANDE!");
                    default -> null;
                };
            }
    }
 }
