package org.nanndeyanenw.loginreward;



public class Reward {

    private final int amount;
    private final String message;

    public Reward(int amount, String message) {
        this.amount = amount;
        this.message = message;
    }

    public int getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }
}