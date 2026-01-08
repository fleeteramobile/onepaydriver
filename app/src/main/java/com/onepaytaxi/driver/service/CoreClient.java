package com.onepaytaxi.driver.service;

import com.onepaytaxi.driver.bookings.model.ResponseNewSchduleBooking;
import com.onepaytaxi.driver.bookings.model.ResponsePastBooking;
import com.onepaytaxi.driver.bookings.model.ResponseUpcomingTrips;
import com.onepaytaxi.driver.data.apiData.ApiRequestData;
import com.onepaytaxi.driver.data.apiData.CompanyDomainResponse;
import com.onepaytaxi.driver.data.apiData.DetailInfo;
import com.onepaytaxi.driver.data.apiData.EndStreetPickupResponse;
import com.onepaytaxi.driver.data.apiData.GetTripDetailResponse;
import com.onepaytaxi.driver.data.apiData.SettlementHistoryData;
import com.onepaytaxi.driver.data.apiData.SettlementPaymentData;
import com.onepaytaxi.driver.data.apiData.SettlementReqData;
import com.onepaytaxi.driver.data.apiData.StreetCompleteResponse;
import com.onepaytaxi.driver.data.apiData.StreetPickUpResponse;
import com.onepaytaxi.driver.data.apiData.TripDetailResponse;
import com.onepaytaxi.driver.data.apiData.UpcomingResponse;
import com.onepaytaxi.driver.earnings.ResponseEarnings;
import com.onepaytaxi.driver.tracklocation.NearestDriverDatas;
import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by developer on 8/31/16.
 */

public interface CoreClient {

    @GET("{owner}")
    Call<ResponseBody> coreDetails(@Path(value = "owner", encoded = true) String owner,@Header("Cache-Control") String cacheControl, @Query("type") String url, @Query(value = "gt_lst_time", encoded = true) String encode,@Query(value = "dn", encoded = true) String auth_key);


    @POST("{owner}")
    Call<ResponseBody> updateUser(@Path(value = "owner", encoded = true) String owner, @Body RequestBody body, @Query("type") String url, @Query(value = "lang", encoded = true) String lang);


    @POST("{owner}" + "?type=driver_booking_list")
    Call<UpcomingResponse> callData(@Path(value = "owner", encoded = true) String owner, @Body ApiRequestData.UpcomingRequest body, @Query("lang") String lang);

    @POST("{owner}" + "?type=driver_booking_list")
    Call<UpcomingResponse> callData_(@Path(value = "owner", encoded = true) String owner, @Body ApiRequestData.UpcomingRequest body, @Query("lang") String lang);

    @POST("{owner}" + "?type=get_trip_detail")
    Call<TripDetailResponse> callData(@Path(value = "owner", encoded = true) String owner, @Body ApiRequestData.getTripDetailRequest body, @Query("lang") String lang);

    @GET
    Call<ResponseBody> getWhole(@Header("Cache-Control") String cacheControl, @Url String url);

    /**
     * method to start Street trip
     *
     * @param body StreetPickRequest class object
     * @param lang Language
     * @return returns api result on call back
     */
    @POST("{owner}" + "?type=driver_start_trip")
    Call<StreetPickUpResponse> startStreetTrip(@Path(value = "owner", encoded = true) String owner, @Body ApiRequestData.StreetPickRequest body, @Query("lang") String lang);

    @POST("{owner}" + "?type=street_pickup_end_trip")
    Call<EndStreetPickupResponse> endStreetTrip(@Path(value = "owner", encoded = true) String owner, @Body ApiRequestData.EndStreetPickup body, @Query("lang") String lang);

    @POST("{owner}" + "?type=get_trip_detail")
    Call<GetTripDetailResponse> getTripDetail(@Path(value = "owner", encoded = true) String owner, @Body ApiRequestData.TripDetailRequest body, @Query("lang") String lang);


    @POST("{owner}" + "?type=street_pickup_tripfare_update")
    Call<StreetCompleteResponse> completeStreetPickUpdate(@Path(value = "owner", encoded = true) String owner, @Body ApiRequestData.StreetPickComplete body, @Query("lang") String lang);



    @POST("{owner}" + "?type=driver_earnings")
    Call<ResponseEarnings> callDatanew(@Path(value = "owner", encoded = true) String owner, @Body ApiRequestData.Earnings body);

    @POST("{owner}" + "?type=check_companydomain")
    Call<CompanyDomainResponse> callData(@Path(value = "owner", encoded = true) String owner, @Body ApiRequestData.BaseUrl body);

    @GET
    Call<JsonObject> getJsonbyWholeUrl(@Header("Cache-Control") String cacheControl, @Url String url);


    @POST("?type=settlement_history")
    Call<SettlementHistoryData> settlement_historyCall(@Body ApiRequestData.SettlementHistory body, @Query(value = "lang", encoded = true) String langCode);

    @POST("?type=driver_request_settlement")
    Call<SettlementPaymentData> settlement_paymentCall(@Body ApiRequestData.PaymentReq body, @Query(value = "lang", encoded = true) String langCode);

    @POST("?type=settlement_request_amount")
    Call<SettlementReqData> settlement_reqCall(@Body ApiRequestData.SettlementReq body, @Query(value = "lang", encoded = true) String langCode);


    @POST("driver_location_history_update")
    Call<ResponseBody> nodeUpdate(@Body RequestBody body, @Query(value = "h", encoded = true) String hours,@Query(value = "t", encoded = true) String timeUpdate);


    @POST("drivervisible_list")
    Call<NearestDriverDatas> nodeUpdates(@Body RequestBody body, @Query(value = "lang", encoded = true)String langCode);
    @POST("auth")
    Call<ResponseBody> nodeAuth(@Body RequestBody body);


    @POST
    Call<ResponseBody> urlCheck(@Url String url, @Body RequestBody body);


    @POST
    Call<ResponseBody> detail_infoCall(@Url String url,@Body DetailInfo body, @Query(value = "lang", encoded = true) String langCode);

    @POST("?type=error_logs")
    Call<ResponseBody> errorLogUpdate(@Body RequestBody body);


    @GET
    Call<ResponseBody> getPolylineDataWithWayPoint(@Url String url, @Query("origin") String origin, @Query("destination") String destination, @Query(value = "waypoints", encoded = true) String waypoints, @Query("key") String key);

    @GET
    Call<ResponseBody> getPolylineData(@Url String url, @Query("origin") String origin, @Query("destination") String destination, @Query("alternatives") String alternatives, @Query("key") String key);

    @GET
    Call<Object> requestExplore(@Url String url,@Query("v") String v, @Query("ll") String ll, @Query("query") String query, @Query("oauth_token") String oauth_token);

    @POST("{owner}" + "?type=driver_booking_list")
    Call<ResponsePastBooking> completedTrips(@Path(value = "owner", encoded = true) String owner, @Body ApiRequestData.UpcomingRequest body, @Query("lang") String lang);

  @POST("{owner}" + "?type=driver_booking_list")
    Call<ResponseUpcomingTrips> activeTrips(@Path(value = "owner", encoded = true) String owner, @Body ApiRequestData.UpcomingRequest body, @Query("lang") String lang);

    @POST("{owner}" + "?type=driver_booking_list")
    Call<ResponseNewSchduleBooking> schduleTrip(@Path(value = "owner", encoded = true) String owner, @Body ApiRequestData.UpcomingRequest body, @Query("lang") String lang);
}
