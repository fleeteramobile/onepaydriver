package com.onepaytaxi.driver.driverregister

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.onepaytaxi.driver.Login.DriverLoginActivity
import com.onepaytaxi.driver.Login.LoginActivity
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.databinding.ActivityOtpVerificationBinding
import com.onepaytaxi.driver.interfaces.APIResult
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON
import com.onepaytaxi.driver.utils.CToast
import com.onepaytaxi.driver.utils.NC
import com.onepaytaxi.driver.utils.NetworkStatus
import com.onepaytaxi.driver.utils.SessionSave
import com.onepaytaxi.driver.utils.Utils
import org.json.JSONException
import org.json.JSONObject

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerificationBinding

    private lateinit var smsBroadcastReceiver: SmsBroadcastReceiver
    private val SMS_CONSENT_REQUEST = 200
    private var countDownTimer: CountDownTimer? = null

    private var phone_number: String? = null
    private var country_code: String? = null
    private var isShowOtpScreen = false
    private var dialog: Dialog? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startSmsUserConsent()
        readIntent()
        setupUI()
        startCountdown(60)
    }

    private fun readIntent() {
        intent.extras?.let {
            val phoneExist = it.getString("phone_exist")
            isShowOtpScreen = phoneExist == "0" || phoneExist == "2"
            phone_number = it.getString("phone")
            country_code = it.getString("country")

            SessionSave.saveSession("m_no", phone_number, this)
            SessionSave.saveSession("countrycode", country_code, this)
        }
    }

    private fun setupUI() {
        binding.tvSubTitle.text =
            "Enter 4 digit number that sent to\n$country_code $phone_number"

        binding.backClick.setOnClickListener { finish() }

        binding.btnContinue.setOnClickListener {
            otpVerify()
        }

        binding.tvResend.isEnabled = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.tvResend.setTextColor(getColor(android.R.color.darker_gray))
        }

        binding.tvResend.setOnClickListener {
            if (binding.tvResend.isEnabled) {
                resendOtp()
            }
        }


        if (isShowOtpScreen) {

            binding.otpNumber.setVisibility(View.VISIBLE)
            binding.otpNumber.setText(
                "OTP: " + SessionSave.getSession(
                    "otp_number",
                    this@OtpVerificationActivity
                )
            )

        } else {
            binding.otpNumber.setVisibility(View.GONE)
        }

        setupOtpInputs()

    }

    private fun setupOtpInputs() {

        binding.otp1.addTextChangedListener(OtpTextWatcher(binding.otp1))
        binding.otp2.addTextChangedListener(OtpTextWatcher(binding.otp2))
        binding.otp3.addTextChangedListener(OtpTextWatcher(binding.otp3))
        binding.otp4.addTextChangedListener(OtpTextWatcher(binding.otp4))
    }

    inner class OtpTextWatcher(private val view: View) : android.text.TextWatcher {

        override fun afterTextChanged(s: android.text.Editable?) {
            when (view.id) {
                R.id.otp1 -> if (s?.length == 1) binding.otp2.requestFocus()
                R.id.otp2 -> if (s?.length == 1) binding.otp3.requestFocus()
                R.id.otp3 -> if (s?.length == 1) binding.otp4.requestFocus()
                R.id.otp4 -> {
                    if (s?.length == 1) {
                        binding.otp4.clearFocus()
                        autoVerifyOtp()
                    }
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
    private fun autoVerifyOtp() {
        val otp = binding.otp1.text.toString() +
                binding.otp2.text.toString() +
                binding.otp3.text.toString() +
                binding.otp4.text.toString()

        if (otp.length == 4) {
            val j = JSONObject().apply {
                put("phone", phone_number)
                put("country_code", country_code)
                put("otp", otp)
            }
            OtpVerification("type=driver_phoneotp_verify", j)
        }
    }


    private fun otpVerify() {
        val otp = binding.otp1.text.toString() +
                binding.otp2.text.toString() +
                binding.otp3.text.toString() +
                binding.otp4.text.toString()

        if (otp.length == 4) {
            val j = JSONObject().apply {
                put("phone", phone_number)
                put("country_code", country_code)
                put("otp", otp)
            }
            OtpVerification("type=driver_phoneotp_verify", j)
        } else {
            dialog = Utils.alert_view_dialog(
                this,
                NC.getString(R.string.message),
                NC.getString(R.string.verify_valid_code),
                NC.getString(R.string.ok),
                "",
                true,
                { d, _ -> d.dismiss() },
                { d, _ -> d.dismiss() },
                ""
            )
        }
    }

    private fun fillOtp(otp: String) {
        if (otp.length >= 4) {
            binding.otp1.setText(otp[0].toString())
            binding.otp2.setText(otp[1].toString())
            binding.otp3.setText(otp[2].toString())
            binding.otp4.setText(otp[3].toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startCountdown(seconds: Int) {
        countDownTimer?.cancel()

        binding.tvResend.isEnabled = false
        binding.tvResend.setTextColor(getColor(android.R.color.darker_gray))

        countDownTimer = object : CountDownTimer(seconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val sec = (millisUntilFinished / 1000).toInt()
                binding.tvResend.text = "Re-send code in 0:${String.format("%02d", sec)}"
            }

            override fun onFinish() {
                binding.tvResend.text = "Re-send code"
                binding.tvResend.isEnabled = true
                binding.tvResend.setTextColor(getColor(R.color.app_theme_main))
            }
        }.start()
    }

    private fun resendOtp() {
        val j = JSONObject().apply {
            put("phone", phone_number)
            put("country_code", country_code)
            put("device_type", "1")
        }
        ResendOtpAPI("type=driver_resend_phoneotp", j)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startCountdown(30)
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun startSmsUserConsent() {
        val client = SmsRetriever.getClient(this)
        client.startSmsUserConsent(null)

        smsBroadcastReceiver = SmsBroadcastReceiver { intent ->
            startActivityForResult(intent, SMS_CONSENT_REQUEST)
        }

        val filter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                smsBroadcastReceiver,
                filter,
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(
                smsBroadcastReceiver,
                filter
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SMS_CONSENT_REQUEST && resultCode == Activity.RESULT_OK) {
            val message = data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE) ?: return
            Regex("(\\d{4,6})").find(message)?.value?.let {
                fillOtp(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()

        try {
            unregisterReceiver(smsBroadcastReceiver)
        } catch (e: IllegalArgumentException) {
            // receiver not registered – safe ignore
        }
    }


    inner class OtpVerification internal constructor(url: String?, data: JSONObject?) :
        APIResult {
        init {
            try {
                if (NetworkStatus.isOnline(this@OtpVerificationActivity)) {
                    APIService_Retrofit_JSON(this@OtpVerificationActivity, this, data, false).execute(url)
                } else {

                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            if (isSuccess) {
                try {
                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {


                        SessionSave.saveSession(
                            "CountyCode",
                            country_code,
                            this@OtpVerificationActivity
                        )
                        SessionSave.saveSession(
                            "reg_driver_Id",
                            json.getJSONObject("detail").getString("driver_id"),
                            this@OtpVerificationActivity
                        )
                        SessionSave.saveSession(
                            "Phone",
                            SessionSave.getSession("m_no", this@OtpVerificationActivity),
                            this@OtpVerificationActivity
                        )
                        SessionSave.saveSession("m_no", "", this@OtpVerificationActivity)
                        SessionSave.saveSession("Register", "2", this@OtpVerificationActivity)
                        val i = Intent(
                            this@OtpVerificationActivity,
                            DriverProfileRegisterActivity::class.java
                        )
                        startActivity(i)
                        finish()
                    } else if (json.getInt("status") == 2) {
                        if (json.getJSONObject("detail").getString("signup_status")
                                .equals("4", ignoreCase = true)
                        ) {
                            val intent = Intent(
                                this@OtpVerificationActivity,
                                DriverLoginActivity::class.java
                            )
                            startActivity(intent)
                        } else {
                            SessionSave.saveSession(
                                "CountyCode",
                                country_code,
                                this@OtpVerificationActivity
                            )
                            SessionSave.saveSession(
                                "reg_driver_Id",
                                json.getJSONObject("detail").getString("driver_id"),
                                this@OtpVerificationActivity
                            )
                            SessionSave.saveSession(
                                "Phone",
                                SessionSave.getSession("m_no", this@OtpVerificationActivity),
                                this@OtpVerificationActivity
                            )
                            SessionSave.saveSession(
                                "company_id",
                                json.getJSONObject("detail").getString("company_id"),
                                this@OtpVerificationActivity
                            )
                            val intent = Intent(
                                this@OtpVerificationActivity,
                                AddFleetActivity::class.java
                            )
                            startActivity(intent)
                        }
                    } else {
                        dialog = Utils.alert_view_dialog(this@OtpVerificationActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),
                            NC.getString(R.string.ok),
                            "",
                            true,
                            { dialog, which -> dialog.dismiss() },
                            { dialog, which -> dialog.dismiss() },
                            ""
                        )
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                // CToast.ShowToast(MyStatus.this, NC.getString(R.string.server_error));
            }
        }
    }


    inner class ResendOtpAPI internal constructor(url: String?, data: JSONObject?) :
        APIResult {
        init {
            try {
                if (NetworkStatus.isOnline(this@OtpVerificationActivity)) {
                    APIService_Retrofit_JSON(this@OtpVerificationActivity, this, data, false).execute(url)
                } else {

                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            if (isSuccess) {
                try {
                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {
                        CToast.ShowToast(this@OtpVerificationActivity, json.getString("message"))
                        startSmsUserConsent()
                        if (json.has("otp")) {
                            binding.otpNumber.text = "OTP: " + json.getString("otp")
                        }
                        dialog = Utils.alert_view_dialog(this@OtpVerificationActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),
                            NC.getString(R.string.ok),
                            "",
                            true,
                            { dialog, which -> dialog.dismiss() },
                            { dialog, which -> dialog.dismiss() },
                            ""
                        )
                    } else {
                        CToast.ShowToast(this@OtpVerificationActivity, json.getString("message"))
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                // CToast.ShowToast(MyStatus.this, NC.getString(R.string.server_error));
            }
        }
    }
}
