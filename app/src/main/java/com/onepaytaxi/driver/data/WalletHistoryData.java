package com.onepaytaxi.driver.data;

public class WalletHistoryData {

    String updated_balance,comments,created_date,amt,sign;

    public String getUpdated_balance() {
        return updated_balance;
    }

    public void setUpdated_balance(String updated_balance) {
        this.updated_balance = updated_balance;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
