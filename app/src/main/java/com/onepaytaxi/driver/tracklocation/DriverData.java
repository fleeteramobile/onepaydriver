package com.onepaytaxi.driver.tracklocation;

import com.google.android.gms.maps.model.Marker;

import java.util.List;

/**
 * Getter/setter class to hold the driver details.
 */
//
public class DriverData {
    public String DriverId, DriverName, minFare, aboveKm, minKm, belowaboveKm, taxiNo, taxiImg, cityId, markerId, taxiId, taxiModel, distanceaway;
    public String Speed, Nearest, DriverPhoto, Lat, Lng, Company, DriverPhone, Tax, baseFare, canFare, belowKm;
    int rating;
    Marker m;
    List<String> list = null;

    public DriverData(String _DriverId, String _Drivername, String _Speed, String _Lat, String _Lng, String _Nearest, String _distanceaway, Marker _marker, List<String> _list) {
        DriverId = _DriverId;
        DriverName = _Drivername;
        Speed = _Speed;
        Lat = _Lat;
        Lng = _Lng;
        Nearest = _Nearest;
        distanceaway = _distanceaway;
        m = _marker;
        list = _list;
    }

    public String getNearest() {
        return Nearest;
    }

    public void setNearest(String nearest) {
        Nearest = nearest;
    }

    public String getSpeed() {
        return Speed;
    }

    public void setSpeed(String speed) {
        Speed = speed;
    }

    public Marker getM() {
        return m;
    }

    public void setM(Marker m) {
        this.m = m;
    }

    public String getDistanceaway() {
        return distanceaway;
    }

    public void setDistanceaway(String distanceaway) {
        this.distanceaway = distanceaway;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTaxiModel() {
        return taxiModel;
    }

    public void setTaxiModel(String taxiModel) {
        this.taxiModel = taxiModel;
    }

    public String getTaxiId() {
        return taxiId;
    }

    public void setTaxiId(String taxiId) {
        this.taxiId = taxiId;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }

    public String getDriverPhone() {
        return DriverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        DriverPhone = driverPhone;
    }

    public String getDriverId() {
        return DriverId;
    }

    public void setDriverId(String driverId) {
        DriverId = driverId;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public String getDriverPhoto() {
        return DriverPhoto;
    }

    public void setDriverPhoto(String driverPhoto) {
        DriverPhoto = driverPhoto;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLng() {
        return Lng;
    }

    public void setLng(String lng) {
        Lng = lng;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getTax() {
        return Tax;
    }

    public void setTax(String tax) {
        Tax = tax;
    }

    public String getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(String baseFare) {
        this.baseFare = baseFare;
    }

    public String getMinFare() {
        return minFare;
    }

    public void setMinFare(String minFare) {
        this.minFare = minFare;
    }

    public String getCanFare() {
        return canFare;
    }

    public void setCanFare(String canFare) {
        this.canFare = canFare;
    }

    public String getBelowKm() {
        return belowKm;
    }

    public void setBelowKm(String belowKm) {
        this.belowKm = belowKm;
    }

    public String getAboveKm() {
        return aboveKm;
    }

    public void setAboveKm(String aboveKm) {
        this.aboveKm = aboveKm;
    }

    public String getMinKm() {
        return minKm;
    }

    public void setMinKm(String minKm) {
        this.minKm = minKm;
    }

    public String getBelowaboveKm() {
        return belowaboveKm;
    }

    public void setBelowaboveKm(String belowaboveKm) {
        this.belowaboveKm = belowaboveKm;
    }

    public String getTaxiNo() {
        return taxiNo;
    }

    public void setTaxiNo(String taxiNo) {
        this.taxiNo = taxiNo;
    }

    public String getTaxiImg() {
        return taxiImg;
    }

    public void setTaxiImg(String taxiImg) {
        this.taxiImg = taxiImg;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}