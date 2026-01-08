package com.onepaytaxi.driver.utils;

public class ServiceItem {
    private int _id;
    private double service_amount;
    private String status;
    private String service_type;

    public ServiceItem(int _id, double service_amount, String status, String service_type) {
        this._id = _id;
        this.service_amount = service_amount;
        this.status = status;
        this.service_type = service_type;
    }

    public int get_id() {
        return _id;
    }

    public double getService_amount() {
        return service_amount;
    }

    public String getStatus() {
        return status;
    }

    public String getService_type() {
        return service_type;
    }

    @Override
    public String toString() {
        return service_type + " (" + String.format("%.2f", service_amount) + ")";
    }
}

