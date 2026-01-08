package com.onepaytaxi.driver.data.apiData;

public class StatementData {
    public String getWallet_item() {
        return wallet_item;
    }

    public void setWallet_item(String wallet_item) {
        this.wallet_item = wallet_item;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdded_amount() {
        return added_amount;
    }

    public void setAdded_amount(String added_amount) {
        this.added_amount = added_amount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    String wallet_item;
    String description;
    String added_amount;
    String balance;
    String createdate;
    String plus_minus;

    public String getPlus_minus() {
        return plus_minus;
    }

    public void setPlus_minus(String plus_minus) {
        this.plus_minus = plus_minus;
    }

    public String getPassenger_name() {
        return passenger_name;
    }

    public void setPassenger_name(String passenger_name) {
        this.passenger_name = passenger_name;
    }

    String passenger_name;
}
