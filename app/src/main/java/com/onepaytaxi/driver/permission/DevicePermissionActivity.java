package com.onepaytaxi.driver.permission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onepaytaxi.driver.BuildConfig;
import com.onepaytaxi.driver.Login.DriverLoginActivity;
import com.onepaytaxi.driver.MainActivity;
import com.onepaytaxi.driver.R;
import com.onepaytaxi.driver.interfaces.APIResult;
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON;
import com.onepaytaxi.driver.utils.CL;
import com.onepaytaxi.driver.utils.FontHelper;
import com.onepaytaxi.driver.utils.NC;
import com.onepaytaxi.driver.utils.SessionSave;
import com.onepaytaxi.driver.utils.Systems;
import com.onepaytaxi.driver.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DevicePermissionActivity extends MainActivity {
    AppCompatCheckBox accessLocationCheckBox, readContactsCheckBox, readDeviceInformationCheckbox, accessCallPhoneCheckBox, accessStorageCheckBox, termsCheckBox;
    LinearLayout buttonProceed;
    AppCompatTextView storeDataView, loc_mandatory, tv_contacts, tv_device_info, tv_call_permission, tv_storage_gallery;

    private static final String ACCESS_COARSE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String ACCESS_FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String FOREGROUND_SERVICE_LOCATION = Manifest.permission.FOREGROUND_SERVICE_LOCATION;
    private static final String READ_EXTERNAL_STOARGE_PERMISSSION = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String WRITE_EXTERNAL_STOARGE_PERMISSSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
//    private static final String READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    private static final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
//    private static final String CALL_PHONE = Manifest.permission.CALL_PHONE;
private static final String READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES;

    private static final String POST_NOTIFICATIONS = Manifest.permission.POST_NOTIFICATIONS;
//
    private static final int COMMON_REQUEST_CODE = 1000;
    private static final int FROM_RATIONALE = 1;
    private static final int NORMAL = 2;

    private ArrayList<String> permissionsList;
    boolean isEnable = true;
    private String location, mandatory;


    @Override
    public int setLayout() {
        return R.layout.activity_device_permission;
    }

    @Override
    public void Initialize() {

        accessLocationCheckBox = findViewById(R.id.accessLocationCheckBox);
        readContactsCheckBox = findViewById(R.id.readContactsCheckBox);
        readDeviceInformationCheckbox = findViewById(R.id.readDeviceInformationCheckbox);
        accessCallPhoneCheckBox = findViewById(R.id.accessCallPhoneCheckBox);
        accessStorageCheckBox = findViewById(R.id.accessStorageCheckBox);
        buttonProceed = findViewById(R.id.buttonProceed);
        storeDataView = findViewById(R.id.storeDataView);
        termsCheckBox = findViewById(R.id.termsCheckBox);

        loc_mandatory = findViewById(R.id.loc_mandatory);
        tv_contacts = findViewById(R.id.tv_contacts);
        tv_device_info = findViewById(R.id.tv_device_info);
        tv_call_permission = findViewById(R.id.tv_call_permission);
        tv_storage_gallery = findViewById(R.id.tv_storage_gallery);

        FontHelper.applyFont(this, findViewById(R.id.id_privacy_parent_lay));

        location = getColoredSpanned(NC.getString(R.string.location), CL.getColor(R.color.pure_black));




//        mandatory = getColoredSpanned(NC.getString(R.string.mandatory), CL.getColor(R.color.colorAccent));
//
//        loc_mandatory.setText(Html.fromHtml(location.concat(" ").concat(mandatory)));


//        loc_mandatory.setTypeface(MyApplication.getInstance().getTypeFace(0));
//        tv_contacts.setTypeface(MyApplication.getInstance().getTypeFace(0));
//        tv_device_info.setTypeface(MyApplication.getInstance().getTypeFace(0));
//        tv_call_permission.setTypeface(MyApplication.getInstance().getTypeFace(0));
//        tv_storage_gallery.setTypeface(MyApplication.getInstance().getTypeFace(0));


        accessLocationCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> Utils.alert_view_dialog(DevicePermissionActivity.this, NC.getString(R.string.location_permission_required), NC.getString(R.string.without_loc), NC.getString(R.string.ok), "", true, (dialog, i) -> {

                    dialog.dismiss();


                }, (dialog2, i) -> {
                    dialog2.dismiss();

                }, "")
        );


        buttonProceed.setOnClickListener(View ->
        {

            permissionsList = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(DevicePermissionActivity.this, ACCESS_COARSE_LOCATION_PERMISSION) !=
                    PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(DevicePermissionActivity.this, ACCESS_FINE_LOCATION_PERMISSION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(DevicePermissionActivity.this, FOREGROUND_SERVICE_LOCATION) != PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(DevicePermissionActivity.this, READ_EXTERNAL_STOARGE_PERMISSSION) != PackageManager.PERMISSION_GRANTED
                    ||
                    ContextCompat.checkSelfPermission(DevicePermissionActivity.this, WRITE_EXTERNAL_STOARGE_PERMISSSION) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsList.add(ACCESS_COARSE_LOCATION_PERMISSION);
                permissionsList.add(ACCESS_FINE_LOCATION_PERMISSION);
                permissionsList.add(POST_NOTIFICATIONS);
                permissionsList.add(READ_MEDIA_IMAGES);
                permissionsList.add(WRITE_EXTERNAL_STOARGE_PERMISSSION);
                permissionsList.add(FOREGROUND_SERVICE_LOCATION);

            }

            if (accessStorageCheckBox.isChecked()) {
                permissionsList.add(READ_MEDIA_IMAGES);
                permissionsList.add(WRITE_EXTERNAL_STOARGE_PERMISSSION);
                permissionsList.add(POST_NOTIFICATIONS);

            }
/*
            if (accessCallPhoneCheckBox.isChecked())
                permissionsList.add(CALL_PHONE);*/
/*
            if (readContactsCheckBox.isChecked())
                permissionsList.add(READ_CONTACTS);*/

            if (readDeviceInformationCheckbox.isChecked())
                permissionsList.add(READ_PHONE_STATE);

           // if (termsCheckBox.isChecked())
                CheckPermissions(permissionsList);
//            else
//                ShowToast(DevicePermissionActivity.this, NC.getString(R.string.agreed_checkbox));


        });

        storeDataView.setOnClickListener(View ->
        {
        });

       //setSpannableTextView(findViewById(R.id.spannable_txt));


    }

    private String getColoredSpanned(String text, int color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    private void CheckPermissions(ArrayList<String> permissionsList) {


        if (Build.VERSION.SDK_INT >= 23) {
            for (int i = 0; i < permissionsList.size(); i++) {
                if (ContextCompat.checkSelfPermission(DevicePermissionActivity.this, permissionsList.get(i)) != PackageManager.PERMISSION_GRANTED) {
                    isEnable = false;
                    break;
                } else
                    isEnable = true;
            }
        } else
            isEnable = true;

        if (!isEnable)
            makeRequest(permissionsList);
        else
            moveToLoginScreen();


    }

    private void makeRequest(ArrayList<String> permissionsList) {
        String[] permissionStringList = new String[permissionsList.size()];

        for (int i = 0; i < permissionsList.size(); i++) {
            permissionStringList[i] = permissionsList.get(i);
        }

        if (permissionStringList.length > 0)
            ActivityCompat.requestPermissions(DevicePermissionActivity.this, permissionStringList, COMMON_REQUEST_CODE);
        else
            moveToLoginScreen();

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == COMMON_REQUEST_CODE) {


            if (ContextCompat.checkSelfPermission(DevicePermissionActivity.this, ACCESS_COARSE_LOCATION_PERMISSION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(DevicePermissionActivity.this, ACCESS_FINE_LOCATION_PERMISSION) != PackageManager.PERMISSION_GRANTED) {

                if (permissionsList != null) {

                    if (permissionsList.size() > 0) {
                        permissionsList.clear();
                        permissionsList.add(ACCESS_FINE_LOCATION_PERMISSION);
                        permissionsList.add(ACCESS_COARSE_LOCATION_PERMISSION);
                        permissionsList.add(READ_MEDIA_IMAGES);
                        permissionsList.add(WRITE_EXTERNAL_STOARGE_PERMISSSION);
                        permissionsList.add(POST_NOTIFICATIONS);
                        permissionsList.add(FOREGROUND_SERVICE_LOCATION);

                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(DevicePermissionActivity.this, ACCESS_FINE_LOCATION_PERMISSION)
                                ||
                                ActivityCompat.shouldShowRequestPermissionRationale(DevicePermissionActivity.this, ACCESS_COARSE_LOCATION_PERMISSION )
                                        || ActivityCompat.shouldShowRequestPermissionRationale(DevicePermissionActivity.this, ACCESS_COARSE_LOCATION_PERMISSION);
                        if (!showRationale) {
                            userAlertView(FROM_RATIONALE);

                        } else {
                            userAlertView(NORMAL);
                        }
                    }

                }


            } else {
                moveToLoginScreen();
            }


        }
    }

    private void moveToLoginScreen() {
        SessionSave.saveSession("user_privacy_policy", "true", DevicePermissionActivity.this);
        Intent i = new Intent(DevicePermissionActivity.this, DriverLoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


    private void userAlertView(int checkRationaleOrNormal) {


        Utils.alert_view_dialog(DevicePermissionActivity.this, checkRationaleOrNormal == 2 ? NC.getString(R.string.location_permission_denied) : NC.getString(R.string.location_permission_required), checkRationaleOrNormal == 1 ? NC.getString(R.string.above_permission_settings) : NC.getString(R.string.deny_permission), checkRationaleOrNormal == 2 ? NC.getString(R.string.retry) : NC.getString(R.string.edit_permissions), checkRationaleOrNormal == 2 ? NC.getString(R.string.privacy_exit_app) : NC.getString(R.string.exit_anyway), true, (dialog, i) -> {
            if (checkRationaleOrNormal == 2) {
                CheckPermissions(permissionsList);
                dialog.dismiss();
            } else {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                intent.setData(uri);
                startActivity(intent);

            }

        }, (dialog2, i) -> {
            dialog2.dismiss();
            finish();

        }, "");
    }

    private void setSpannableTextView(TextView view) {
//    //    SpannableStringBuilder spanTxt = new SpannableStringBuilder(
//                R.string.by_clicking_proceed + " ");
//       // spanTxt.setSpan(new ForegroundColorSpan(CL.getColor(R.color.quantum_grey500)), spanTxt.length() - NC.getString(R.string.by_clicking_proceed).length() - 1, spanTxt.length(), 0);
//        spanTxt.append(NC.getString(R.string.terms_condition2));
//        spanTxt.setSpan(new ClickableSpan() {
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setColor(CL.getColor(R.color.button_accept));    // you can use custom color
//                ds.setUnderlineText(true);
//            }
//
//            @Override
//            public void onClick(View widget) {
//                String url = "&type=dynamic_page&pagename=10&device_type=1";
//                new ShowWebpage(url, null, "T");
//            }
//        }, spanTxt.length() - NC.getString(R.string.terms_condition2).length(), spanTxt.length(), 0);
//        spanTxt.append(" " + NC.getString(R.string.and));
//        spanTxt.setSpan(new ForegroundColorSpan(CL.getColor(R.color.quantum_grey500)), spanTxt.length() - NC.getString(R.string.and).length(), spanTxt.length(), 0);
//        spanTxt.append(" " + NC.getString(R.string.privacy_policy));
//
//
//        spanTxt.setSpan(new ClickableSpan() {
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setColor(CL.getColor(R.color.button_accept));    // you can use custom color
//                ds.setUnderlineText(true);
//            }
//
//            @Override
//            public void onClick(View widget) {
//                String url = "&type=dynamic_page&pagename=11&device_type=1";
//                new ShowWebpage(url, null, "P");
//            }
//        }, spanTxt.length() - NC.getString(R.string.privacy_policy).length(), spanTxt.length(), 0);
//        view.setMovementMethod(LinkMovementMethod.getInstance());
//        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    private class ShowWebpage implements APIResult {
        String type = "T";

        public ShowWebpage(final String string, JSONObject data, String type) {
            // TODO Auto-generated constructor stub
            this.type = type;
            String ss = SessionSave.getSession("base_url", DevicePermissionActivity.this) + "?" + "lang=" + SessionSave.getSession("Lang", DevicePermissionActivity.this) + string;
            Systems.out.println("weburl____" + ss);
            new APIService_Retrofit_JSON(DevicePermissionActivity.this, this, true, ss).execute();
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            // TODO Auto-generated method stub
            try {
                if (isSuccess) {

                } else {
                    //runOnUiThread(() -> ShowToast(DevicePermissionActivity.this, NC.getString(R.string.server_error)));
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

}
