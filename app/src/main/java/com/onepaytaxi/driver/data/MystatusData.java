package com.onepaytaxi.driver.data;

import android.content.Context;

//Getter/setter class to hold the driver status details.
public class MystatusData {
    // timer_data
    public static long saveTime = 0;
    // profile_data
    public static String d_name = "";
    public static String d_lastname = "";
    public static String d_bankname = "";
    public static String d_bankaccountNo = "";
    public static String d_email = "";
    public static String d_mainimagePath = "";
    public static String d_thumbimagePath = "";
    public static String d_address = "";
    public static String d_driverlicenseId = "";
    public static String driverWaitingHr = "";
    // driver status
    public static String p_logid = "";
    public static String s_driverid = "";
    public static String s_latitude = "";
    public static String s_longtitude = "";
    public static String s_status = "";
    public static String s_pickupLoc = "";
    public static String s_dropLoc = "";
    // notification receive
    public static String p_passengerId = "";
    public static String p_tripId = "";
    public static String p_companyId = "";
    public static String p_distance = "";
    public static String p_pickupLoc = "";
    public static String p_taxiId = "";
    public static String p_dropLoc = "";
    public static String p_phoneNo = "";
    public static String p_points = "";
    // notification accept
    public static String m_status = "";
    public static String m_pickupLocation = "";
    public static String m_pickupLatitude = "";
    public static String m_pickupLongitude = "";
    public static String m_driverLatitude = "";
    public static String m_driverLongitude = "";
    public static String m_dropLocation = "";
    public static String m_dropLatitude = "";
    public static String m_dropLongitude = "";
    public static String m_passengername = "";
    public static String m_passengerimage = "";
    public static String m_passengerphone = "";
    public static String m_passengernotes = "";
    public static String m_PassengerdropLocation = "";

    public static String getPickup_notes() {
        return pickup_notes;
    }

    public static void setPickup_notes(String pickup_notes) {
        MystatusData.pickup_notes = pickup_notes;
    }

    public static String getDropoff_notes() {
        return dropoff_notes;
    }

    public static void setDropoff_notes(String dropoff_notes) {
        MystatusData.dropoff_notes = dropoff_notes;
    }

    public static String pickup_notes = "";
    public static String dropoff_notes = "";

    public String getDriverWaitingHr() {
        return driverWaitingHr;
    }

    public void setDriverWaitingHr(String driverWaitingHr) {
        MystatusData.driverWaitingHr = driverWaitingHr;
    }

    // saveTime
    public long getsaveTime() {
        return saveTime;
    }

    public void setsaveTime(long t_saveTime) {
        saveTime = t_saveTime;
    }

    // d_driverlicenseId
    public String getDdriverlicenseId() {
        return d_driverlicenseId;
    }

    public void setDdriverlicenseId(String driverlicenseId) {
        d_driverlicenseId = driverlicenseId;
    }

    // d_address
    public String getDaddress() {
        return d_address;
    }

    public void setDaddress(String address) {
        d_address = address;
    }

    // d_thumbimagePath
    public String getDthumbimagePath() {
        return d_thumbimagePath;
    }

    public void setDthumbimagePath(String thumbimagePath) {
        d_thumbimagePath = thumbimagePath;
    }

    // d_mainimagePath
    public String getDmainimagePath() {
        return d_mainimagePath;
    }

    public void setDmainimagePath(String mainimagePath) {
        d_mainimagePath = mainimagePath;
    }

    // d_email
    public String getDemail() {
        return d_email;
    }

    public void setDemail(String email) {
        d_email = email;
    }

    // d_bankaccountNo
    public String getDbankaccountNo() {
        return d_bankaccountNo;
    }

    public void setDbankaccountNo(String bankaccountNo) {
        d_bankaccountNo = bankaccountNo;
    }

    // d_bankname
    public String getDbankname() {
        return d_bankname;
    }

    public void setDbankname(String bankname) {
        d_bankname = bankname;
    }

    // d_lastname
    public String getDlastname() {
        return d_lastname;
    }

    public void setDlastname(String lastname) {
        d_lastname = lastname;
    }

    // d_name
    public String getDname() {
        return d_name;
    }

    public void setDname(String name) {
        d_name = name;
    }

    // ongoing status
    public static String m_ongoingsts = "";
    Context ctx;
    int Status;

    public MystatusData(Context con) {
        this.ctx = con;
    }

    // m_passengername
    public String getOnpassengerName() {
        return m_passengername;
    }

    public void setOnpassengerName(String passengername) {
        m_passengername = passengername;
    }

    // m_ongoingsts
    public String getOngoingsts() {
        return m_ongoingsts;
    }

    public void setOngoingsts(String ongoingsts) {
        m_ongoingsts = ongoingsts;
    }

    // m_status
    public String getOnstatus() {
        return m_status;
    }

    public void setOnstatus(String status) {
        m_status = status;
    }

    // m_pickupLocation
    public String getOnpickupLocation() {
        return m_pickupLocation;
    }

    public void setOnpickupLocation(String pickupLocation) {
        m_pickupLocation = pickupLocation;
    }

    // m_pickupLatitude
    public String getOnpickupLatitude() {
        return m_pickupLatitude;
    }

    public void setOnpickupLatitude(String pickupLatitude) {
        m_pickupLatitude = pickupLatitude;
    }

    // m_driverLatitude
    public String getOndriverLatitude() {
        return m_driverLatitude;
    }

    public void setOndriverLatitude(String driverLatitude) {
        m_driverLatitude = driverLatitude;
    }

    // m_driverLongitude
    public String getOndriverLongitude() {
        return m_driverLongitude;
    }

    public void setOndriverLongitude(String driverLongitude) {
        m_driverLongitude = driverLongitude;
    }

    // m_pickupLongitude
    public String getOnpickupLongitude() {
        return m_pickupLongitude;
    }

    public void setOnpickupLongitude(String pickupLongitude) {
        m_pickupLongitude = pickupLongitude;
    }

    // m_dropLocation
    public String getOndropLocation() {
        return m_dropLocation;
    }

    public void setOndropLocation(String dropLocation) {
        m_dropLocation = dropLocation;
    }

    // m_PassengerdropLocation
    public String getPassengerOndropLocation() {
        return m_PassengerdropLocation;
    }

    public void setPassengerOndropLocation(String dropLocation) {
        m_PassengerdropLocation = dropLocation;
    }

    // m_dropLatitude
    public String getOndropLatitude() {
        return m_dropLatitude;
    }

    public void setOndropLatitude(String dropLatitude) {
        m_dropLatitude = dropLatitude;
    }

    // m_dropLongitude
    public String getOndropLongitude() {
        return m_dropLongitude;
    }

    public void setOndropLongitude(String dropLongitude) {
        m_dropLongitude = dropLongitude;
    }

    // m_passengerimage
    public String getOnPassengerImage() {
        return m_passengerimage;
    }

    public void setOnPassengerImage(String passengerimage) {
        m_passengerimage = passengerimage;
    }

    // set&get p_dropLoc
    public void setdropLoc(String p_dropLoc) {
        MystatusData.p_dropLoc = p_dropLoc;
    }

    public String getdropLoc() {
        return p_dropLoc;
    }

    // set&get p_taxiId
    public void settaxiId(String p_taxiId) {
        MystatusData.p_taxiId = p_taxiId;
    }

    public String gettaxiId() {
        return p_taxiId;
    }

    // set&get p_phoneNo
    public void setphoneNo(String p_phoneno) {
        p_phoneNo = p_phoneno;
    }

    public String getphoneNo() {
        return p_phoneNo;
    }

    // set&get p_pickupLoc
    public void setpickupLoc(String p_pickupLoc) {
        MystatusData.p_pickupLoc = p_pickupLoc;
    }

    public String getpickupLoc() {
        return p_pickupLoc;
    }

    // set&get p_distance
    public void setdistance(String p_distance) {
        MystatusData.p_distance = p_distance;
    }

    public String getdistance() {
        return p_distance;
    }

    // set&get p_points
    public void setOnwaypoints(String p_Points) {
        p_points = p_Points;
    }

    public String getOnwaypoints() {
        return p_points;
    }

    // set&get p_companyId
    public void setcompanyId(String p_companyId) {
        MystatusData.p_companyId = p_companyId;
    }

    public String getcompanyId() {
        return p_companyId;
    }

    // set&get p_tripId
    public void settripId(String p_tripId) {
        MystatusData.p_tripId = p_tripId;
    }

    public String gettripId() {
        return p_tripId;
    }

    // set&get p_passengerId
    public void setpassengerId(String p_passengerId) {
        MystatusData.p_passengerId = p_passengerId;
    }

    public String getpassengerId() {
        return p_passengerId;
    }

    // set&get logid
    public void setPlogId(String p_Logid) {
        p_logid = p_Logid;
    }

    public String getPlogId() {
        return p_logid;
    }

    // set&get s_latitude
    public void setDriver_id(String s_Driverid) {
        s_driverid = s_Driverid;
    }

    public String getDriver_id() {
        return s_driverid;
    }

    // set&get s_latitude
    public void setLatitude(String s_Latitude) {
        s_latitude = s_Latitude;
    }

    public String getLatitude() {
        return s_latitude;
    }

    // set&get s_longtitude
    public void setLongtitude(String s_Longtitude) {
        s_longtitude = s_Longtitude;
    }

    public String getLongtitude() {
        return s_longtitude;
    }

    // set&get s_status
    public void setStatus(String s_Status) {
        s_status = s_Status;
    }

    public String getStatus() {
        return s_status;
    }

    // set&get s_pickupLoc
    public void setPickupLoc(String s_PickupLoc) {
        s_pickupLoc = s_PickupLoc;
    }

    public String PickupLoc() {
        return s_pickupLoc;
    }

    // set&get s_pickupLoc
    public void setDropLoc(String s_DropLoc) {
        s_dropLoc = s_DropLoc;
    }

    public String DropLoc() {
        return s_dropLoc;
    }

    // set & get passenger phone
    public void setpassengerphone(String phone) {
        m_passengerphone = phone;
    }

    public String getpassengerphone() {
        return m_passengerphone;
    }

    // set & get passenger phone
    public void setpassengerNotes(String note) {
        m_passengernotes = note;
    }

    public String getpassengerNotes() {
        return m_passengernotes;
    }
}
