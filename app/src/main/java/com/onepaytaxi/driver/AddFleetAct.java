package com.onepaytaxi.driver;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
//import com.scanlibrary.ScanActivity;
//import com.scanlibrary.ScanConstants;
import com.onepaytaxi.driver.Login.DriverLoginActivity;
import com.onepaytaxi.driver.data.ColorInfo;
import com.onepaytaxi.driver.data.VehicleModelInfo;
import com.onepaytaxi.driver.data.apiData.ModelListInfo;
import com.onepaytaxi.driver.data.apiData.StateInfo;
import com.onepaytaxi.driver.data.apiData.VehicleListInfo;
import com.onepaytaxi.driver.data.apiData.VehiclePrefixInfo;
import com.onepaytaxi.driver.interfaces.APIResult;
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON;
import com.onepaytaxi.driver.utils.CToast;
import com.onepaytaxi.driver.utils.FontHelper;
import com.onepaytaxi.driver.utils.NC;
import com.onepaytaxi.driver.utils.NetworkStatus;
import com.onepaytaxi.driver.utils.SessionSave;
import com.onepaytaxi.driver.utils.Utils;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class AddFleetAct extends MainActivity {
    private TextView HeadTitle, btn_submit;
    private ImageView leftIcon, imgV_pco_license, imgV_vehicle_mot, imgV_vehicle_insurance, imgV_vehicle_image, imgV_logbook,imgV_vehicle_reg_image;
    private EditText km_edt, prefix_plate_num_edt, plate_num_edt, edt_vehicle_manufacturer, edt_vehicle_model, edt_service_type, edt_vehicle_owner_name, edt_year_of_manufacturer;
    private EditText edt_vechile_insurance, edt_vechile_insurance_expiry, edt_vechile_body, edt_date_of_registration, edt_vechile_keeper;
    private LinearLayout ll_vehicle_pco_lic_txt, ll_vehicle_mot_txt, ll_vehicle_insurance_txt, ll_vehicle_img_txt, ll_vehicle_log_txt,ll_vehicle_reg_img_txt;
    private int mYear, mMonth, mDay;
    private Calendar c;
    private Dialog cameraDialog, dialog1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 113;
    private Uri imageUri;
    AppCompatCheckBox register_CheckBox;
    private String destinationFileName = "Image11";
    private int image_type = 11;
    private StateAdapter stateAdapter;
    private VehicleAdapter vehicleAdapter;
    private ModellistAdapter modellistAdapter;
    private VehicleInfoListAdapter vehicleInfoListAdapter;
    private VehicleModelAdapter vehiclemodelAdapter;
    private ArrayList<StateInfo> stateInfos;
    private ArrayList<VehiclePrefixInfo> vehiclePrefixInfos;
    private ArrayList<ModelListInfo> modelListInfos;
    private ArrayList<VehicleListInfo> vehicleListInfos;
    private ArrayList<VehicleModelInfo> vehicleModlelInfos;


    private String vehicle_img = "";
    private final String logbook_img = "";
    private String encodeImg = "";
    private String vehicle_reg_img="";
    private final String response = "";
    private final String driver_id = "";
    private final String company_id = "";
    private final String validity_date = "";
    private AlertDialog alertDialog;
    private String state_id = "", state_name = "";
    private String plate_prefix_id, plate_prefix = "";
    private String model_id, model_name = "";
    private String manufacturer_id;
    private String manufacturer_name;
    private final String manufacturer_model = "";
    private String vehicle_id = "";
    private String vehicle_name = "";
    private static final int REQUEST_CODE = 99;
    private EditText edt_three_digit, edt_ten_digit, edt_five_digit, edt_vechile_reference;
    private final String insurance_date = "";
    private final String selectedTime = "";
    private final String already_exist = "0";
    private final int numberOfColumns = 5;
    private RecyclerView id_color;
    private ArrayList<ColorInfo> colorInfos;
    private ColorAdapter colorAdapter;
    private String color_id, color_name = "";
    private TextView back_btn;

    private CardView back_trip_details;

    Dialog loadingDialog;


    @Override
    public int setLayout() {
        return R.layout.activity_addfleet_step_one;
    }

    @Override
    public void Initialize() {

        km_edt = findViewById(R.id.km_edt);
        prefix_plate_num_edt = findViewById(R.id.prefix_plate_num_edt);
        plate_num_edt = findViewById(R.id.plate_num_edt);
        edt_vehicle_manufacturer = findViewById(R.id.edt_vehicle_manufacturer);
        edt_vehicle_model = findViewById(R.id.edt_vehicle_model);
        edt_service_type = findViewById(R.id.edt_service_type);
        edt_vehicle_owner_name = findViewById(R.id.edt_vehicle_owner_name);
        edt_year_of_manufacturer = findViewById(R.id.edt_year_of_manufacturer);
        ll_vehicle_img_txt = findViewById(R.id.ll_vehicle_img_txt);
        imgV_vehicle_image = findViewById(R.id.imgV_vehicle_image);
        register_CheckBox = findViewById(R.id.register_CheckBox);
        btn_submit = findViewById(R.id.btn_submit);
        FontHelper.applyFont(this, findViewById(R.id.slide_lay));
        back_btn = findViewById(R.id.back_btn);

        back_trip_details= findViewById(R.id.back_trip_details);
        ll_vehicle_reg_img_txt=findViewById(R.id.ll_vehicle_reg_img_txt);
        imgV_vehicle_reg_image=findViewById(R.id.imgV_vehicle_reg_image);

        SessionSave.saveSession("model", "", AddFleetAct.this);

        id_color = findViewById(R.id.id_color);
        id_color.setLayoutManager(new GridLayoutManager(this, numberOfColumns));


        edt_year_of_manufacturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickyear();
            }
        });

        ll_vehicle_img_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationFileName = "Image14";
                image_type = 14; // image
                uploadImage();
            }
        });

        imgV_vehicle_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationFileName = "Image14";
                image_type = 14; // image
                uploadImage();
            }
        });

        ll_vehicle_reg_img_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationFileName = "Image15";
                image_type = 15; // document
                uploadImage();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        back_trip_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        km_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        prefix_plate_num_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openVehicleDialog();
            }
        });

        edt_service_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openModellist();
            }
        });
        edt_vehicle_manufacturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openVehicleInfoListDialog();
                // edt_vehicle_model.setText(NC.getResources().getString(R.string.vehicle_model));

            }
        });

        edt_vehicle_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog();

                if (SessionSave.getSession("model", AddFleetAct.this).equalsIgnoreCase("")) {
                    // CToast.ShowToast(AddFleetAct.this, "Kindly select vehicle manufacturer");
                    closeDialog();

                } else {

                    try {

                        JSONArray vehicle_model_Array = new JSONArray(SessionSave.getSession("model", AddFleetAct.this));

                        vehicleModlelInfos = new ArrayList<>();

                        if (vehicle_model_Array.length() > 0) {
                            for (int i = 0; i < vehicle_model_Array.length(); i++) {
                                VehicleModelInfo vehicleModelInfo = new VehicleModelInfo();
                                vehicleModelInfo._id = vehicle_model_Array.getJSONObject(i).getString("_id");
                                vehicleModelInfo.name = vehicle_model_Array.getJSONObject(i).getString("name");
                                vehicleModlelInfos.add(vehicleModelInfo);
                            }
                            openVehicleModelListDialog();

                        } else {
                            CToast.ShowToast(AddFleetAct.this, "No vehicle models available for this manufacturer");

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        imgV_vehicle_reg_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationFileName = "Image14";
                image_type = 15;
                uploadImage();

            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(km_edt.getText().toString().trim())) {
                    CToast.ShowToast(AddFleetAct.this, NC.getResources().getString(R.string.select_state));
                } else if (TextUtils.isEmpty(prefix_plate_num_edt.getText().toString().trim())) {
                    CToast.ShowToast(AddFleetAct.this, NC.getResources().getString(R.string.Plate_prefix_num));
                } else if (TextUtils.isEmpty(plate_num_edt.getText().toString().trim())) {
                    CToast.ShowToast(AddFleetAct.this, NC.getResources().getString(R.string.Plate_number));
                } else if (TextUtils.isEmpty(edt_vehicle_manufacturer.getText().toString().trim())) {
                    CToast.ShowToast(AddFleetAct.this, NC.getResources().getString(R.string.vehicle_manufacturer));
                } else if (TextUtils.isEmpty(edt_vehicle_model.getText().toString().trim())) {
                    CToast.ShowToast(AddFleetAct.this, NC.getResources().getString(R.string.vehicle_model_error));
                } else if (TextUtils.isEmpty(edt_service_type.getText().toString().trim())) {
                    CToast.ShowToast(AddFleetAct.this, NC.getResources().getString(R.string.service_type_error));
                } else if (TextUtils.isEmpty(edt_vehicle_owner_name.getText().toString().trim())) {
                    CToast.ShowToast(AddFleetAct.this, NC.getResources().getString(R.string.owner_name_error));
                } else if (TextUtils.isEmpty(color_name.trim())) {
                    CToast.ShowToast(AddFleetAct.this, NC.getResources().getString(R.string.color_error));
                } else if (TextUtils.isEmpty(edt_year_of_manufacturer.getText().toString().trim())) {
                    CToast.ShowToast(AddFleetAct.this, NC.getResources().getString(R.string.year_of_manufacturer_error));
                } else if (TextUtils.isEmpty(vehicle_img.trim())) {
                    CToast.ShowToast(AddFleetAct.this, NC.getResources().getString(R.string.upload_vehicle_img));
                } else if (!register_CheckBox.isChecked()) {
                    CToast.ShowToast(AddFleetAct.this, "Please confirm the vehicle registration");
                } else {
                    String url = "type=add_fleet";
                    new AddFleet(url);
                }


            }


        });


        try {
            JSONArray vehicle_state_Array = new JSONArray(SessionSave.getSession("vehicle_state_list", AddFleetAct.this));

            stateInfos = new ArrayList<>();

            for (int i = 0; i < vehicle_state_Array.length(); i++) {
                StateInfo stateInfo = new StateInfo();
                stateInfo.state_id = vehicle_state_Array.getJSONObject(i).getString("state_id");
                stateInfo.state_name = vehicle_state_Array.getJSONObject(i).getString("state_name");
                stateInfos.add(stateInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONArray vehicle_state_Array = new JSONArray(SessionSave.getSession("vehicle_plate_prefix_list", AddFleetAct.this));

            vehiclePrefixInfos = new ArrayList<>();

            for (int i = 0; i < vehicle_state_Array.length(); i++) {
                VehiclePrefixInfo vehiclePrefixInfo = new VehiclePrefixInfo();
                vehiclePrefixInfo._id = vehicle_state_Array.getJSONObject(i).getString("_id");
                vehiclePrefixInfo.plate_prefix = vehicle_state_Array.getJSONObject(i).getString("plate_prefix");
                vehiclePrefixInfos.add(vehiclePrefixInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONArray vehicle_state_Array = new JSONArray(SessionSave.getSession("model_details", AddFleetAct.this));

            modelListInfos = new ArrayList<>();

            for (int i = 0; i < vehicle_state_Array.length(); i++) {
                ModelListInfo modelListInfo = new ModelListInfo();
                modelListInfo._id = vehicle_state_Array.getJSONObject(i).getString("_id");
                modelListInfo.model_name = vehicle_state_Array.getJSONObject(i).getString("model_name");
                modelListInfos.add(modelListInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONArray vehicle_state_Array = new JSONArray(SessionSave.getSession("vehicle_info_list", AddFleetAct.this));

            vehicleListInfos = new ArrayList<>();


            for (int i = 0; i < vehicle_state_Array.length(); i++) {
                VehicleListInfo vehicleListInfo = new VehicleListInfo();
                vehicleListInfo._id = vehicle_state_Array.getJSONObject(i).getString("_id");
                vehicleListInfo.manufacturer_name = vehicle_state_Array.getJSONObject(i).getString("manufacturer_name");
                vehicleListInfo.model = vehicle_state_Array.getJSONObject(i).getString("model");
                vehicleListInfos.add(vehicleListInfo);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            colorInfos = new ArrayList<>();

            JSONArray color_Array = new JSONArray(SessionSave.getSession("vehicle_color_list", AddFleetAct.this));
            for (int i = 0; i < color_Array.length(); i++) {
                ColorInfo colorInfo = new ColorInfo();
                colorInfo._id = color_Array.getJSONObject(i).getString("_id");
                colorInfo.color = color_Array.getJSONObject(i).getString("color");
                colorInfos.add(colorInfo);
            }
            colorAdapter = new ColorAdapter(AddFleetAct.this, colorInfos);
            id_color.setAdapter(colorAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void pickyear() {


        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFleetAct.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.month_year_picker_dialog, viewGroup, false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        Calendar cal = Calendar.getInstance();

        final NumberPicker yearPicker = dialogView.findViewById(R.id.picker_year);

        final Button okbtn = dialogView.findViewById(R.id.okbtn);
        final Button cancelbtn = dialogView.findViewById(R.id.cancelbtn);


        int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(year - 16);
        yearPicker.setMaxValue(year);
        yearPicker.setValue(year);

        alertDialog.show();


        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                edt_year_of_manufacturer.setText(String.valueOf(yearPicker.getValue()));
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


    }


    private void pickDate(int type) {
        c = Calendar.getInstance();
        //c.add(Calendar.YEAR, -18);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddFleetAct.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String month = String.valueOf((monthOfYear + 1));
                        String day = String.valueOf(dayOfMonth);
                        if ((monthOfYear + 1) < 10) {
                            month = "0" + month;
                        }
                        if (dayOfMonth < 10) {
                            day = "0" + day;
                        }
                        String selected_date = day + "-" + month + "-" + year;
                        Log.e("selDate", selected_date);
                        if (type == 1) {
                            edt_year_of_manufacturer.setText(selected_date);
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, monthOfYear, dayOfMonth);
                            calendar.add(Calendar.YEAR, 65);
                            int exp_year = calendar.get(Calendar.YEAR);
                            int exp_month = calendar.get(Calendar.MONTH);
                            int exp_day = calendar.get(Calendar.DAY_OF_MONTH);
                            String ex_month = String.valueOf((exp_month + 1));
                            String ex_day = String.valueOf(exp_day);
                            if ((exp_month + 1) < 10) {
                                ex_month = "0" + ex_month;
                            }
                            if (exp_day < 10) {
                                ex_day = "0" + ex_day;
                            }
                            String selected_exp_date = ex_day + "-" + ex_month + "-" + exp_year;
                            //  edt_driver_national_insurance_expiry.setText(selected_exp_date);
                            Log.e("selExp", selected_exp_date);
                        }
                    }
                }, mYear, mMonth, mDay);
        if (type == 1) {
            //datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());

        } else {
            datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        }

        datePickerDialog.show();

    }


    private void openVehicleInfoListDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFleetAct.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reason_dialog, viewGroup, false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        TextView title = dialogView.findViewById(R.id.tv_title);
        title.setText(NC.getResources().getString(R.string.select_manufacturer));
        RecyclerView reason_recycle = dialogView.findViewById(R.id.reason_recycle);
        LinearLayoutManager layoutManagercancel;
        layoutManagercancel = new LinearLayoutManager(AddFleetAct.this);
        reason_recycle.setLayoutManager(layoutManagercancel);
        vehicleInfoListAdapter = new VehicleInfoListAdapter(AddFleetAct.this, vehicleListInfos);
        reason_recycle.setAdapter(vehicleInfoListAdapter);
        SessionSave.saveSession("model", "", AddFleetAct.this);
        edt_vehicle_model.setText(NC.getResources().getString(R.string.vehicle_model));
        alertDialog.show();
    }

    private void openModellist() {

        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFleetAct.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reason_dialog, viewGroup, false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        TextView title = dialogView.findViewById(R.id.tv_title);
        title.setText(NC.getResources().getString(R.string.select_service_type));
        RecyclerView reason_recycle = dialogView.findViewById(R.id.reason_recycle);
        LinearLayoutManager layoutManagercancel;
        layoutManagercancel = new LinearLayoutManager(AddFleetAct.this);
        reason_recycle.setLayoutManager(layoutManagercancel);
        modellistAdapter = new ModellistAdapter(AddFleetAct.this, modelListInfos);
        reason_recycle.setAdapter(modellistAdapter);
        alertDialog.show();
    }

    private void openDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFleetAct.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reason_dialog, viewGroup, false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        TextView title = dialogView.findViewById(R.id.tv_title);
        title.setText(NC.getResources().getString(R.string.select_state_type));
        RecyclerView reason_recycle = dialogView.findViewById(R.id.reason_recycle);
        LinearLayoutManager layoutManagercancel;
        layoutManagercancel = new LinearLayoutManager(AddFleetAct.this);
        reason_recycle.setLayoutManager(layoutManagercancel);
        stateAdapter = new StateAdapter(AddFleetAct.this, stateInfos);
        reason_recycle.setAdapter(stateAdapter);
        alertDialog.show();
    }

    private void openVehicleDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFleetAct.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reason_dialog, viewGroup, false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        TextView title = dialogView.findViewById(R.id.tv_title);
        title.setText(NC.getResources().getString(R.string.selet_prefix_type));
        RecyclerView reason_recycle = dialogView.findViewById(R.id.reason_recycle);
        LinearLayoutManager layoutManagercancel;
        layoutManagercancel = new LinearLayoutManager(AddFleetAct.this);
        reason_recycle.setLayoutManager(layoutManagercancel);
        vehicleAdapter = new VehicleAdapter(AddFleetAct.this, vehiclePrefixInfos);
        reason_recycle.setAdapter(vehicleAdapter);
        alertDialog.show();
    }


    private void openVehicleModelListDialog() {

        closeDialog();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFleetAct.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reason_dialog, viewGroup, false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        TextView title = dialogView.findViewById(R.id.tv_title);
        title.setText(NC.getResources().getString(R.string.select_vehicle_model));
        RecyclerView reason_recycle = dialogView.findViewById(R.id.reason_recycle);
        LinearLayoutManager layoutManagercancel;
        layoutManagercancel = new LinearLayoutManager(AddFleetAct.this);
        reason_recycle.setLayoutManager(layoutManagercancel);
        vehiclemodelAdapter = new VehicleModelAdapter(AddFleetAct.this, vehicleModlelInfos);
        reason_recycle.setAdapter(vehiclemodelAdapter);
        alertDialog.show();
    }

    private class AddFleet implements APIResult {
        String msg = "";

        public AddFleet(String url) {
            try {
                JSONObject j = new JSONObject();
                j.put("driver_id", SessionSave.getSession("reg_driver_Id", AddFleetAct.this));
                j.put("company_id", SessionSave.getSession("company_id", AddFleetAct.this));

                j.put("state_id", state_id);
                j.put("plate_prefix_id", plate_prefix_id);
                j.put("plate_number", plate_num_edt.getText().toString().trim());
                j.put("taxi_no", state_name + plate_prefix + "/" + plate_num_edt.getText().toString().trim());
                j.put("taxi_owner_name", edt_vehicle_owner_name.getText().toString().trim());
                j.put("taxi_manufacturer_id", manufacturer_id);
                j.put("taxi_manufacturer", manufacturer_name);
                j.put("taxi_make_id", vehicle_id);
                j.put("taxi_make", vehicle_name);

                j.put("taxi_model", model_id);
                j.put("taxi_manufacturing_year", edt_year_of_manufacturer.getText().toString().trim());
                j.put("taxi_colour_id", color_id);
                j.put("taxi_colour", color_name);
                j.put("taxi_image", vehicle_img);
                j.put("vehicle_registration_license", vehicle_reg_img);


                if (isOnline()) {
                    new APIService_Retrofit_JSON(AddFleetAct.this, this, j, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(AddFleetAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, AddFleetAct.this, "");
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, final String result) {

            try {
                if (isSuccess) {
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        msg = json.getString("message");

                      //  success_dialog(json.getJSONObject("details").getString("skip_video"));

                        //SessionSave.saveSession("taxi_id", json.getString("taxi_id"), AddFleetAct.this);
                        CToast.ShowToast(AddFleetAct.this, msg);
                        //SessionSave.saveSession(CommonData.DRIVER_RESULT, "", AddFleetAct.this);
                        Intent i = new Intent(AddFleetAct.this, DriverLoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        final Bundle bundle = new Bundle();
//                        bundle.putString("already_exist", json.getString("already_exist"));
//                        i.putExtras(bundle);
                        startActivity(i);

                    } else {
                        msg = json.getString("message");
                        dialog1 = Utils.alert_view(AddFleetAct.this, NC.getResources().getString(R.string.message), msg, NC.getResources().getString(R.string.ok), "", true, AddFleetAct.this, "");
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // CToast.ShowToast(AddFleetAct.this, NC.getString(R.string.server_error));
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }




    public void uploadImage() {
        try {
            if (ActivityCompat.checkSelfPermission(AddFleetAct.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                cameraDialog = Utils.alert_view_dialog(AddFleetAct.this, "", NC.getResources().getString(R.string.str_media_access), NC.getResources().getString(R.string.yes), "", true, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        ActivityCompat.requestPermissions(AddFleetAct.this,
                                new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                        dialog.dismiss();
                    }
                }, (dialogInterface, i) -> dialogInterface.dismiss(), "");
            } else
                getCamera();
        } catch (Exception e) {

            // TODO: handle exception
        }
    }

    private void getCamera() {
        //if image_type == 14 means image otherwise document scan
       // if (image_type == 14) {
            dialog1 = Utils.alert_view_dialog(this, NC.getResources().getString(R.string.profile_image), NC.getResources().getString(R.string.choose_an_image), NC.getResources().getString(R.string.camera), NC.getResources().getString(R.string.gallery), true, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.cancel();

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                imageUri = FileProvider.getUriForFile(AddFleetAct.this,
                                        AddFleetAct.this.getPackageName().concat(".files_root"),
                                        photoFile);
                            } else {
                                imageUri = Uri.fromFile(photoFile);
                            }

                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(takePictureIntent, 1);
                        }
                    }


                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    System.gc();
                    final Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(intent, 0);
                    dialog.cancel();
                }
            }, "");
       /* } else {
            uploadDocument();
        }*/


    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    @Override
    public void onActivityResult(final int requestcode, final int resultcode, final Intent data) {
        super.onActivityResult(requestcode, resultcode, data);
        try {
           /* if (requestcode == REQUEST_CODE && resultcode == Activity.RESULT_OK && data.getExtras() != null && data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT) != null) {
                Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    getContentResolver().delete(uri, null, null);
                    if (image_type == 11) {
                        if (ll_vehicle_pco_lic_txt.getVisibility() == View.VISIBLE) {
                            ll_vehicle_pco_lic_txt.setVisibility(View.GONE);
                        }
                        imgV_pco_license.setImageBitmap(bitmap);
                    } else if (image_type == 12) {
                        if (ll_vehicle_mot_txt.getVisibility() == View.VISIBLE) {
                            ll_vehicle_mot_txt.setVisibility(View.GONE);
                        }
                        imgV_vehicle_mot.setImageBitmap(bitmap);
                    } else if (image_type == 14) {
                        if (ll_vehicle_insurance_txt.getVisibility() == View.VISIBLE) {
                            ll_vehicle_insurance_txt.setVisibility(View.GONE);
                        }
                        imgV_vehicle_insurance.setImageBitmap(bitmap);
                    } else if (image_type == 15) {
                        if (ll_vehicle_reg_img_txt.getVisibility() == View.VISIBLE) {
                            ll_vehicle_reg_img_txt.setVisibility(View.GONE);
                        }
                        imgV_vehicle_reg_image.setImageBitmap(bitmap);
                        vehicle_reg_img = Utils.convertToBase64(imgV_vehicle_reg_image);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {*/
                if (requestcode == UCrop.REQUEST_CROP) {
                    handleCropResult(data);
                } else if (resultcode == RESULT_OK) {
                    switch (requestcode) {
                        case 0:
                            try {
                                UCrop uCrop = UCrop.of(Uri.fromFile(new File(getRealPathFromURI(data.getDataString()))), Uri.fromFile(new File(AddFleetAct.this.getCacheDir(), destinationFileName)))
                                        .useSourceImageAspectRatio().withAspectRatio(1, 1)
                                        .withMaxResultSize(400, 400);
                                UCrop.Options options = new UCrop.Options();
                                options.setToolbarColor(ContextCompat.getColor(AddFleetAct.this, R.color.pure_white));
                                options.setStatusBarColor(ContextCompat.getColor(AddFleetAct.this, R.color.hdr_txt_primary));
                                options.setToolbarWidgetColor(ContextCompat.getColor(AddFleetAct.this, R.color.hdr_txt_primary));
                                options.setMaxBitmapSize(1000000000);
                                uCrop.withOptions(options);
                                uCrop.start(AddFleetAct.this);
                            } catch (final Exception e) {
                            }
                            break;
                        case 1:
                            try {
                                UCrop.of(imageUri, Uri.fromFile(new File(AddFleetAct.this.getCacheDir(), destinationFileName)))
                                        .withAspectRatio(1, 1)
                                        .withMaxResultSize(2000, 2000)
                                        .start(AddFleetAct.this);
                            } catch (final Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }

            //}
        } catch (final Exception e) {
        }
    }


    private String getRealPathFromURI(final String contentURI) {

        final Uri contentUri = Uri.parse(contentURI);
        final Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null)
            return contentUri.getPath();
        else {
            cursor.moveToFirst();
            final int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            new ImageCompressionAsyncTask().execute(resultUri.toString());
        } else {
            // Toast.makeText(SampleActivity.this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    private class ImageCompressionAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private Dialog mDialog;
        private String result;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            final View view = View.inflate(AddFleetAct.this, R.layout.progress_bar, null);
            mDialog = new Dialog(AddFleetAct.this, R.style.NewDialog);
            mDialog.setContentView(view);
            mDialog.setCancelable(false);
            mDialog.show();

            ImageView iv = mDialog.findViewById(R.id.giff);
            DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
            Glide.with(AddFleetAct.this)
                    .load(R.raw.loading_anim)
                    .into(imageViewTarget);
        }

        @Override
        protected Bitmap doInBackground(final String... params) {
            Bitmap mBitmap = null;
            try {
                result = getRealPathFromURI(params[0]);
                final File file = new File(result);

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                mBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                final byte[] image = stream.toByteArray();
                encodeImg = Base64.encodeToString(image, Base64.DEFAULT);

            } catch (final Exception e) {
                // TODO: handle exception
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CToast.ShowToast(AddFleetAct.this, NC.getResources().getString(R.string.image_failed));
                    }
                });
            }
            return mBitmap;
        }

        @Override
        protected void onPostExecute(final Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                if (mDialog.isShowing())
                    mDialog.dismiss();
                if (result != null) {
                    if (image_type == 14) {
                        if (ll_vehicle_img_txt.getVisibility() == View.VISIBLE) {
                            ll_vehicle_img_txt.setVisibility(View.GONE);
                        }
                        vehicle_img = encodeImg;
                        imgV_vehicle_image.setImageBitmap(result);
                    }else   if (image_type == 15) {

                        if (ll_vehicle_reg_img_txt.getVisibility() == View.VISIBLE) {
                            ll_vehicle_reg_img_txt.setVisibility(View.GONE);
                        }
                        imgV_vehicle_reg_image.setImageBitmap(result);
                        vehicle_reg_img = encodeImg;


                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                getCamera();

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                //  finish();
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        Intent in = new Intent(AddFleetAct.this, DriverRegisterActStepOne.class);
//        final Bundle bundle = new Bundle();
//        in.putExtras(bundle);
//        startActivity(in);
//        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog1 != null) {
            dialog1.dismiss();
        }
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    private class StateAdapter extends RecyclerView.Adapter<StateAdapter.ViewHolder> {
        Context context;
        private final List<StateInfo> stateData;


        public StateAdapter(Context context, List<StateInfo> data) {
            this.context = context;
            this.stateData = data;
        }

        @NonNull
        @Override
        public StateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(AddFleetAct.this);
            View view = null;
            view = inflater.inflate(R.layout.reason_list, parent, false);
            
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StateAdapter.ViewHolder holder, int position) {

            holder.reasontxt.setText(stateData.get(position).state_name);
            holder.reasontxt.setOnClickListener(v -> {
                state_id = stateData.get(position).state_id;
                state_name = stateData.get(position).state_name;
                notifyDataSetChanged();
                canceldialog();
            });
            holder.lay_reason.setOnClickListener(v -> {

                state_id = stateData.get(position).state_id;
                state_name = stateData.get(position).state_name;
                notifyDataSetChanged();
                canceldialog();

            });
        }

        @Override
        public int getItemCount() {
            return stateData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView reasontxt;
            AppCompatImageView check1;
            RelativeLayout lay_reason;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                reasontxt = itemView.findViewById(R.id.reason_txt);
                check1 = itemView.findViewById(R.id.check1);
                lay_reason = itemView.findViewById(R.id.lay_reason);
            }

        }
    }


    private class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {
        Context context;
        private final List<VehiclePrefixInfo> vehiclePrefixData;


        public VehicleAdapter(Context context, List<VehiclePrefixInfo> data) {
            this.context = context;
            this.vehiclePrefixData = data;
        }

        @NonNull
        @Override
        public VehicleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(AddFleetAct.this);
            View view = null;
            view = inflater.inflate(R.layout.reason_list, parent, false);
            
            return new VehicleAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VehicleAdapter.ViewHolder holder, int position) {
            holder.reasontxt.setText(vehiclePrefixData.get(position).plate_prefix);
            holder.reasontxt.setOnClickListener(v -> {
                plate_prefix_id = vehiclePrefixData.get(position)._id;
                plate_prefix = vehiclePrefixData.get(position).plate_prefix;
                notifyDataSetChanged();
                cancelVehicle();

            });
            holder.lay_reason.setOnClickListener(v -> {

                plate_prefix_id = vehiclePrefixData.get(position)._id;
                plate_prefix = vehiclePrefixData.get(position).plate_prefix;
                notifyDataSetChanged();
                cancelVehicle();


            });
        }

        @Override
        public int getItemCount() {
            return vehiclePrefixData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView reasontxt;
            AppCompatImageView check1;
            RelativeLayout lay_reason;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                reasontxt = itemView.findViewById(R.id.reason_txt);
                check1 = itemView.findViewById(R.id.check1);
                lay_reason = itemView.findViewById(R.id.lay_reason);
            }

        }
    }


    private class ModellistAdapter extends RecyclerView.Adapter<ModellistAdapter.ViewHolder> {

        Context context;
        private final List<ModelListInfo> modelListData;


        public ModellistAdapter(Context context, List<ModelListInfo> data) {
            this.context = context;
            this.modelListData = data;
        }

        @NonNull
        @Override
        public ModellistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(AddFleetAct.this);
            View view = null;
            view = inflater.inflate(R.layout.reason_list, parent, false);
            
            return new ModellistAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ModellistAdapter.ViewHolder holder, int position) {
            holder.reasontxt.setText(modelListData.get(position).model_name);
            holder.reasontxt.setOnClickListener(v -> {
                model_id = modelListData.get(position)._id;
                model_name = modelListData.get(position).model_name;
                notifyDataSetChanged();
                cancelModelDialog();

            });
            holder.lay_reason.setOnClickListener(v -> {

                model_id = modelListData.get(position)._id;
                model_name = modelListData.get(position).model_name;
                notifyDataSetChanged();
                cancelModelDialog();

            });
        }

        @Override
        public int getItemCount() {
            return modelListData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView reasontxt;
            AppCompatImageView check1;
            RelativeLayout lay_reason;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                reasontxt = itemView.findViewById(R.id.reason_txt);
                check1 = itemView.findViewById(R.id.check1);
                lay_reason = itemView.findViewById(R.id.lay_reason);
            }

        }
    }

    private class VehicleInfoListAdapter extends RecyclerView.Adapter<VehicleInfoListAdapter.ViewHolder> {
        Context context;
        private final List<VehicleListInfo> VehiclelistinfoData;

        public VehicleInfoListAdapter(Context context, List<VehicleListInfo> data) {
            this.context = context;
            this.VehiclelistinfoData = data;
        }

        @NonNull
        @Override
        public VehicleInfoListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(AddFleetAct.this);
            View view = null;
            view = inflater.inflate(R.layout.reason_list, parent, false);
            
            return new VehicleInfoListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VehicleInfoListAdapter.ViewHolder holder, int position) {
            holder.reasontxt.setText(VehiclelistinfoData.get(position).manufacturer_name);

//            SessionSave.saveSession("model", ""
//                    , AddFleetAct.this);

            holder.reasontxt.setOnClickListener(v -> {
                edt_vehicle_model.setText(NC.getResources().getString(R.string.vehicle_model));

               SessionSave.saveSession("model", VehiclelistinfoData.get(position).model
                        , AddFleetAct.this);
                manufacturer_id = VehiclelistinfoData.get(position)._id;
                manufacturer_name = VehiclelistinfoData.get(position).manufacturer_name;
                notifyDataSetChanged();
                cancelVehicleListDialog();

            });
            holder.lay_reason.setOnClickListener(v -> {

                edt_vehicle_model.setText(NC.getResources().getString(R.string.vehicle_model));

                SessionSave.saveSession("model", VehiclelistinfoData.get(position).model
                        , AddFleetAct.this);

                manufacturer_id = VehiclelistinfoData.get(position)._id;
                manufacturer_name = VehiclelistinfoData.get(position).manufacturer_name;
                notifyDataSetChanged();
                cancelVehicleListDialog();

            });
        }

        @Override
        public int getItemCount() {
            return VehiclelistinfoData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView reasontxt;
            AppCompatImageView check1;
            RelativeLayout lay_reason;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                reasontxt = itemView.findViewById(R.id.reason_txt);
                check1 = itemView.findViewById(R.id.check1);
                lay_reason = itemView.findViewById(R.id.lay_reason);
            }

        }
    }


    private class VehicleModelAdapter extends RecyclerView.Adapter<VehicleModelAdapter.ViewHolder> {
        Context context;
        private final List<VehicleModelInfo> VehicleModelinfoData;

        public VehicleModelAdapter(Context context, List<VehicleModelInfo> data) {
            this.context = context;
            this.VehicleModelinfoData = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(AddFleetAct.this);
            View view = null;
            view = inflater.inflate(R.layout.reason_list, parent, false);
            
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.reasontxt.setText(VehicleModelinfoData.get(position).name);
            holder.reasontxt.setOnClickListener(v -> {


                vehicle_id = VehicleModelinfoData.get(position)._id;
                vehicle_name = VehicleModelinfoData.get(position).name;
                notifyDataSetChanged();
                cancelVehicleModelDialog();

            });
            holder.lay_reason.setOnClickListener(v -> {

                vehicle_id = VehicleModelinfoData.get(position)._id;
                vehicle_name = VehicleModelinfoData.get(position).name;
                notifyDataSetChanged();
                cancelVehicleModelDialog();

            });
        }

        @Override
        public int getItemCount() {
            return VehicleModelinfoData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView reasontxt;
            AppCompatImageView check1;
            RelativeLayout lay_reason;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                reasontxt = itemView.findViewById(R.id.reason_txt);
                check1 = itemView.findViewById(R.id.check1);
                lay_reason = itemView.findViewById(R.id.lay_reason);
            }

        }
    }


    private void cancelVehicleListDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        edt_vehicle_manufacturer.setText(manufacturer_name);

    }

    private void cancelVehicleModelDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        edt_vehicle_model.setText(vehicle_name);

    }

    private void cancelModelDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        edt_service_type.setText(model_name);
    }

    private void cancelVehicle() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        prefix_plate_num_edt.setText(plate_prefix);
    }

    private void canceldialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        km_edt.setText(state_name);
    }


    private class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {
        Context context;
        private final List<ColorInfo> colorData;
        int selectpos = -1;

        public ColorAdapter(Context context, List<ColorInfo> data) {
            this.context = context;
            this.colorData = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(AddFleetAct.this);
            View view = null;
            view = inflater.inflate(R.layout.color_list, parent, false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            if (selectpos == position) {
                holder.color_image_shadow.setVisibility(View.VISIBLE);
            } else {
                holder.color_image_shadow.setVisibility(View.GONE);
            }

//            holder.reasontxt.setOnClickListener(v -> {
//               // model_id = modelData.get(position).model_id;
//                //model_name = modelData.get(position).model_name;
//                notifyDataSetChanged();
//            });
            holder.lay_reason.setOnClickListener(v -> {
                //  model_id = modelData.get(position).model_id;
                // model_name = modelData.get(position).model_name;

                //  holder.color_image_shadow.setVisibility(View.VISIBLE);
                selectpos = position;
                color_id = colorData.get(position)._id;
                color_name = colorData.get(position).color;

                notifyDataSetChanged();

//                if (holder.color_image_shadow.getVisibility() == View.VISIBLE) {
//                    holder.color_image_shadow.setVisibility(View.GONE);
//                }else {
//                    holder.color_image_shadow.setVisibility(View.VISIBLE);
//                }


            });

            holder.color_image.getDrawable().setColorFilter(Color.parseColor(colorData.get(position).color), PorterDuff.Mode.MULTIPLY);


        }

        @Override
        public int getItemCount() {
            return colorData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView color_image, color_image_shadow;
            RelativeLayout lay_reason;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                color_image = itemView.findViewById(R.id.color_image);
                color_image_shadow = itemView.findViewById(R.id.color_image_shadow);
                lay_reason = itemView.findViewById(R.id.lay_reason);
            }

        }
    }


    public void showDialog() {
        try {
            if (NetworkStatus.isOnline(AddFleetAct.this)) {
                if (loadingDialog != null && loadingDialog.isShowing())
                    loadingDialog.dismiss();
                View view = View.inflate(AddFleetAct.this, R.layout.progress_bar, null);
                loadingDialog = new Dialog(AddFleetAct.this, R.style.dialogwinddow);
                loadingDialog.setContentView(view);
                loadingDialog.setCancelable(false);
                if (this != null)
                    loadingDialog.show();

                ImageView iv = loadingDialog.findViewById(R.id.giff);
                DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
                Glide.with(this)
                        .load(R.raw.loading_anim)
                        .into(imageViewTarget);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //method to close dialog
    public void closeDialog() {

        try {
            if (loadingDialog != null)
                if (loadingDialog.isShowing())
                    loadingDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}