package com.onepaytaxi.driver.Login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.onepaytaxi.driver.AddFleetAct;
import com.onepaytaxi.driver.DriverRegisterActStepOne;

import com.onepaytaxi.driver.Login.smsverify.SmsReceiver;
import com.onepaytaxi.driver.MainActivity;
import com.onepaytaxi.driver.R;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.interfaces.APIResult;
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON;
import com.onepaytaxi.driver.utils.FontHelper;
import com.onepaytaxi.driver.utils.NC;
import com.onepaytaxi.driver.utils.SessionSave;
import com.onepaytaxi.driver.utils.Systems;
import com.onepaytaxi.driver.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//import com.google.firebase.analytics.FirebaseAnalytics;

public class VerificationActivity extends MainActivity implements View.OnClickListener {
    private TextView minstext, minscolon, otp_number;
    private TextView resendText,resend_text_new, Phone_number_text, edt_number, countdown_secs, forget_password;
    private EditText verifyno1Txt, verifyno2Txt, verifyno3Txt, verifyno4Txt, user_password;
    private Button continue_password;

    private LinearLayout  submitOtp;

    private LinearLayout timer_lays;
    private ImageView back_click;
    private Dialog mDialog, dialog;
    private Boolean isShowOtpScreen = false;
    private String phone_number, country_code;
    private final int time_out = 30;
    private CountDownTimer countDownTimer;
    private SmsReceiver smsReceiver;
    private SmsRetrieverClient client;
   // private CreditCardRepository creditCardRepository;

    @Override
    public int setLayout() {
        // TODO Auto-generated method stub
        return R.layout.activity_verification;
    }

    /**
     * <p>
     * this method is used for field declarations
     * <p>
     * this method is used to get otp numbers in separate edittext
     * and focus other edittext when current edditext gets input from user
     * </p>
     */

    @SuppressLint("NewApi")
    @Override
    public void Initialize() {
        // TODO Auto-generated method stub

        CommonData.mActivitylist.add(this);
        FontHelper.applyFont(this, findViewById(R.id.otproot_lay));
        TextInputLayout textInputLayoutPassWord = findViewById(R.id.pass_txt);
   //     creditCardRepository = CreditCardRepository.getRepository(this);
        back_click = findViewById(R.id.back_click);
        verifyno1Txt = findViewById(R.id.verifyno1Txt);
        verifyno2Txt = findViewById(R.id.verifyno2Txt);
        verifyno3Txt = findViewById(R.id.verifyno3Txt);
        verifyno4Txt = findViewById(R.id.verifyno4Txt);
        edt_number = findViewById(R.id.edt_num);
        submitOtp = findViewById(R.id.submit_otp);
        Phone_number_text = findViewById(R.id.txt_smspin);
        resendText = findViewById(R.id.resend_text);
        resend_text_new = findViewById(R.id.resend_text_new);
        countdown_secs = findViewById(R.id.secsText);
        timer_lays = findViewById(R.id.timer_lays);
        user_password = findViewById(R.id.edt_password);
        forget_password = findViewById(R.id.forget_password);
        RelativeLayout otp_verify_screen = findViewById(R.id.otp_verify_screen);
        RelativeLayout verify_password_lay = findViewById(R.id.verify_password_lay);
        otp_number = findViewById(R.id.otp_number);
        continue_password = findViewById(R.id.continue_phone);
        minstext = findViewById(R.id.minstext);
        minscolon = findViewById(R.id.colon_text);
        minstext.setTextColor(Color.GRAY);
        minscolon.setTextColor(Color.GRAY);

        resendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOtp();
            }
        });

        resend_text_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOtp();
            }
        });


        Bundle extras = getIntent().getExtras();
        try {
            if (extras != null) {
                String phone_exist = extras.getString("phone_exist");
                isShowOtpScreen = phone_exist.equals("0") || phone_exist.equals("2");
                phone_number = extras.getString("phone");
                country_code = extras.getString("country");
                SessionSave.saveSession("m_no", phone_number, VerificationActivity.this);
                SessionSave.saveSession("countrycode", country_code, VerificationActivity.this);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        textInputLayoutPassWord.setHint(NC.getString(R.string.enter_ur_password));
      // Phone_number_text.setText("" + NC.getResources().getString(R.string.enter_pin) + " " + country_code + " - " + phone_number);
       // Phone_number_text.setText("" + NC.getResources().getString(R.string.enter_pin) + " \n"+ phone_number + " - " +  country_code.replace("+","")+"+");


        if (SessionSave.getSession("Lang", VerificationActivity.this).equals("ar"))
            Phone_number_text.setText(country_code + " - " + phone_number);
        else
            Phone_number_text.setText( country_code + " - " + phone_number );


        if (isShowOtpScreen) {

//            if (SessionSave.getSession(CommonData.HIDE_OTP, VerificationActivity.this, false))
//                otp_number.setVisibility(View.GONE);
//            else
//                otp_number.setVisibility(View.VISIBLE);

            otp_number.setText("OTP: " + SessionSave.getSession("otp_number", VerificationActivity.this));
            otp_verify_screen.setVisibility(View.VISIBLE);
            verify_password_lay.setVisibility(View.GONE);
            StartResendTimer();

        } else {
            otp_number.setVisibility(View.GONE);
            otp_verify_screen.setVisibility(View.GONE);
            verify_password_lay.setVisibility(View.GONE);
        }
        verifyno1Txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (s.toString().trim().length() == 1) {
                    verifyno2Txt.requestFocus();
                    verifyno2Txt.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
        verifyno1Txt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                verifyno1Txt.setText("");
                return false;
            }
        });
        verifyno2Txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                Systems.out.println("TextChanged" + s + "__" + count);
                if (s.toString().trim().length() == 1) {
                    verifyno3Txt.requestFocus();
                    verifyno3Txt.setText("");
                } else if (count != 0) {
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
        verifyno2Txt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                verifyno2Txt.setText("");
                return false;
            }
        });
        verifyno3Txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (s.toString().trim().length() == 1) {
                    verifyno4Txt.requestFocus();
                    verifyno4Txt.setText("");
                } else if (count != 0) {
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        verifyno3Txt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                verifyno3Txt.setText("");
                return false;
            }
        });
        verifyno4Txt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                verifyno4Txt.setText("");
                return false;
            }
        });

        verifyno3Txt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int actionId, KeyEvent event) {
                Systems.out.println("backPressed3" + (actionId == KeyEvent.KEYCODE_DEL));

                if (actionId == KeyEvent.KEYCODE_DEL) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            verifyno2Txt.requestFocus();
                        }
                    }, 100);
                    //
                    // return true;
                }
                return false;
            }
        });
        verifyno2Txt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int actionId, KeyEvent event) {
                Systems.out.println("backPressed2" + (actionId == KeyEvent.KEYCODE_DEL));

                if (actionId == KeyEvent.KEYCODE_DEL) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            verifyno1Txt.requestFocus();
                        }
                    }, 100);
                }
                return false;
            }
        });
        verifyno4Txt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int actionId, KeyEvent event) {
                Systems.out.println("backPressed4" + (actionId == KeyEvent.KEYCODE_DEL));
                if (actionId == KeyEvent.KEYCODE_DEL) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            verifyno3Txt.requestFocus();
                        }
                    }, 100);
                }
                return false;
            }
        });


        verifyno4Txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (s.toString().trim().length() == 1) {
                    if (verifyno1Txt.getText().toString().length() == 1
                            && verifyno2Txt.getText().toString().length() == 1
                            && verifyno3Txt.getText().toString().length() == 1) {

                        verificationData();
                    } else if (count != 0) {
                        dialog = Utils.alert_view_dialog(VerificationActivity.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.verify_valid_code), NC.getResources().getString(R.string.ok), "", true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, "");
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
        user_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    continue_password.performClick();
                }
                return false;
            }
        });
        setonclickLisenter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
          //  window.setStatusBarColor(CL.getColor(this, R.color.button_accept));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (isShowOtpScreen) {
//            smsReceiver = new SmsReceiver();
//            client = SmsRetriever.getClient(this);
//            IntentFilter intentFilter = new IntentFilter();
//            intentFilter.addAction("com.google.android.gms.auth.api.phone.SMS_RETRIEVED");
//            registerReceiver(smsReceiver, intentFilter);
//            buildSmsRetrieverApiClient();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void buildSmsRetrieverApiClient() {
        if (client != null) {
            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(aVoid -> {
                if (smsReceiver != null)
                    smsReceiver.setCallback(message -> {
                        String code = parseCode(message);
                        if (code.length() == 4) {
                            verifyno1Txt.setText(String.valueOf(code.charAt(0)));
                            verifyno2Txt.setText(String.valueOf(code.charAt(1)));
                            verifyno3Txt.setText(String.valueOf(code.charAt(2)));
                            verifyno4Txt.setText(String.valueOf(code.charAt(3)));
                        }
                    });
            });

            task.addOnFailureListener(Throwable::printStackTrace);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isShowOtpScreen) {
            try {
                if (smsReceiver != null) {
                    unregisterReceiver(smsReceiver);
                    smsReceiver = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (dialog != null)
            Utils.closeDialog(dialog);
        super.onDestroy();
    }

    /**
     * Parse verification code
     *
     * @param message sms message
     * @return only four numbers from massage string
     */
    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    private void StartResendTimer() {
        resendText.setTextColor(Color.GRAY);
        countdown_secs.setTextColor(Color.GRAY);
        resendText.setClickable(false);
        resendText.setFocusable(false);
        resendText.setEnabled(false);
        timer_lays.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer((time_out) * 1000L, 1000) {


            @Override
            public void onTick(final long millisUntilFinished_) {
                long sec = millisUntilFinished_ / 1000;
                countdown_secs.setText(String.format(Locale.UK, "%1$02d", sec));
            }

            @Override
            public void onFinish() {
                try {
                    countDownTimer.cancel();
              //      resendText.setTextColor(CL.getColor(VerificationActivity.this, R.color.button_accept));
                    resendText.setEnabled(true);
                    resendText.setClickable(true);
                    resendText.setFocusable(true);
                    timer_lays.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void setonclickLisenter() {
        back_click.setOnClickListener(this);
        edt_number.setOnClickListener(this);
        forget_password.setOnClickListener(this);
        continue_password.setOnClickListener(this);
    //    resendText.setOnClickListener(this);
        submitOtp.setOnClickListener(this);

        verifyno1Txt.setOnLongClickListener(null);
        verifyno2Txt.setOnLongClickListener(null);
        verifyno3Txt.setOnLongClickListener(null);
        verifyno4Txt.setOnLongClickListener(null);

    }

    /**
     * this method is used to get the otp number in edittext and call the api
     */
    private void verificationData() {
        String verifyno1 = verifyno1Txt.getText().toString().trim();
        String verifyno2 = verifyno2Txt.getText().toString().trim();
        String verifyno3 = verifyno3Txt.getText().toString().trim();
        String verifyno4 = verifyno4Txt.getText().toString().trim();
        String otpCode = "";
        if (verifyno1.length() != 0 && verifyno2.length() != 0 && verifyno3.length() != 0 && verifyno4.length() != 0) {
            otpCode = verifyno1 + verifyno2 + verifyno3 + verifyno4;
            try {
                JSONObject j = new JSONObject();
                j.put("phone", phone_number);
                j.put("country_code", country_code);
                j.put("otp", otpCode);
                final String url = "type=driver_phoneotp_verify";
                new OtpVerification(url, j);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            dialog = Utils.alert_view_dialog(VerificationActivity.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.verify_valid_code), NC.getResources().getString(R.string.ok), "", true, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, "");
        }
    }

    /**
     * This method is used to call resend otp api
     */

    private void resendOtp() {
        try {
            JSONObject j = new JSONObject();
            j.put("phone", phone_number);
            j.put("country_code", SessionSave.getSession("country_code",VerificationActivity.this));
            j.put("device_type", "1");
            final String url = "type=driver_resend_phoneotp";
            new ResendOtp(url, j);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.back_click:
                onBackPressed();
                break;
//            case R.id.resend_text:
//                resendOtp();
//                break;
            case R.id.edt_num:
                finish();
                break;
            case R.id.forget_password:
                Forget_password();
                break;
            case R.id.continue_phone:
                Submitpassword();
                break;
            case R.id.submit_otp:
                verificationData();
                break;
            default:
                break;
        }
    }

    private void Submitpassword() {
        try {
            String password = "";
//            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(VerificationActivity.this);
            Bundle params = new Bundle();
            params.putString("user", phone_number);
//            params.putString(FirebaseAnalytics.Param.ITEM_NAME, phone_number);
            params.putString("type", "passenger");
//            mFirebaseAnalytics.logEvent("Login_clicked_by", params);
            Systems.out.println("analyticsLogTrigger");
            password = user_password.getText().toString().trim();
            if (validations(ValidateAction.isValueNULL, VerificationActivity.this, phone_number))
                if (validations(ValidateAction.isValidPassword, VerificationActivity.this, password)) {
                    continue_password.setEnabled(false);
                    JSONObject json = new JSONObject();
                    json.put("phone", phone_number);
                    json.put("country_code", country_code);
                    json.put("password", Uri.encode(password));
                    json.put("deviceid", SessionSave.getSession("sDevice_id", VerificationActivity.this));
                    String token = SessionSave.getSession(CommonData.DEVICE_TOKEN, VerificationActivity.this);

                    json.put("devicetoken", token == null ? SessionSave.getSession("sDevice_id", VerificationActivity.this) : token);
                    json.put("devicetype", "1");
                    new SignIn("type=passenger_login", json);
                }
        } catch (Exception e) {
            e.printStackTrace();
            ShowToast(VerificationActivity.this, "Invaid format");
        }
    }

    @Override
    public void onBackPressed() {
        if (isShowOtpScreen) {
            showAlertBack();
        } else {
            finish();
        }

    }

    private void showAlertBack() {
        dialog = Utils.alert_view_dialog(VerificationActivity.this, NC.getString(R.string.message), NC.getString(R.string.message_alert_back), NC.getString(R.string.ok), NC.getString(R.string.cancel), true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, "");

    }

    /**
     * @author developer
     * <p>
     * This section help to get the password.
     * </p>
     */
    protected void Forget_password() {
        try {
            final View view = View.inflate(VerificationActivity.this, R.layout.forgot_popupnew, null);
            mDialog = new Dialog(VerificationActivity.this, R.style.dialogwinddow);
            mDialog.setContentView(view);
            mDialog.setCancelable(true);
            if (!mDialog.isShowing())
                mDialog.show();
            FontHelper.applyFont(VerificationActivity.this, mDialog.findViewById(R.id.inner_content));
           // mDialog.findViewById(R.id.f_textview);
            final EditText mail = mDialog.findViewById(R.id.forgotmail);
            mail.setInputType(InputType.TYPE_CLASS_NUMBER);
            final Button OK = mDialog.findViewById(R.id.okbtn);
            final Button Cancel = mDialog.findViewById(R.id.cancelbtn);

            OK.setOnClickListener(new View.OnClickListener() {
                private String mobilenumber;

                @Override
                public void onClick(final View v) {
                    // TODO Auto-generated method stub
                    try {
                        mobilenumber = mail.getText().toString();
                        if (validations(ValidateAction.isValueNULL, VerificationActivity.this, mobilenumber)) {
                            JSONObject j = new JSONObject();
                            j.put("phone_no", mobilenumber);
                            j.put("user_type", "P");
                            j.put("country_code", "+91");
                            final String url = "type=forgot_password";
                            new ForgotPassword(url, j);
                            mail.setText("");
                            mDialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    mDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This class used to verify the otp
     * <p>
     * This class used to verify the otp
     * <p>
     *
     * @author developer
     */

    private class OtpVerification implements APIResult {
        private OtpVerification(final String url, JSONObject data) {
            new APIService_Retrofit_JSON(VerificationActivity.this, this, data, false).execute(url);
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            if (isSuccess) {
                try {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {

//                        if (json.getJSONObject("detail").has(CommonData.SKIP_PASSENGER_EMAIL))
//                            SessionSave.saveSession(CommonData.SKIP_PASSENGER_EMAIL, json.getJSONObject("detail").getString(CommonData.SKIP_PASSENGER_EMAIL).equals("1"), VerificationActivity.this);
//                        else
//                            SessionSave.saveSession(CommonData.SKIP_PASSENGER_EMAIL, false, VerificationActivity.this);

                        SessionSave.saveSession("CountyCode", country_code, VerificationActivity.this);
                        SessionSave.saveSession("reg_driver_Id", json.getJSONObject("detail").getString("driver_id"), VerificationActivity.this);
                        SessionSave.saveSession("Phone", SessionSave.getSession("m_no", VerificationActivity.this), VerificationActivity.this);
                        SessionSave.saveSession("m_no", "", VerificationActivity.this);
                        SessionSave.saveSession("Register", "2", VerificationActivity.this);
                        final Intent i = new Intent(VerificationActivity.this, DriverRegisterActStepOne.class);
                        startActivity(i);
                        finish();
                    } else if(json.getInt("status") == 2){

                        if (json.getJSONObject("detail").getString("signup_status").equalsIgnoreCase("4")) {
                            Intent intent = new Intent(VerificationActivity.this, DriverLoginActivity.class);
                            startActivity(intent);
                        }else{
                            SessionSave.saveSession("CountyCode", country_code, VerificationActivity.this);
                            SessionSave.saveSession("reg_driver_Id", json.getJSONObject("detail").getString("driver_id"), VerificationActivity.this);
                            SessionSave.saveSession("Phone", SessionSave.getSession("m_no", VerificationActivity.this), VerificationActivity.this);
                            SessionSave.saveSession("company_id",  json.getJSONObject("detail").getString("company_id"), VerificationActivity.this);

                            Intent intent = new Intent(VerificationActivity.this, AddFleetAct.class);
                            startActivity(intent);
                        }



                    }else
                        dialog = Utils.alert_view_dialog(VerificationActivity.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, "");
                } catch (final JSONException e) {
                    e.printStackTrace();
                }
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                       // ShowToast(VerificationActivity.this, NC.getString(R.string.server_error));
                    }
                });
            }
        }
    }

    /**
     * This class used to resend the otp
     * <p>
     * This class used to resend the otp
     * <p>
     *
     * @author developer
     */
    private class ResendOtp implements APIResult {
        private ResendOtp(final String url, JSONObject data) {
            new APIService_Retrofit_JSON(VerificationActivity.this, this, data, false).execute(url);
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            if (isSuccess) {
                try {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        StartResendTimer();
                        buildSmsRetrieverApiClient();
                        if (json.has("otp")) {
                            otp_number.setText("OTP: " + json.getString("otp"));
                        }
                        dialog = Utils.alert_view_dialog(VerificationActivity.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, "");
                    } else
                        dialog = Utils.alert_view_dialog(VerificationActivity.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, "");
                } catch (final JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                       // ShowToast(VerificationActivity.this, NC.getString(R.string.server_error));
                    }
                });
            }
        }
    }

    /**
     * This class used to remember password
     * <p>
     * This class used to remember password
     * </p>
     *
     * @author developer
     */
    private class ForgotPassword implements APIResult {
        protected ForgotPassword(final String url, final JSONObject data) {
            new APIService_Retrofit_JSON(VerificationActivity.this, this, data, false).execute(url);
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            // TODO Auto-generated method stub
            Systems.out.println("result_" + result);
            try {
                if (isSuccess) {
                    final JSONObject json = new JSONObject(result);
                    dialog = Utils.alert_view_dialog(VerificationActivity.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, "");
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                           // ShowToast(VerificationActivity.this, NC.getString(R.string.server_error));
                        }
                    });
                }
            } catch (final JSONException e) {
                e.printStackTrace();
            }
        }
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
        protected SignIn(final String url, JSONObject j) {
    //        new APIService_Retrofit_JSON(VerificationActivity.this, this, j, false, CommonData.API_BASE_URL + CommonData.COMPANY_KEY + "/?" + "lang=" + SessionSave.getSession(LANG, VerificationActivity.this) + "&" + url).execute();
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            // TODO Auto-generated method stub
            try {
                continue_password.setEnabled(true);
//                if (isSuccess) {
//                    final JSONObject json = new JSONObject(result);
//                    if (json.getInt("status") == 1) {
//                        SessionSave.saveSession("Email", json.getJSONObject("detail").getString("email"), VerificationActivity.this);
//                        SessionSave.saveSession(PASS_ID, json.getJSONObject("detail").getString("id"), VerificationActivity.this);
//                        SessionSave.saveSession("Tellfrdmsg", json.getJSONObject("detail").getString("telltofriend_message"), VerificationActivity.this);
//                        SessionSave.saveSession("Phone", json.getJSONObject("detail").getString("phone"), VerificationActivity.this);
//                        SessionSave.saveSession("ProfileImage", json.getJSONObject("detail").getString("profile_image"), VerificationActivity.this);
//                        SessionSave.saveSession(PASS_NAME, json.getJSONObject("detail").getString("name"), VerificationActivity.this);
//                        SessionSave.saveSession("About", json.getJSONObject("detail").getString("aboutpage_description"), VerificationActivity.this);
////                        SessionSave.saveSession("Currency", json.getJSONObject("detail").getString("site_currency") + " ", VerificationActivity.this);
//                        SessionSave.saveSession("RefCode", json.getJSONObject("detail").getString("referral_code"), VerificationActivity.this);
//                        SessionSave.saveSession("RefAmount", json.getJSONObject("detail").getString("referral_code_amount"), VerificationActivity.this);
//                        SessionSave.saveSession("Register", "", VerificationActivity.this);
//                        SessionSave.saveSession(CREDIT_CARD, "" + json.getJSONObject("detail").getString("credit_card_status"), VerificationActivity.this);
//                        SessionSave.saveSession("CountyCode", json.getJSONObject("detail").getString("country_code"), VerificationActivity.this);
//                        if (json.getJSONObject("detail").getString("split_fare").equals("1"))
//                            SessionSave.saveSession(CommonData.isSplitOn, true, VerificationActivity.this);
//                        else
//                            SessionSave.saveSession(CommonData.isSplitOn, false, VerificationActivity.this);
//                        if (json.getJSONObject("detail").getString("favourite_driver").equals("1"))
//                            SessionSave.saveSession(CommonData.isFavDriverOn, true, VerificationActivity.this);
//                        else
//                            SessionSave.saveSession(CommonData.isFavDriverOn, false, VerificationActivity.this);
//                        if (json.getJSONObject("detail").getString("skip_favourite").equals("1"))
//                            SessionSave.saveSession(CommonData.isSkipFavOn, true, VerificationActivity.this);
//                        else
//                            SessionSave.saveSession(CommonData.isSkipFavOn, false, VerificationActivity.this);
//
//                        Log.e("splitfare", json.getJSONObject("detail").getString("split_fare"));
//
//                        if (json.has("sos_detail")) {
//                            SessionSave.saveSession("contact_sos_list", json.getString("sos_detail"), VerificationActivity.this);
//                        }
//
//                        if (json.has(CommonData.USER_KEY)) {
//                            if (!json.getString(CommonData.USER_KEY).equals("") && json.getString(CommonData.USER_KEY) != null)
//                                SessionSave.saveSession(CommonData.USER_KEY, json.getString(CommonData.USER_KEY), VerificationActivity.this);
//                        }
//
//                        if (json.getJSONObject("detail").has(CommonData.CORPORATE_PASSENGER)) {
//                            SessionSave.saveSession(CommonData.CORPORATE_PASSENGER, json.getJSONObject("detail").getString(CommonData.CORPORATE_PASSENGER), VerificationActivity.this);
//                        }
//
//                        if (json.getJSONObject("detail").has(CommonData.CORPORATE_COMPANY_ID)) {
//                            SessionSave.saveSession(CommonData.CORPORATE_COMPANY_ID, json.getJSONObject("detail").getString(CommonData.CORPORATE_COMPANY_ID), VerificationActivity.this);
//                        }
//
//                        if (json.getJSONObject("detail").has(CommonData.CORPORATE_COMPANY_NAME)) {
//                            SessionSave.saveSession(CommonData.CORPORATE_COMPANY_NAME, json.getJSONObject("detail").getString(CommonData.CORPORATE_COMPANY_NAME), VerificationActivity.this);
//                        }
//
//                        if (json.getJSONObject("detail").has(CommonData.CORPORATE_COMPANY_BLOCK)) {
//                            SessionSave.saveSession(CommonData.CORPORATE_COMPANY_BLOCK, json.getJSONObject("detail").getString(CommonData.CORPORATE_COMPANY_BLOCK), VerificationActivity.this);
//                        }
//                        if (json.getJSONObject("detail").has("creditcard_details"))
//                            storeCardList(json.getJSONObject("detail").getJSONArray("creditcard_details"));
//
//                        if (json.getJSONObject("detail").has(CommonData.USER_WALLET_AMOUNT))
//                            SessionSave.saveWalletAmount((float) json.getJSONObject("detail").getDouble(CommonData.USER_WALLET_AMOUNT), VerificationActivity.this);
//                        else
//                            SessionSave.saveWalletAmount(0f, VerificationActivity.this);
//
//                        Intent intent = new Intent(getApplicationContext(), MainHomeFragmentActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                        finish();
//                    } else if (json.getInt("status") == -3) {
//
//
//                        SessionSave.saveSession("Email", json.getJSONObject("detail").getString("email"), VerificationActivity.this);
//                        SessionSave.saveSession(PASS_ID, json.getJSONObject("detail").getString("id"), VerificationActivity.this);
//                        SessionSave.saveSession("Tellfrdmsg", json.getJSONObject("detail").getString("telltofriend_message"), VerificationActivity.this);
//                        SessionSave.saveSession("Phone", json.getJSONObject("detail").getString("phone"), VerificationActivity.this);
//                        SessionSave.saveSession("ProfileImage", json.getJSONObject("detail").getString("profile_image"), VerificationActivity.this);
//                        SessionSave.saveSession(PASS_NAME, json.getJSONObject("detail").getString("name"), VerificationActivity.this);
//                        SessionSave.saveSession("About", json.getJSONObject("detail").getString("aboutpage_description"), VerificationActivity.this);
////                        SessionSave.saveSession("Currency", json.getJSONObject("detail").getString("site_currency") + " ", VerificationActivity.this);
//                        SessionSave.saveSession("RefCode", json.getJSONObject("detail").getString("referral_code"), VerificationActivity.this);
//                        SessionSave.saveSession("RefAmount", json.getJSONObject("detail").getString("referral_code_amount"), VerificationActivity.this);
//                        SessionSave.saveSession("Register", "", VerificationActivity.this);
//                        SessionSave.saveSession(CREDIT_CARD, "" + json.getJSONObject("detail").getString("credit_card_status"), VerificationActivity.this);
//                        SessionSave.saveSession("CountyCode", json.getJSONObject("detail").getString("country_code"), VerificationActivity.this);
//                        if (json.getJSONObject("detail").getString("split_fare").equals("1"))
//                            SessionSave.saveSession(CommonData.isSplitOn, true, VerificationActivity.this);
//                        else
//                            SessionSave.saveSession(CommonData.isSplitOn, false, VerificationActivity.this);
//                        if (json.getJSONObject("detail").getString("favourite_driver").equals("1"))
//                            SessionSave.saveSession(CommonData.isFavDriverOn, true, VerificationActivity.this);
//                        else
//                            SessionSave.saveSession(CommonData.isFavDriverOn, false, VerificationActivity.this);
//                        if (json.getJSONObject("detail").getString("skip_favourite").equals("1"))
//                            SessionSave.saveSession(CommonData.isSkipFavOn, true, VerificationActivity.this);
//                        else
//                            SessionSave.saveSession(CommonData.isSkipFavOn, false, VerificationActivity.this);
//
//                        if (json.has("sos_detail")) {
//                            SessionSave.saveSession("contact_sos_list", json.getString("sos_detail"), VerificationActivity.this);
//                        }
//
//
//                        final Intent i = new Intent(VerificationActivity.this, MainHomeFragmentActivity.class);
//                        i.putExtra("alert_message", json.getString("message"));
//                        if (json.getJSONObject("detail").has("SKIP_CREDIT_CARD") && json.getJSONObject("detail").getString("SKIP_CREDIT_CARD").equals("1"))
//                            SessionSave.saveSession("SKIP_CREDIT_CARD", true, VerificationActivity.this);
//                        else
//                            SessionSave.saveSession("SKIP_CREDIT_CARD", false, VerificationActivity.this);
//                        startActivity(i);
//                        finish();
//                    } else if (json.getInt("status") == -10) {
//                        ShowToast.center(VerificationActivity.this, json.getString("message"));
//
//                    } else if (json.getInt("status") == -2) {
//
//                        SessionSave.saveSession("Email", json.getJSONObject("detail").getString("email"), VerificationActivity.this);
//                        SessionSave.saveSession("Phone", json.getJSONObject("detail").getString("phone"), VerificationActivity.this);
//                        SessionSave.saveSession("Register", "1", VerificationActivity.this);
//                        SessionSave.saveSession("m_no", phone_number, VerificationActivity.this);
//                        final Intent i = new Intent(VerificationActivity.this, VerificationActivity.class);
//                        Bundle detail_fb = new Bundle();
//
//                        if (json.has("phone_exist"))
//                            detail_fb.putString("phone_exist", json.getString("phone_exist"));
//                        else detail_fb.putString("phone_exist", "0");
//
//                        detail_fb.putString("phone", phone_number);
//                        detail_fb.putString("country", country_code);
//                        i.putExtras(detail_fb);
//                        startActivity(i);
//                        finish();
//                    } else if (json.getInt("status") == -5) {
//                        dialog = Utils.alert_view_dialog(VerificationActivity.this, "" + NC.getResources().getString(R.string.message), "" + json.getString("message"), NC.getResources().getString(R.string.ok), "", true, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }, "");
//                        continue_password.setEnabled(true);
//                    } else if (json.getInt("status") == 4) {
//                        dialog = Utils.alert_view_dialog(VerificationActivity.this, "" + NC.getResources().getString(R.string.message), "" + json.getString("message"), NC.getResources().getString(R.string.ok), "", true, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }, "");
//                        continue_password.setEnabled(true);
//                    } else {
//                        dialog = Utils.alert_view_dialog(VerificationActivity.this, "" + NC.getResources().getString(R.string.message), "" + json.getString("message"), NC.getResources().getString(R.string.ok), "", true, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }, "");
//                        continue_password.setEnabled(true);
//                    }
//                } else {
//                    continue_password.setEnabled(true);
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            ShowToast(VerificationActivity.this, NC.getString(R.string.server_con_error));
//                        }
//                    });
 //               }
            } catch (final Exception e) {
                continue_password.setEnabled(true);
                runOnUiThread(new Runnable() {
                    public void run() {
                       // ShowToast(VerificationActivity.this, NC.getString(R.string.server_error));
                    }
                });
            }
        }
    }

    /**
     * Function to parse and store credit card details of passenger in local database
     *
     * @param jsonArray - Array of credit card details
     */
    private void storeCardList(JSONArray jsonArray) {
//        try {
//            if (jsonArray.length() > 0 && creditCardRepository != null) {
//                List<CreditCardData> cardDataList = new ArrayList<>();
//                creditCardRepository.deleteAllCards();
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    String id = jsonArray.getJSONObject(i).getString("passenger_cardid");
//                    String type = jsonArray.getJSONObject(i).getString("card_type");
//                    String month = jsonArray.getJSONObject(i).getString("expdatemonth");
//                    String year = jsonArray.getJSONObject(i).getString("expdateyear");
//                    String card = jsonArray.getJSONObject(i).getString("masked_creditcard_no");
//                    String cvv = jsonArray.getJSONObject(i).getString("masked_creditcard_cvv");
//                    String original_cardno = jsonArray.getJSONObject(i).getString("creditcard_no");
//                    String original_cvv = jsonArray.getJSONObject(i).getString("creditcard_cvv");
//                    String default_card = jsonArray.getJSONObject(i).getString("default_card");
//                    String name = jsonArray.getJSONObject(i).getString("card_holder_name");
//                    cardDataList.add(new CreditCardData(name, id, type, month, year, card, cvv, default_card, original_cardno, original_cvv));
//                }
//                creditCardRepository.insertAllCreditCards(cardDataList);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}