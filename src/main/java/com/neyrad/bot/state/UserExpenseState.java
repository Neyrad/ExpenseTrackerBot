package com.neyrad.bot.state;

public class UserExpenseState {
    private String category;
    private Integer amount;
    private String description;
    private boolean isReady;

    public UserExpenseState() {
        this.isReady = false;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getAmount() {
        return amount;
    }
    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isReady() {
        return isReady;
    }
    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    public boolean isComplete() {
        return category != null && amount != null && description != null;
    }

    public void reset() {
        this.category = null;
        this.amount = null;
        this.description = null;
        this.isReady = false;
    }
}

