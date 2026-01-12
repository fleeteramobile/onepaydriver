package com.onepaytaxi.driver.Login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.onepaytaxi.driver.MainActivity;
import com.onepaytaxi.driver.R;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.interfaces.APIResult;
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON;
import com.onepaytaxi.driver.utils.NC;
import com.onepaytaxi.driver.utils.SessionSave;

import org.json.JSONObject;

import java.util.UUID;


//import com.seero.util.DeviceUtils;
//import com.google.firebase.analytics.FirebaseAnalytics;

public class LoginActivity extends MainActivity {
    private String[] mobilenoary;
    private final String fbname = "";
    private final String lname = "";
    private final String countrycode = "";
    private String phone;

    private EditText MobileNumber;
    private LinearLayout Continue_mobile,countrycode_lay;
    private ImageView back_click;





    @Override
    public int setLayout() {
        // TODO Auto-generated method stub
        setLocale();
        return R.layout.activity_signup;
    }

    @SuppressLint("NewApi")
    @Override
    public void Initialize() {

        if (!CommonData.mDevice_id.equals("")) {
            SessionSave.saveSession("mDevice_id", CommonData.mDevice_id, LoginActivity.this);
        }


        MobileNumber = findViewById(R.id.edt_mobileno);
        Continue_mobile = findViewById(R.id.continue_phone);
        countrycode_lay= findViewById(R.id.countrycode_lay);
        back_click = findViewById(R.id.back_click);




        MobileNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Continue_mobile.performClick();
                }
                return false;
            }
        });
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(MobileNumber, InputMethodManager.SHOW_IMPLICIT);
        Bundle extras = getIntent().getExtras();



        MobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try {
                    if (charSequence.length() != 0) {
                        String s = String.valueOf(charSequence.charAt(0));



                        if (s.equalsIgnoreCase("0")||s.equalsIgnoreCase("2")||s.equalsIgnoreCase("3")||s.equalsIgnoreCase("4")||s.equalsIgnoreCase("5")) {
                            MobileNumber.setText("");

                        } else {
                            int maxLength = 16;
                            InputFilter[] FilterArray = new InputFilter[1];
                            FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                            MobileNumber.setFilters(FilterArray);
                        }


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        back_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        /**
         * @author developer
         *         <p>
         *         This section helps to login to app.
         *         </p>
         * @param string
         *
         *            mobile number, string password
         *
         *            API used:- type=passenger_login
         */
        Continue_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                try {
//                    FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(LoginActivity.this);
                    Bundle params = new Bundle();
                    params.putString("user", phone);
//                    params.putString(FirebaseAnalytics.Param.ITEM_NAME, phone);
                    params.putString("type", "passenger");
//                    mFirebaseAnalytics.logEvent("Login_clicked_by", params);

                    ContinueWithMobile();

                } catch (Exception e) {
                    e.printStackTrace();
                    ShowToast(LoginActivity.this, "Invaid format");
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //  window.setStatusBarColor(CL.getColor(this, R.color.button_accept));
        }

    }


    private void ContinueWithMobile() {
        try {
            phone = MobileNumber.getText().toString().trim();
            String mUUID = "";
            if (CommonData.mDevice_id.equals("")) {
                if (UUID.randomUUID().toString().equals(""))
                    mUUID = CommonData.mDevice_id_constant;
                else
                    mUUID = UUID.randomUUID().toString();
                CommonData.mDevice_id = mUUID;
            } else {
            }

            if (SessionSave.getSession("sDevice_id",  LoginActivity.this).equals("")) {
                if (!UUID.randomUUID().toString().equals("")) {
                    mUUID = UUID.randomUUID().toString();
                } else {
                    mUUID = CommonData.mDevice_id_constant;
                }
                SessionSave.saveSession("sDevice_id", mUUID, LoginActivity.this);
            }
            if (!CommonData.mDevice_id.equals("")) {
                SessionSave.saveSession("mDevice_id", CommonData.mDevice_id, LoginActivity.this);
            }
     if (validations(ValidateAction.isValueNULL, LoginActivity.this, phone)) {
                JSONObject json = new JSONObject();
                json.put("phone", phone);
                json.put("country_code", SessionSave.getSession("country_code",LoginActivity.this));
//                String token = FirebaseInstanceId.getInstance().getToken();
                String token = SessionSave.getSession(CommonData.DEVICE_TOKEN, LoginActivity.this);
                json.put("device_id", SessionSave.getSession("sDevice_id", LoginActivity.this));
                json.put("device_token", token == null || token == "" ? SessionSave.getSession("sDevice_id", LoginActivity.this) : token);
                json.put("device_type", "1");
//                json.put("device_info", new JSONObject(new Gson().toJson(DeviceUtils.INSTANCE.getAllInfo(LoginActivity.this))));
                new SignIn("type=driver_signupwith_phone", json);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            ShowToast(LoginActivity.this, "Invaid format");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        AppEventsLogger.activateApp(getApplication());
    }

    /**
     * <p>
     * This method used store the FB logged user details into bundle
     * </p>
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {

        super.onPause();
//        AppEventsLogger.deactivateApp(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
//        Utility.closeDialog();
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this, DriverLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    /**
     * This class used to login in app
     * <p>
     * This class used to login in app
     * </p>
     *
     * @author developer
     */
    private class SignIn implements APIResult {
        private SignIn(final String url, JSONObject j) {
            // new APIService_Retrofit_JSON(LoginActivity.this, this, j, false, CommonData.API_BASE_URL + CommonData.COMPANY_KEY + "/?" + "lang=" + SessionSave.getSession("Lang", LoginActivity.this) + "&" + url).execute();
            new APIService_Retrofit_JSON(LoginActivity.this, this, j, false).execute(url);

        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            // TODO Auto-generated method stub
            try {
                Continue_mobile.setEnabled(true);
                if (isSuccess) {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {

                        json.put("phone", phone);
                        SessionSave.saveSession("phone", phone, LoginActivity.this);


                        if (json.has("otp")) {
                            SessionSave.saveSession("otp_number", json.getString("otp"), LoginActivity.this);
                        } else {
                            SessionSave.saveSession("otp_number", "", LoginActivity.this);
                        }

//                        if (json.has(CommonData.HIDE_OTP)) {
//                            SessionSave.saveSession(CommonData.HIDE_OTP, json.getString(CommonData.HIDE_OTP).equals("1"), LoginActivity.this);
//                        } else {
//                            SessionSave.saveSession(CommonData.HIDE_OTP, false, LoginActivity.this);
//                        }

                        if (json.has("phone_exist")) {
                            if (json.getInt("phone_exist") == 3) {
                                if (json.getJSONObject("detail").has("passenger_id")) {
                                    SessionSave.saveSession("passenger_id", json.getJSONObject("detail").getString("passenger_id"), LoginActivity.this);
                                }
//                                if (json.getJSONObject("detail").has(CommonData.SKIP_PASSENGER_EMAIL))
//                                    SessionSave.saveSession(CommonData.SKIP_PASSENGER_EMAIL, json.getJSONObject("detail").getString(CommonData.SKIP_PASSENGER_EMAIL).equals("1"), LoginActivity.this);
//                                else
//                                    SessionSave.saveSession(CommonData.SKIP_PASSENGER_EMAIL, false, LoginActivity.this);
//
//                                final Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
//                                startActivity(i);
                            } else {
                                final Intent i = new Intent(LoginActivity.this, VerificationActivity.class);
                                Bundle detail_fb = new Bundle();
                                detail_fb.putString("phone_exist", json.getString("phone_exist"));
                                detail_fb.putString("phone", json.getJSONObject("detail").getString("phone"));
                                detail_fb.putString("country", json.getJSONObject("detail").getString("country_code"));
                                i.putExtras(detail_fb);
                                startActivity(i);
                            }
                        }
                    } else {
                        alert_view(LoginActivity.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "");
                        Continue_mobile.setEnabled(true);
                    }
                } else {
                    Continue_mobile.setEnabled(true);
                    runOnUiThread(new Runnable() {
                        public void run() {
                           // ShowToast(LoginActivity.this, NC.getString(R.string.server_error));
                        }
                    });
                }
            } catch (final Exception e) {
                // TODO Auto-generated catch block
                Continue_mobile.setEnabled(true);
                e.printStackTrace();
            }
        }
    }

    /**
     * This class used to update the mobileno to the api
     * <p>
     * This class used to update the mobileno to the api
     * </p>
     */

}