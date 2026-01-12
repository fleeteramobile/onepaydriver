package com.onepaytaxi.driver.Login

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.onepaytaxi.driver.AddFleetAct
import com.onepaytaxi.driver.MainActivity
import com.onepaytaxi.driver.OngoingAct
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.data.CommonData
import com.onepaytaxi.driver.databinding.ActivityDriverLoginBinding
import com.onepaytaxi.driver.driverregister.DriverRegisterActivity
import com.onepaytaxi.driver.homepage.HomePageActivity
import com.onepaytaxi.driver.interfaces.APIResult
import com.onepaytaxi.driver.interfaces.ClickInterface
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON
import com.onepaytaxi.driver.service.LocationUpdate
import com.onepaytaxi.driver.utils.CToast
import com.onepaytaxi.driver.utils.NC
import com.onepaytaxi.driver.utils.NetworkStatus
import com.onepaytaxi.driver.utils.SessionSave
import com.onepaytaxi.driver.utils.Utils
import org.json.JSONObject
import java.math.BigInteger
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

class DriverLoginActivity : AppCompatActivity(), ClickInterface {
    private lateinit var binding: ActivityDriverLoginBinding
    var FORCE_LOGIN: Boolean = false
     var alert_bundle = Bundle()
    private var alert_msg: String? = null
    private var dialog: Dialog? = null
    private var mobileNumber: String? = null
    private var password: String? = null
    private var jsonDriver: JSONObject? = null
    var mDialog: Dialog? = null
    var passwordmd =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDriverLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SessionSave.saveSession("Lang", "en", this@DriverLoginActivity)
        SessionSave.saveSession(
            "Lang_Country", "en_GB",
            this@DriverLoginActivity
        )
        Initialize()
        // Set up click listeners
        setupClickListeners()

    }

    private fun Initialize() {
        val alertMsg = intent.getStringExtra("alert_message")

        if (!alertMsg.isNullOrEmpty()) {
            dialog = Utils.alert_view(
                this@DriverLoginActivity,
                NC.getString(R.string.message),
                alertMsg,
                NC.getString(R.string.ok),
                "",
                true,
                this@DriverLoginActivity,
                ""
            )
        }



        CommonData.current_act = "SplashAct"
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@addOnCompleteListener
                }

                val token = task.result

                if (!token.isNullOrEmpty()) {
                    println("Token_new $token")

                    SessionSave.saveSession(
                        CommonData.DEVICE_TOKEN,
                        token,
                        this
                    )
                }
            }
        val c = AtomicInteger(0)
        val deviceId = SessionSave.getSession("sDevice_id", this)
            .takeIf { !it.isNullOrEmpty() }
            ?: (UUID.randomUUID().toString().ifEmpty {
                CommonData.mDevice_id_constant + c.incrementAndGet()
            }).also {
                SessionSave.saveSession("sDevice_id", it, this)
            }

        CommonData.mDevice_id = deviceId



    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            performLogin()
        }

        // Handle Forgot Password click
        binding.forgotPasswordLink.setOnClickListener {
            // Navigate to the Forgot Password screen
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
        }

        // Handle Sign Up click
        binding.signupTextLink.setOnClickListener {
            // Navigate to the Sign Up screen
            val signUpIntent = Intent(this, DriverRegisterActivity::class.java) // Assuming SignUpActivity exists
            startActivity(signUpIntent)
        }
    }

    private fun performLogin() {
         mobileNumber = binding.emailEditText.text.toString().trim()
         password = binding.passwordEditText.text.toString().trim()

        // Basic input validation
        if (mobileNumber!!.isEmpty()) {
            binding.emailInputLayout.error = "Mobile number is required"
            return
        } else {
            binding.emailInputLayout.error = null // Clear error
        }

        if (password!!.isEmpty()) {
            binding.passwordInputLayout.error = "Password is required"
            return
        } else {
            binding.passwordInputLayout.error = null // Clear error
        }
        SessionSave.saveSession("phone_number", mobileNumber, this@DriverLoginActivity)
        SessionSave.saveSession("phone_number", mobileNumber, this)
        SessionSave.saveSession(
            "driver_password",
            password,
            this
        )

         passwordmd = convertPassMd5(
            password!!.trim()
        )

        val url = "type=driver_login"
        SignIn(url, FORCE_LOGIN)

        //Login APi
    }

    inner class SignIn(url: String?, FORCE_LOGINS: Boolean) :
        APIResult {
        init {
            try {
                val j = JSONObject()
                j.put("phone", mobileNumber)
                j.put("password", passwordmd)
                j.put(
                    "device_id", SessionSave.getSession(
                        "sDevice_id",
                        this@DriverLoginActivity
                    )
                )
                j.put(
                    "device_token", SessionSave.getSession(
                        CommonData.DEVICE_TOKEN,
                        this@DriverLoginActivity
                    )
                )
                j.put("device_type", "1")
                j.put("force_login", FORCE_LOGINS)
                FORCE_LOGIN = false

                APIService_Retrofit_JSON(this@DriverLoginActivity, this, j, false).execute(url)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            val runnableServerError = Runnable {
                CToast.ShowToast(
                    this@DriverLoginActivity,
                    NC.getString(R.string.server_error)
                )
            }
            try {
                if (isSuccess) {
                    val json = JSONObject(result)
                    if (json.getInt("status") == 1 || json.getInt("status") == 10) {
                        val obj = json.getJSONObject("detail")
                        val ary = obj.getJSONArray("driver_details")
                        val detail = ary.getJSONObject(0)
                        SessionSave.saveSession(
                            "Email", detail.getString("email"),
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "Id", detail.getString("userid"),
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "Lastname", detail.getString("lastname"),
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "Name", detail.getString("name"),
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "u_name", detail.getString("name"),
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "Bankname", detail.getString("bankname"),
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "Bankaccount_No", detail.getString("bankaccount_no"),
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "Salutation", detail.getString("salutation"),
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "taxi_id", detail.getString("taxi_id"),
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "company_id", detail.getString("company_id"),
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "status", detail.getString("driver_status"),
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "Shiftupdate_Id", detail.getString("shiftupdate_id"),
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "country_code", detail.getString("country_code"),
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "model_id", detail.getString("model_id"),
                            this@DriverLoginActivity
                        )


                        if (detail.has("driver_type")) {
                            SessionSave.saveSession(
                                "account_message", json.getString("message"),
                                this@DriverLoginActivity
                            )
                            SessionSave.saveSession(
                                "driver_type", detail.getString("driver_type"),
                                this@DriverLoginActivity
                            )
                        } else {
                            SessionSave.saveSession(
                                "driver_type", "A",
                                this@DriverLoginActivity
                            )
                        }
                        if (detail.getString("shiftupdate_id") != "") SessionSave.saveSession(
                            "driver_shift", "IN",
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "Picture", detail.getString("profile_picture"),
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession("Register", "", this@DriverLoginActivity)
                        if (detail.getString("trip_id") != "") {
                            SessionSave.saveSession(
                                "trip_id", detail.getString("trip_id"),
                                this@DriverLoginActivity
                            )
                            MainActivity.mMyStatus.settripId(detail.getString("trip_id"))
                            SessionSave.saveSession(
                                "status", detail.getString("driver_status"),
                                this@DriverLoginActivity
                            )
                            SessionSave.saveSession(
                                "travel_status", detail.getString("travel_status"),
                                this@DriverLoginActivity
                            )
                        }

                        if (json.has("user_key")) {
                            val userKey = json.getString(CommonData.USER_KEY)
                            if (!TextUtils.isEmpty(userKey)) SessionSave.saveSession(
                                CommonData.USER_KEY, userKey,
                                this@DriverLoginActivity
                            )
                        }

                        if (json.has("sos_detail")) SessionSave.saveSession(
                            "contact_sos_list", json.getString("sos_detail"),
                            this@DriverLoginActivity
                        )
                        jsonDriver = detail.getJSONObject("driver_statistics")
                        SessionSave.saveSession(
                            "driver_statistics", jsonDriver.toString(),
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "Version_Update", "0",
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "shift_status",
                            detail.getJSONObject("driver_statistics").getString("shift_status"),
                            this@DriverLoginActivity
                        )


                        SessionSave.saveSession(
                            "taxi_no", detail.getString("taxi_no"),
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "model_name", detail.getString("model_name"),
                            this@DriverLoginActivity
                        )

                        val isFirst = detail.getString("driver_first_login")
                        if (isFirst == "1") {

                            pop_up(jsonDriver)
                        } else
                        {
                            pop_up(jsonDriver)
                        }
                    } else if (json.getInt("status") == 100) {
                        SessionSave.saveSession(
                            "reg_driver_Id", json.getString("driver_id"),
                            this@DriverLoginActivity
                        )
                        SessionSave.saveSession(
                            "company_id", json.getString("company_id"),
                            this@DriverLoginActivity
                        )
                        val intent = Intent(
                            this@DriverLoginActivity,
                            AddFleetAct::class.java
                        )
                        startActivity(intent)
                    } else if (json.getInt("status") == -5) CToast.ShowToast(
                        this@DriverLoginActivity, json.getString("message")
                    )
                    else if (json.getInt("status") == 0)
                    {
                        loggedInOtherDevice(json.getString("message"))
                    }
                    else{
                        showAlertView(json.getString("message"))
                    }

                    
                } else {
                    runOnUiThread(runnableServerError)

                }


            } catch (e: Exception) {
                e.printStackTrace()

                runOnUiThread(runnableServerError)
            }
        }
    }

    override fun positiveButtonClick(dialog: DialogInterface?, id: Int, s: String?) {

    }

    override fun negativeButtonClick(dialog: DialogInterface?, id: Int, s: String?) {

    }


    private fun showAlertView(message: String) {
        dialog = Utils.alert_view_dialog(
            this@DriverLoginActivity,
            "",
            message,
            NC.getString(R.string.ok),
            "",
            false,
            { dialog: DialogInterface, which: Int -> dialog.dismiss() }, null, ""
        )
    }

    fun convertPassMd5(pass: String): String {
        val mdEnc = MessageDigest.getInstance("MD5")
        val digest = mdEnc.digest(pass.toByteArray())

        return BigInteger(1, digest)
            .toString(16)
            .padStart(32, '0')
    }

    fun pop_up(jsonDriverObject: JSONObject?) {
        if (SessionSave.getSession("trip_id", this@DriverLoginActivity) != "") {
            if (SessionSave.getSession("travel_status", this@DriverLoginActivity) == "5") {
                SessionSave.saveSession("status", "A", this@DriverLoginActivity)
                val `in` = Intent(
                    this@DriverLoginActivity,
                    OngoingAct::class.java
                )
                startActivity(`in`)
                LocationUpdate.startLocationService(this@DriverLoginActivity)
                finish()
                if (mDialog != null) mDialog!!.dismiss()
            } else if (SessionSave.getSession("travel_status", this@DriverLoginActivity) == "2") {
                SessionSave.saveSession("status", "A", this@DriverLoginActivity)
                val `in` = Intent(
                    this@DriverLoginActivity,
                    OngoingAct::class.java
                )
                startActivity(`in`)
                LocationUpdate.startLocationService(this@DriverLoginActivity)
                finish()
                if (mDialog != null && this@DriverLoginActivity != null) mDialog!!.dismiss()
            } else {
                val i = Intent(
                    this@DriverLoginActivity,
                    HomePageActivity::class.java
                )
                SessionSave.saveSession("need_animation", true, this@DriverLoginActivity)
                SessionSave.saveSession(
                    CommonData.SHIFT_OUT, false,
                    this@DriverLoginActivity
                )
                SessionSave.saveSession(
                    CommonData.LOGOUT, false,
                    this@DriverLoginActivity
                )
                LocationUpdate.startLocationService(this@DriverLoginActivity)
                startActivity(i)
                finish()
                overridePendingTransition(0, 0)
                if (mDialog != null) mDialog!!.dismiss()
            }
        } else {
            val i = Intent(
                this@DriverLoginActivity,
                HomePageActivity::class.java
            )
            SessionSave.saveSession(
                CommonData.SHIFT_OUT, false,
                this@DriverLoginActivity
            )
            SessionSave.saveSession(
                CommonData.LOGOUT, false,
                this@DriverLoginActivity
            )
            SessionSave.saveSession("need_animation", true, this@DriverLoginActivity)
            LocationUpdate.startLocationService(this@DriverLoginActivity)
            startActivity(i)
            finish()
            overridePendingTransition(0, 0)
            if (mDialog != null) mDialog!!.dismiss()
        }
    }
    fun loggedInOtherDevice(msg: String?) {
        try {
            dialog = Utils.alert_view_dialog(
                this@DriverLoginActivity,
                NC.getString(R.string.message),
                msg,
                NC.getString(R.string.ok),
                NC.getString(R.string.cancell),
                false,
                { dialog: DialogInterface, which: Int ->
                    dialog.dismiss()
                    if (NetworkStatus.isOnline(this@DriverLoginActivity)) {
                        dialog.dismiss()
                        FORCE_LOGIN = true
                      performLogin()
                    } else {
                        CToast.ShowToast(
                            this@DriverLoginActivity,
                            NC.getString(R.string.check_net_connection)
                        )
                    }
                },
                { dialog: DialogInterface, which: Int -> dialog.dismiss() }, ""
            )
        } catch (e: java.lang.Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }
    }

}