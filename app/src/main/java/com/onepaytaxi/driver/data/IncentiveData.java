package com.onepaytaxi.driver.data;

public class IncentiveData {
    public String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getIncentive_name() {
        return incentive_name;
    }

    public void setIncentive_name(String incentive_name) {
        this.incentive_name = incentive_name;
    }

    public String getIncentive_amount() {
        return incentive_amount;
    }

    public void setIncentive_amount(String incentive_amount) {
        this.incentive_amount = incentive_amount;
    }

    public String getTime_from() {
        return time_from;
    }

    public void setTime_from(String time_from) {
        this.time_from = time_from;
    }

    public String getTime_to() {
        return time_to;
    }

    public void setTime_to(String time_to) {
        this.time_to = time_to;
    }

    public String getSp_time_from() {
        return sp_time_from;
    }

    public void setSp_time_from(String sp_time_from) {
        this.sp_time_from = sp_time_from;
    }

    public String getSp_time_to() {
        return sp_time_to;
    }

    public void setSp_time_to(String sp_time_to) {
        this.sp_time_to = sp_time_to;
    }

    public String getDriver_availability_range() {
        return driver_availability_range;
    }

    public void setDriver_availability_range(String driver_availability_range) {
        this.driver_availability_range = driver_availability_range;
    }

    public String getDriver_rating_range() {
        return driver_rating_range;
    }

    public void setDriver_rating_range(String driver_rating_range) {
        this.driver_rating_range = driver_rating_range;
    }

    public String getDriver_accept_range() {
        return driver_accept_range;
    }

    public void setDriver_accept_range(String driver_accept_range) {
        this.driver_accept_range = driver_accept_range;
    }

    public String getTrips_range() {
        return trips_range;
    }

    public void setTrips_range(String trips_range) {
        this.trips_range = trips_range;
    }

    public String incentive_name;
    public String incentive_amount;
    public String time_from;
    public String time_to;
    public String sp_time_from;
    public String sp_time_to;
    public String driver_availability_range;
    public String driver_rating_range;
    public String driver_accept_range;
    public String trips_range;

    public String getIs_feature_incentive() {
        return is_feature_incentive;
    }

    public void setIs_feature_incentive(String is_feature_incentive) {
        this.is_feature_incentive = is_feature_incentive;
    }

    public String is_feature_incentive;
}
