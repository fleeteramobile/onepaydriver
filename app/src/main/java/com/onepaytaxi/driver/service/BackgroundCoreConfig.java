package com.onepaytaxi.driver.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.res.Configuration;

import com.onepaytaxi.driver.Login.DriverLoginActivity;
import com.onepaytaxi.driver.MyApplication;
import com.onepaytaxi.driver.R;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.interfaces.APIResult;
import com.onepaytaxi.driver.utils.CL;
import com.onepaytaxi.driver.utils.CToast;
import com.onepaytaxi.driver.utils.NC;
import com.onepaytaxi.driver.utils.SessionSave;
import com.onepaytaxi.driver.utils.Systems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import androidx.annotation.Nullable;

/**
 * Created by developer on 22/2/18.
 */

public class BackgroundCoreConfig extends IntentService {
    private long getCore_Utc;
    private String getCoreLangTime;
    private String getCoreColorTime;

    public BackgroundCoreConfig() {
        super("BackgroundCoreConfig");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        SessionSave.saveSession("auth_last_call_type", ts, BackgroundCoreConfig.this);
        if (SessionSave.getSession("wholekey", BackgroundCoreConfig.this).equals(""))
            new callString("");
        else {
            String url = "type=getcoreconfig";
            new CoreConfigCall(url);
        }
    }

    /**
     * Setting Language Configuration
     */
    public void setLocale() {
        if (SessionSave.getSession("Lang", BackgroundCoreConfig.this).equals("")) {
            SessionSave.saveSession("Lang", "en", BackgroundCoreConfig.this);
            SessionSave.saveSession("Lang_Country", "en_GB", BackgroundCoreConfig.this);
        }


        Configuration config = new Configuration();
        String langcountry = SessionSave.getSession("Lang_Country", BackgroundCoreConfig.this);
        String language = SessionSave.getSession("Lang", BackgroundCoreConfig.this);
        String[] arry = langcountry.split("_");
        config.locale = new Locale(language, arry[1]);
        Locale.setDefault(new Locale(language, arry[1]));
        BackgroundCoreConfig.this.getBaseContext().getResources().updateConfiguration(config, BackgroundCoreConfig.this.getResources().getDisplayMetrics());

    }

    /**
     * Adding string files to Local hashmap
     */
    private synchronized void getAndStoreStringValues(String result) {
//        try {
//
//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//            InputStream is = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
//            Document doc = dBuilder.parse(is);
//            Element element = doc.getDocumentElement();
//            element.normalize();
//
//            NodeList nList = doc.getElementsByTagName("*");
//
//            for (int i = 0; i < nList.getLength(); i++) {
//
//                Node node = nList.item(i);
//                if (node.getNodeType() == Node.ELEMENT_NODE) {
//                    Element element2 = (Element) node;
//                    NC.nfields_byName.put(element2.getAttribute("name"), element2.getTextContent());
//                }
//            }
//            getValueDetail();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Getting String values from local
     */
    synchronized void getValueDetail() {
        Field[] fieldss = R.string.class.getDeclaredFields();
        for (Field field : fieldss) {
            int id = getResources().getIdentifier(field.getName(), "string", getPackageName());
            if (NC.nfields_byName.containsKey(field.getName())) {
                NC.fields.add(field.getName());
                NC.fields_value.add(getResources().getString(id));
                NC.fields_id.put(field.getName(), id);

            }
        }
        for (Map.Entry<String, String> entry : NC.nfields_byName.entrySet()) {
            String h = entry.getKey();
            NC.nfields_byID.put(NC.fields_id.get(h), NC.nfields_byName.get(h));
            // do stuff
        }

    }

    /**
     * Adding color files to Local hashmap
     */
    private synchronized void getAndStoreColorValues(String result) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
            Document doc = dBuilder.parse(is);
            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("*");

            Systems.out.println("lislength" + nList.getLength());
            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    CL.nfields_byName.put(element2.getAttribute("name"), element2.getTextContent());

                }
            }
            getColorValueDetail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Getting Color values from local hash map
     */
    synchronized void getColorValueDetail() {
        Field[] fieldss = R.color.class.getDeclaredFields();
        // fields =new int[fieldss.length];
        for (Field field : fieldss) {
            int id = getResources().getIdentifier(field.getName(), "color", getPackageName());
            if (CL.nfields_byName.containsKey(field.getName())) {
                CL.fields.add(field.getName());
                CL.fields_value.add(getResources().getString(id));
                CL.fields_id.put(field.getName(), id);

            }
        }

        for (Map.Entry<String, String> entry : CL.nfields_byName.entrySet()) {
            String h = entry.getKey();
            String value = entry.getValue();
            CL.nfields_byID.put(CL.fields_id.get(h), CL.nfields_byName.get(h));
        }

    }

    /**
     * Method to logout user if status -101 and redirect to login page
     *
     * @param message - To intimate user by showing alert message
     */
    private void forceLogout(String message) {
        CToast.ShowToast(BackgroundCoreConfig.this, message);
        ServiceGenerator.API_BASE_URL = "";
        SessionSave.saveSession("base_url", "", BackgroundCoreConfig.this);
        SessionSave.saveSession("Id", "", BackgroundCoreConfig.this);
        SessionSave.clearAllSession(BackgroundCoreConfig.this);
        stopService(new Intent(this, LocationUpdate.class));
        stopSelf();
        Intent intent = new Intent(BackgroundCoreConfig.this, DriverLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Getting Language Files from Server
     */
    private class callString implements APIResult {
        public callString(final String url) {
            // TODO Auto-generated constructor stub

            String urls = SessionSave.getSession("currentStringUrl", BackgroundCoreConfig.this);
            if (urls.equals("")) {
                urls = SessionSave.getSession(SessionSave.getSession("LANGDef", BackgroundCoreConfig.this), BackgroundCoreConfig.this);
                if (SessionSave.getSession("LANGTempDef", BackgroundCoreConfig.this).trim().equalsIgnoreCase("RTL")) {
                    SessionSave.saveSession("Lang_Country", "ar_EG", BackgroundCoreConfig.this);
                    SessionSave.saveSession("Lang", "ar", BackgroundCoreConfig.this);
                    Configuration config = new Configuration();
                    String langcountry = SessionSave.getSession("Lang_Country", BackgroundCoreConfig.this);
                    String language = SessionSave.getSession("Lang", BackgroundCoreConfig.this);
                    String[] arry = langcountry.split("_");
                    config.locale = new Locale(language, arry[1]);
                    Locale.setDefault(new Locale(language, arry[1]));
                }
            }
            new APIService_Retrofit_JSON_NoProgress(BackgroundCoreConfig.this, this, null, true, urls, true).execute();
        }

        @Override
        public void getResult(boolean isSuccess, String result) {

            if (isSuccess) {
                setLocale();
                getAndStoreStringValues(result);
                SessionSave.saveSession("wholekey", result, BackgroundCoreConfig.this);

                if (SessionSave.getSession("wholekeyColor", BackgroundCoreConfig.this).trim().equals("") || !SessionSave.getSession(CommonData.PASSENGER_COLOR_TIME, BackgroundCoreConfig.this).equals(getCoreColorTime))
                    new callColor("");

            } else {
            }
        }

    }

    /**
     * Getting Color Files from Server and response parsing
     */
    private class callColor implements APIResult {
        public callColor(final String url) {

            new APIService_Retrofit_JSON_NoProgress(BackgroundCoreConfig.this, this, null, true, SessionSave.getSession("colorcode", BackgroundCoreConfig.this).replace("DriverAppColor", "driverAppColors"), true).execute();


        }

        @Override
        public void getResult(boolean isSuccess, String result) {

            if (isSuccess) {
             //   getAndStoreColorValues(result);
                SessionSave.saveSession("wholekeyColor", result, BackgroundCoreConfig.this);

            }

        }
    }

    /**
     * CoreConfig method API call and response parsing.
     */
    public class CoreConfigCall implements APIResult {
        public CoreConfigCall(final String url) {
            // TODO Auto-generated constructor stub
            new APIService_Retrofit_JSON_NoProgress(BackgroundCoreConfig.this, this, "", true).execute(url);
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            // TODO Auto-generated method stub
            if (isSuccess) {

                try {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {


                        if (json.has("gt_lst_time"))
                            SessionSave.saveSession(CommonData.GETCORE_LASTUPDATE, json.getString("gt_lst_time"), BackgroundCoreConfig.this);

                        if (json.has(CommonData.ACTIVITY_BG))
                            SessionSave.saveSession(CommonData.ACTIVITY_BG, json.getString(CommonData.ACTIVITY_BG), BackgroundCoreConfig.this);

                        if (json.has(CommonData.ERROR_LOGS))
                            SessionSave.saveSession(CommonData.ERROR_LOGS, json.getString(CommonData.ERROR_LOGS).equals("1"), BackgroundCoreConfig.this);
                        if (json.has(CommonData.TIMEZONE)) {
                            SessionSave.saveSession(CommonData.TIMEZONE, json.getString(CommonData.TIMEZONE), BackgroundCoreConfig.this);
                        }
                        JSONArray jArry = json.getJSONArray("detail");

                        SessionSave.saveSession("api_base", jArry.getJSONObject(0).getString("api_base"), BackgroundCoreConfig.this);
                        SessionSave.saveSession("isFourSquare", jArry.getJSONObject(0).getString("android_foursquare_status"), BackgroundCoreConfig.this);
                        SessionSave.saveSession("android_foursquare_api_key", jArry.getJSONObject(0).getString("android_foursquare_api_key"), BackgroundCoreConfig.this);
                        SessionSave.saveSession("facebook_key", jArry.getJSONObject(0).getString("facebook_key"), BackgroundCoreConfig.this);
                        SessionSave.saveSession("play_store_version", jArry.getJSONObject(0).getString("android_driver_version"), BackgroundCoreConfig.this);
                        if (jArry.getJSONObject(0).has("playstore_driver"))
                            SessionSave.saveSession(CommonData.PLAY_STORE_LINK, jArry.getJSONObject(0).getString("playstore_driver"), BackgroundCoreConfig.this);

                        if (jArry.getJSONObject(0).has("last_forceupdate_version"))
                            SessionSave.saveSession(CommonData.LAST_FORCEUPDATE_VERSION, jArry.getJSONObject(0).getString("last_forceupdate_version"), BackgroundCoreConfig.this);
                        else
                            SessionSave.saveSession(CommonData.LAST_FORCEUPDATE_VERSION, "0", BackgroundCoreConfig.this);

                        if (jArry.getJSONObject(0).has("manual_waiting_enable")) {
                            SessionSave.saveSession(CommonData.WAITING_TIME_MANUAL, jArry.getJSONObject(0).getString("manual_waiting_enable").equals("1"), BackgroundCoreConfig.this);
                        }

                        SessionSave.saveSession("country_iso_code", jArry.getJSONObject(0).getString("country_iso_code"), BackgroundCoreConfig.this);

                        String googleApiKey = jArry.getJSONObject(0).getString("android_google_api_key");
                        if (!getString(R.string.googleID).equals(googleApiKey))
                            MyApplication.getInstance().setPlaceApiKey(googleApiKey);

                        SessionSave.saveSession(CommonData.GOOGLE_KEY, googleApiKey, BackgroundCoreConfig.this);

                        if (jArry.getJSONObject(0).has("android_mapbox_key")) {
                            SessionSave.saveSession(CommonData.MAP_BOX_TOKEN, jArry.getJSONObject(0).getString("android_mapbox_key"), BackgroundCoreConfig.this);
                        } else {
                            SessionSave.saveSession(CommonData.MAP_BOX_TOKEN, "pk.eyJ1Ijoic2FiYXJpc2hqIiwiYSI6ImNqaGc1Yzd1ZDFlb24zZG4yNzNzaGo0aDgifQ.TzQA9NFpczQ5Yu5duB753A", BackgroundCoreConfig.this);
                        }
                        if (jArry.getJSONObject(0).has("android_local_map_enable")) {
                            SessionSave.saveSession(CommonData.LOCAL_STORAGE, jArry.getJSONObject(0).getString("android_local_map_enable").equals("1"), BackgroundCoreConfig.this);
                        } else {
                            SessionSave.saveSession(CommonData.LOCAL_STORAGE, false, BackgroundCoreConfig.this);
                        }
                        if (jArry.getJSONObject(0).has("sos_msg"))
                            SessionSave.saveSession("sos_message", jArry.getJSONObject(0).getString("sos_msg"), BackgroundCoreConfig.this);
                       /* if (json.has("mobile_socket_http_url")) {
                            SessionSave.saveSession(CommonData.NODE_URL, json.getString("mobile_socket_http_url"), BackgroundCoreConfig.this);
                        }
*/
                        if (json.has("https_node_url")) {
                            SessionSave.saveSession(CommonData.NODE_URL, json.getString("https_node_url"), BackgroundCoreConfig.this);
                        }

                        if (json.has("mobile_socket_http_domain")) {
                            SessionSave.saveSession(CommonData.NODE_DOMAIN, json.getString("mobile_socket_http_domain"), BackgroundCoreConfig.this);
                        }

                        if (json.has(CommonData.HELP_URL)) {
                            SessionSave.saveSession(CommonData.HELP_URL, json.getString(CommonData.HELP_URL), BackgroundCoreConfig.this);
                        }


                        if (jArry.getJSONObject(0).has("sos_setting"))
                            SessionSave.saveSession(CommonData.SOS_ENABLED, jArry.getJSONObject(0).getString("sos_setting").equals("1"), BackgroundCoreConfig.this);
                        if (jArry.getJSONObject(0).has("map_settings") && jArry.getJSONObject(0).getJSONObject("map_settings").has("is_google_distance")) {
                            SessionSave.saveSession(CommonData.isGoogleDistance, jArry.getJSONObject(0).getJSONObject("map_settings").getString("is_google_distance").equals("1"), BackgroundCoreConfig.this);
                            SessionSave.saveSession(CommonData.isGoogleRoute, jArry.getJSONObject(0).getJSONObject("map_settings").getString("is_google_direction").equals("1"), BackgroundCoreConfig.this);
                            SessionSave.saveSession(CommonData.isGoogleGeocoder, jArry.getJSONObject(0).getJSONObject("map_settings").getString("is_google_geocode").equals("1"), BackgroundCoreConfig.this);
                            SessionSave.saveSession(CommonData.isNeedtoDrawRoute, jArry.getJSONObject(0).getJSONObject("map_settings").getString("enable_route").equals("1"), BackgroundCoreConfig.this);
                            SessionSave.saveSession(CommonData.isNeedtofetchAddress, jArry.getJSONObject(0).getJSONObject("map_settings").getString("display_current_location").equals("1"), BackgroundCoreConfig.this);


                        } else {
                            SessionSave.saveSession(CommonData.isGoogleDistance, true, BackgroundCoreConfig.this);
                            SessionSave.saveSession(CommonData.isGoogleRoute, true, BackgroundCoreConfig.this);
                            SessionSave.saveSession(CommonData.isGoogleGeocoder, true, BackgroundCoreConfig.this);
                            SessionSave.saveSession(CommonData.isNeedtoDrawRoute, true, BackgroundCoreConfig.this);
                            SessionSave.saveSession(CommonData.isNeedtofetchAddress, true, BackgroundCoreConfig.this);

                        }
                        int length = jArry.length();
                        for (int i = 0; i < length; i++) {
                            SessionSave.saveSession("noimage_base", jArry.getJSONObject(i).getString("noimage_base"), getApplicationContext());
                            SessionSave.saveSession("site_currency", jArry.getJSONObject(i).getString("site_currency") + " ", getApplicationContext());
                            SessionSave.saveSession("invite_txt", jArry.getJSONObject(i).getString("aboutpage_description"), getApplicationContext());
                            SessionSave.saveSession("referal", jArry.getJSONObject(i).getString("driver_referral_settings"), getApplicationContext());
                            SessionSave.saveSession("Metric", jArry.getJSONObject(i).getString("metric"), BackgroundCoreConfig.this);
                        }
                        try {
                            getCoreLangTime = json.getJSONObject("language_color_status").getString("android_driver_language");
                            getCoreColorTime = json.getJSONObject("language_color_status").getString("android_driver_colorcode");
                            getCore_Utc = jArry.getJSONObject(0).getLong("utc_time");
                            SessionSave.saveSession("utc_time", String.valueOf(getCore_Utc), BackgroundCoreConfig.this);
                            boolean deflanAvail = false;
                            String totalLanguage = "";
                            JSONArray pArray = json.getJSONObject("language_color").getJSONObject("android").getJSONArray("driver_language");
                            for (int i = 0; i < pArray.length(); i++) {
                                totalLanguage += pArray.getJSONObject(i).getString("language").replaceAll(".xml", "") + "____";
                                SessionSave.saveSession("LANG" + i, pArray.getJSONObject(i).getString("language"), BackgroundCoreConfig.this);
                                SessionSave.saveSession("LANGTemp" + i, pArray.getJSONObject(i).getString("design_type"), BackgroundCoreConfig.this);
                                SessionSave.saveSession("LANGCode" + i, pArray.getJSONObject(i).getString("language_code"), BackgroundCoreConfig.this);
                                SessionSave.saveSession(pArray.getJSONObject(i).getString("language"), pArray.getJSONObject(i).getString("url"), BackgroundCoreConfig.this);
                                if (!SessionSave.getSession("LANGDef", BackgroundCoreConfig.this).equals("") && pArray.getJSONObject(i).getString("language").contains(SessionSave.getSession("LANGDef", BackgroundCoreConfig.this))) {
                                    deflanAvail = true;
                                }
                            }
                            if (SessionSave.getSession("LANGDef", BackgroundCoreConfig.this).trim().equals("") || !deflanAvail) {
                                SessionSave.saveSession("LANGDef", SessionSave.getSession("LANG0", BackgroundCoreConfig.this), BackgroundCoreConfig.this);
                                SessionSave.saveSession("LANGTempDef", SessionSave.getSession("LANGTemp0", BackgroundCoreConfig.this), BackgroundCoreConfig.this);
                                SessionSave.saveSession("Lang", pArray.getJSONObject(0).getString("language_code").replaceAll(".xml", ""), BackgroundCoreConfig.this);
                                String url = SessionSave.getSession(SessionSave.getSession("LANG" + 0, BackgroundCoreConfig.this), BackgroundCoreConfig.this);
                                SessionSave.saveSession("currentStringUrl", url, BackgroundCoreConfig.this);
                            }

                            SessionSave.saveSession("lang_json", totalLanguage, BackgroundCoreConfig.this);

                            SessionSave.saveSession("colorcode", json.getJSONObject("language_color").getJSONObject("android").getString("driverColorCode"), BackgroundCoreConfig.this);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!SessionSave.getSession(CommonData.PASSENGER_LANGUAGE_TIME, BackgroundCoreConfig.this).trim().equals(getCoreLangTime)) {
                            new callString(getCoreColorTime);
                        } else if (!SessionSave.getSession(CommonData.PASSENGER_COLOR_TIME, BackgroundCoreConfig.this).trim().equals(getCoreColorTime)) {
                            new callColor(getCoreLangTime);
                        }
                    } else if (json.getInt("status") == 0) {
                        //no changes made
                    } else if (json.getInt("status") == -101) {
                        if (json.has("message"))
                            forceLogout(json.getString("message"));
                        else
                            forceLogout(NC.getString(R.string.server_error));
                    }
                } catch (final JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (final NullPointerException e) {
                    // TODO: handle exception
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            } else {
            }
        }

    }
}
