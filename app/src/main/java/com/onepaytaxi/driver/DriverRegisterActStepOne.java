package com.onepaytaxi.driver;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.interfaces.APIResult;
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON;
import com.onepaytaxi.driver.utils.CToast;
import com.onepaytaxi.driver.utils.NC;
import com.onepaytaxi.driver.utils.SessionSave;
import com.onepaytaxi.driver.utils.Utils;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DriverRegisterActStepOne extends MainActivity implements AdapterView.OnItemSelectedListener {
    private TextView HeadTitle, btn_nxt;

    private ImageView imgV_license, imgV_profile, imgV_license_back;
    private EditText edt_first_name, edt_last_name, edt_email, edt_pswd, edt_cnfrm_pswd, cardnoEdt, nation_id_Edt, referalCdeEdt;

    private Spinner edt_gender;
    private RadioGroup rb_gender;
    private LinearLayout ll_profile_pic_txt, ll_driver_lic_txt, ll_driver_lic_back_txt;
    private String gender = "", license_img = "", profile_img = "", encodeImg = "", license_back_img;
    private int mYear, mMonth, mDay;
    private Calendar c;
    private Dialog cameraDialog, dialog1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 113;
    private String driver_id = "";
    private Uri imageUri;
    private String destinationFileName = "Image1", dbs_expiry_date = "";
    private int image_type = 1;
    private static final int REQUEST_CODE = 99;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 222;
    private final Double lat = 0.0;
    private final Double lng = 0.0;
    private AlertDialog alertDialog;
    private final String selectedTime = "";
    private final String already_exist = "0";
    private EditText edt_driver_license, edt_driver_license_exp;
    private TextView back_Txt;
private CardView back_trip_details;

    @Override
    public int setLayout() {
        return R.layout.activity_driver_register_step_one;
    }

    @Override
    public void Initialize() {
        HeadTitle = findViewById(R.id.header_titleTxt);
//        HeadTitle.setText(R.string.driver_registration);
        edt_first_name = findViewById(R.id.edt_first_name);
        edt_last_name = findViewById(R.id.edt_last_name);
        edt_email = findViewById(R.id.edt_email);
        edt_pswd = findViewById(R.id.edt_pswd);
        edt_cnfrm_pswd = findViewById(R.id.edt_cnfrm_pswd);
        ll_driver_lic_txt = findViewById(R.id.ll_driver_lic_txt);
        ll_driver_lic_back_txt = findViewById(R.id.ll_driver_lic_back_txt);
        imgV_license = findViewById(R.id.imgV_license);
        imgV_license_back = findViewById(R.id.imgV_license_back);
        referalCdeEdt = findViewById(R.id.referalCdeEdt);
        edt_gender = findViewById(R.id.edt_gender);
     //   FontHelper.applyFont(this, findViewById(R.id.slide_lay));

        TextView HeadTitle = findViewById(R.id.header_titleTxt);
        //  HeadTitle.setText("" + com.seero.driver.utils.NC.getResources().getString(R.string.driver_reg));
        edt_driver_license = findViewById(R.id.edt_driver_license);
        edt_driver_license_exp = findViewById(R.id.edt_driver_license_exp);
        rb_gender = findViewById(R.id.rb_gender);
        ll_profile_pic_txt = findViewById(R.id.ll_profile_pic_txt);
        imgV_profile = findViewById(R.id.imgV_profile);
        btn_nxt = findViewById(R.id.btn_nxt);
        back_Txt = findViewById(R.id.back_Txt);
        back_trip_details = findViewById(R.id.back_trip_details);
      //  cardnoEdt = findViewById(R.id.cardnoEdt);
       // nation_id_Edt = findViewById(R.id.nation_id_Edt);
        edt_gender.setOnItemSelectedListener(this);

     //   cardnoEdt.addTextChangedListener(new FourDigitCardFormatWatcher(DriverRegisterActStepOne.this));

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        edt_gender.setAdapter(adapter);



        rb_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getCheckedRadioButtonId() == R.id.rb_male) {
                    gender = "Male";
                } else if (group.getCheckedRadioButtonId() == R.id.rb_female) {
                    gender = "Female";
                }

            }
        });

        ll_driver_lic_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationFileName = "Image1";
                image_type = 1; // document
                uploadImage();
            }
        });
        ll_driver_lic_back_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationFileName = "Image11";
                image_type = 11; // document
                uploadImage();
            }
        });

        imgV_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationFileName = "Image1";
                image_type = 1; // document
                uploadImage();
            }
        });

        imgV_license_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationFileName = "Image11";
                image_type = 11; // document
                uploadImage();
            }
        });

        edt_driver_license_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(2);
            }
        });


        ll_profile_pic_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationFileName = "Image3";
                image_type = 3; // image
                uploadImage();
            }
        });

        back_Txt.setOnClickListener(new View.OnClickListener() {
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

        btn_nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edt_first_name.getText().toString().trim())) {
                    CToast.ShowToast(com.onepaytaxi.driver.DriverRegisterActStepOne.this, NC.getResources().getString(R.string.enter_the_first_name));
                } else if (edt_first_name.getText().toString().trim().length() < 1) {
                    CToast.ShowToast(com.onepaytaxi.driver.DriverRegisterActStepOne.this, NC.getResources().getString(R.string.error_first_name));
                } else if (TextUtils.isEmpty(gender.trim())) {
                    CToast.ShowToast(com.onepaytaxi.driver.DriverRegisterActStepOne.this, NC.getResources().getString(R.string.select_gender));
                }
                else if (TextUtils.isEmpty(edt_email.getText().toString().trim())) {
                    CToast.ShowToast(DriverRegisterActStepOne.this, NC.getResources().getString(R.string.enter_the_email));
                }

                else if (!validdmail(edt_email.getText().toString().trim())) {
                    CToast.ShowToast(DriverRegisterActStepOne.this, NC.getResources().getString(R.string.enter_the_valid_email));
                }
                else if (TextUtils.isEmpty(edt_pswd.getText().toString().trim())) {
                    CToast.ShowToast(com.onepaytaxi.driver.DriverRegisterActStepOne.this, NC.getResources().getString(R.string.enter_the_password));
                } else if (edt_pswd.getText().toString().trim().length() < 5) {
                    CToast.ShowToast(com.onepaytaxi.driver.DriverRegisterActStepOne.this, NC.getResources().getString(R.string.pwd_min));
                } else if (TextUtils.isEmpty(edt_cnfrm_pswd.getText().toString().trim())) {
                    CToast.ShowToast(com.onepaytaxi.driver.DriverRegisterActStepOne.this, NC.getResources().getString(R.string.enter_the_confirmation_password));
                } else if (!edt_pswd.getText().toString().trim().matches(edt_cnfrm_pswd.getText().toString().trim())) {
                    CToast.ShowToast(com.onepaytaxi.driver.DriverRegisterActStepOne.this, NC.getResources().getString(R.string.confirmation_password_mismatch_with_password));
                } else if (TextUtils.isEmpty(edt_driver_license.getText().toString().trim())) {
                    CToast.ShowToast(DriverRegisterActStepOne.this, NC.getResources().getString(R.string.enter_license));
                } else if (TextUtils.isEmpty(edt_driver_license_exp.getText().toString().trim())) {
                    CToast.ShowToast(DriverRegisterActStepOne.this, NC.getResources().getString(R.string.select_license_expiry));
                } else if (TextUtils.isEmpty(license_img.trim())) {
                    CToast.ShowToast(DriverRegisterActStepOne.this, NC.getResources().getString(R.string.upload_lic));
                } else if (TextUtils.isEmpty(profile_img.trim())) {
                    CToast.ShowToast(com.onepaytaxi.driver.DriverRegisterActStepOne.this, NC.getResources().getString(R.string.upload_profile));
                } else {
                    String url = "type=add_driver";
                    new Registration(url);
                }

/*
                SessionSave.getSession(CommonData.IS_BANKID_MANTATORY, DriverRegisterActStepOne.this, false) &&
*/

/*else if ( TextUtils.isEmpty(cardnoEdt.getText().toString().trim())) {
                    CToast.ShowToast(com.seero.driver.DriverRegisterActStepOne.this, NC.getResources().getString(R.string.update_bankid));
                }*/

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent in = new Intent(com.seero.driver.DriverRegisterActStepOne.this, UserLoginAct.class);
//        startActivity(in);
//        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (!parent.getSelectedItem().toString().equals("Select gender")) {
            gender = parent.getSelectedItem().toString();
        } else {
            gender = "";
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class Registration implements APIResult {
        String msg = "";

        public Registration(String url) {

            try {
                JSONObject driver_info = new JSONObject();
                driver_info.put("driver_id", SessionSave.getSession("reg_driver_Id", DriverRegisterActStepOne.this));
                driver_info.put("phone", SessionSave.getSession("phone", DriverRegisterActStepOne.this));

                driver_info.put("firstname", edt_first_name.getText().toString().trim());
                driver_info.put("lastname", edt_last_name.getText().toString().trim());
                driver_info.put("gender", gender);
                driver_info.put("email", edt_email.getText().toString().trim());
                driver_info.put("password", edt_pswd.getText().toString().trim());
                driver_info.put("repassword", edt_cnfrm_pswd.getText().toString().trim());
                driver_info.put("driver_license_id", edt_driver_license.getText().toString().trim());
                driver_info.put("driver_license_expire_date", edt_driver_license_exp.getText().toString().trim());
//                driver_info.put("bank_id", cardnoEdt.getText().toString().trim());
//                driver_info.put("national_id", nation_id_Edt.getText().toString().trim());
                driver_info.put("referral_code", referalCdeEdt.getText().toString().trim());
                driver_info.put("profile_picture", profile_img);
                driver_info.put("driver_licence", license_img);
                driver_info.put("license_back_side", license_back_img);
                if (isOnline()) {
                    new APIService_Retrofit_JSON(com.onepaytaxi.driver.DriverRegisterActStepOne.this, this, driver_info, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(com.onepaytaxi.driver.DriverRegisterActStepOne.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, com.onepaytaxi.driver.DriverRegisterActStepOne.this, "");
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
                        //CToast.ShowToast(DriverRegisterActStepOne.this, msg);
                        if (json.has("details") && json.getJSONObject("details").has("driver_id")) {

                            driver_id = json.getJSONObject("details").getString("driver_id");
                            SessionSave.saveSession("driver_name", edt_first_name.getText().toString().trim(), com.onepaytaxi.driver.DriverRegisterActStepOne.this);

                            SessionSave.saveSession("reg_driver_Id", driver_id, com.onepaytaxi.driver.DriverRegisterActStepOne.this);
                            SessionSave.saveSession(CommonData.DRIVER_RESULT, result, com.onepaytaxi.driver.DriverRegisterActStepOne.this);
                            SessionSave.saveSession("company_id", json.getJSONObject("details").getString("company_id"), com.onepaytaxi.driver.DriverRegisterActStepOne.this);
                            if (json.getJSONObject("details").has("model_details")) {
                                SessionSave.saveSession("model_details", json.getJSONObject("details").getString("model_details"), DriverRegisterActStepOne.this);

                            }
                            Intent intent = new Intent(DriverRegisterActStepOne.this, AddFleetAct.class);
                            startActivity(intent);

                            // sathish
                           /* Intent in = new Intent(com.seero.driver.DriverRegisterActStepOne.this, DriverRegisterActStepTwo.class);
                            final Bundle bundle = new Bundle();
                            bundle.putString("already_exist", json.getJSONObject("details").getString("already_exist"));
                            in.putExtras(bundle);
                            startActivity(in);*/
                        }
                    } else {
                        msg = json.getString("message");
                        dialog1 = Utils.alert_view(com.onepaytaxi.driver.DriverRegisterActStepOne.this, NC.getResources().getString(R.string.message), msg, NC.getResources().getString(R.string.ok), "", true, com.onepaytaxi.driver.DriverRegisterActStepOne.this, "");
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // CToast.ShowToast(com.seero.driver.DriverRegisterActStepOne.this, NC.getString(R.string.server_error));
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void pickDate(int type) {
        c = Calendar.getInstance();
        c.add(Calendar.YEAR, -18);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(DriverRegisterActStepOne.this, R.style.DatePickerTheme,
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
                        } else if (type == 2) {
                            edt_driver_license_exp.setText(selected_date);
                        } else if (type == 3) {
                            //   edt_driver_pco_license_date.setText(selected_date);
                        } else if (type == 4) {
                            //   edt_driver_insurance_expiry.setText(selected_date);
                        } else if (type == 5) {
                            //  edt_driver_national_insurance_expiry.setText(selected_date);
                        } else if (type == 6) {
                            dbs_expiry_date = selected_date;
                            //    showTimePicker(1);
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

   /* public void showTimePicker(int type) {
        selectedTime = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(com.seero.driver.DriverRegisterActStepOne.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.time_picker_dialog, viewGroup, false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        TextView tv_dismss = dialogView.findViewById(R.id.tv_dismss);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String hour = "" + hourOfDay;
                String min = "" + minute;
                if (hourOfDay < 10) {
                    hour = "0" + hourOfDay;
                }
                if (minute < 10) {
                    min = "0" + minute;
                }
                selectedTime = "" + hour + ":" + min + ":00";
                Log.e("time", selectedTime);

            }
        });

        tv_dismss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(selectedTime.trim())) {
                    edt_dbs_cert_expiry_date_time.setText(dbs_expiry_date + " " + selectedTime);
                    alertDialog.dismiss();
                } else {
                    CToast.ShowToast(com.seero.driver.DriverRegisterActStepOne.this, NC.getResources().getString(R.string.select_time));
                }

            }
        });

        alertDialog.show();
    }*/

   /* public void uploadDocument() {
//        Intent intent = new Intent(this, ScanActivity.class);
//        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);
//        startActivityForResult(intent, REQUEST_CODE);

        dialog1 = Utils.alert_view_dialog(this, NC.getResources().getString(R.string.profile_image), NC.getResources().getString(R.string.choose_an_image), NC.getResources().getString(R.string.camera), NC.getResources().getString(R.string.gallery), true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();

                Intent intent = new Intent(DriverRegisterActStepOne.this, ScanActivity.class);
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);
                startActivityForResult(intent, REQUEST_CODE);


            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
                Intent intent = new Intent(DriverRegisterActStepOne.this, ScanActivity.class);
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_MEDIA);
                startActivityForResult(intent, REQUEST_CODE);
            }
        }, "");
    }*/

    public void uploadImage() {
        try {
            if (ContextCompat.checkSelfPermission(DriverRegisterActStepOne.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                cameraDialog = Utils.alert_view_dialog(DriverRegisterActStepOne.this, "", NC.getResources().getString(R.string.str_media_access), NC.getResources().getString(R.string.yes), "", true, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {


                        ActivityCompat.requestPermissions(DriverRegisterActStepOne.this,
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
        //if image_type == 3 means image otherwise document scan
        //   if (image_type == 3) {
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
                            imageUri = FileProvider.getUriForFile(com.onepaytaxi.driver.DriverRegisterActStepOne.this,
                                    com.onepaytaxi.driver.DriverRegisterActStepOne.this.getPackageName().concat(".files_root"),
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
//        } else {
//            uploadDocument();
//        }


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
            //   if (requestcode == REQUEST_CODE && resultcode == Activity.RESULT_OK && data.getExtras() != null/* && data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT) != null*/) {
               /* Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    getContentResolver().delete(uri, null, null);
                    if (image_type == 1) {
                        if (ll_driver_lic_txt.getVisibility() == View.VISIBLE) {
                            ll_driver_lic_txt.setVisibility(View.GONE);
                        }
                        imgV_license.setImageBitmap(bitmap);
                        license_img = Utils.convertToBase64(imgV_license);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            // } else {
            if (requestcode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            } else if (resultcode == RESULT_OK) {
                switch (requestcode) {
                    case 0:
                        try {
                            UCrop uCrop = UCrop.of(Uri.fromFile(new File(getRealPathFromURI(data.getDataString()))), Uri.fromFile(new File(com.onepaytaxi.driver.DriverRegisterActStepOne.this.getCacheDir(), destinationFileName)))
                                    .useSourceImageAspectRatio().withAspectRatio(1, 1)
                                    .withMaxResultSize(400, 400);
                            UCrop.Options options = new UCrop.Options();
                            options.setToolbarColor(ContextCompat.getColor(com.onepaytaxi.driver.DriverRegisterActStepOne.this, R.color.pure_white));
                            options.setStatusBarColor(ContextCompat.getColor(com.onepaytaxi.driver.DriverRegisterActStepOne.this, R.color.hdr_txt_primary));
                            options.setToolbarWidgetColor(ContextCompat.getColor(com.onepaytaxi.driver.DriverRegisterActStepOne.this, R.color.hdr_txt_primary));
                            options.setMaxBitmapSize(1000000000);
                            uCrop.withOptions(options);
                            uCrop.start(com.onepaytaxi.driver.DriverRegisterActStepOne.this);
                        } catch (final Exception e) {
                        }
                        break;
                    case 1:
                        try {
                            UCrop.of(imageUri, Uri.fromFile(new File(com.onepaytaxi.driver.DriverRegisterActStepOne.this.getCacheDir(), destinationFileName)))
                                    .withAspectRatio(1, 1)
                                    .withMaxResultSize(2000, 2000)
                                    .start(com.onepaytaxi.driver.DriverRegisterActStepOne.this);
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
            // }

        } catch (final Exception e) {
            e.printStackTrace();
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
            final View view = View.inflate(com.onepaytaxi.driver.DriverRegisterActStepOne.this, R.layout.progress_bar, null);
            mDialog = new Dialog(com.onepaytaxi.driver.DriverRegisterActStepOne.this, R.style.NewDialog);
            mDialog.setContentView(view);
            mDialog.setCancelable(false);
            mDialog.show();

            ImageView iv = mDialog.findViewById(R.id.giff);
            DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
            Glide.with(com.onepaytaxi.driver.DriverRegisterActStepOne.this)
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
                        CToast.ShowToast(com.onepaytaxi.driver.DriverRegisterActStepOne.this, NC.getResources().getString(R.string.image_failed));
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
                    if (image_type == 3) {
                        if (ll_profile_pic_txt.getVisibility() == View.VISIBLE) {
                            ll_profile_pic_txt.setVisibility(View.GONE);
                        }
                        profile_img = encodeImg;
                        imgV_profile.setImageBitmap(result);
                    } else if (image_type == 1) {
                        if (ll_driver_lic_txt.getVisibility() == View.VISIBLE) {
                            ll_driver_lic_txt.setVisibility(View.GONE);
                        }
                        imgV_license.setImageBitmap(result);
                        license_img = encodeImg;
                    } else if (image_type == 11) {
                        if (ll_driver_lic_back_txt.getVisibility() == View.VISIBLE) {
                            ll_driver_lic_back_txt.setVisibility(View.GONE);
                        }
                        imgV_license_back.setImageBitmap(result);
                        license_back_img = encodeImg;
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
    protected void onDestroy() {
        super.onDestroy();
        if (dialog1 != null) {
            dialog1.dismiss();
        }
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }


}




    
    
    
    
