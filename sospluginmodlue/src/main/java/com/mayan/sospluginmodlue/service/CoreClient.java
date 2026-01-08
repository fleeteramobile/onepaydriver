package com.mayan.sospluginmodlue.service;

import com.google.gson.JsonObject;
import com.mayan.sospluginmodlue.model.ApiRequestData;
import com.mayan.sospluginmodlue.model.CompanyDomainResponse;
import com.mayan.sospluginmodlue.model.EmergencyListData;

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
    String owner = "dGF4aV9hbGw=/";

    //    @GET("{owner}")
//    Call<String> coreDetails(@Path(value = "owner",encoded = true) String owner);
    @GET("{owner}" + "?type=getcoreconfig")
    Call<ResponseBody> coreDetailsN(@Path(value = "owner", encoded = true) String owner, @Query(value = "encode", encoded = true) String auth_key);

    @GET("{owner}")
    Call<ResponseBody> coreDetails(@Path(value = "owner", encoded = true) String owner, @Query("type") String url, @Query(value = "encode", encoded = true) String auth_key);

//    @POST("{owner}")
//    Call<ResponseBody> updateUser(@Path(value = "owner",encoded = true) String owner,@Body RequestBody body, @Query("type") String url,@Query(value = "encode",encoded = true) String auth_key);


    @POST("?type=emergency_conact_list")
    Call<EmergencyListData> emergencyList(@Body ApiRequestData.getEmergencyRequestData body, @Query(value = "lang", encoded = true) String lang);

    @POST("?type=add_emergency_contact")
    Call<EmergencyListData> addContact(@Body ApiRequestData.AddContactRequestData body, @Query(value = "lang", encoded = true) String lang);

    @POST("?type=delete_emergency_contact")
    Call<EmergencyListData> deleteContact(@Body ApiRequestData.DeleteContactRequestData body, @Query(value = "lang", encoded = true) String lang);


    @POST("?type=SOS_emergency_contact")
    Call<ApiRequestData.StandardResponse> Emergencysos(@Body ApiRequestData.EmergencyRequestData body, @Query(value = "lang", encoded = true) String lang);


    @GET
    Call<JsonObject> getJsonbyWholeUrl(@Header("Cache-Control") String cacheControl, @Url String url);


    @POST("?type=check_companydomain")
    Call<CompanyDomainResponse> callData(@Body ApiRequestData.callCheckCompanyDomain body);

}
