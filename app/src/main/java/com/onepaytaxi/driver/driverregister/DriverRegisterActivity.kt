package com.onepaytaxi.driver.driverregister



import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.onepaytaxi.driver.Login.DriverLoginActivity
import com.onepaytaxi.driver.Login.VerificationActivity
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.data.CommonData
import com.onepaytaxi.driver.databinding.ActivityDriverRegisterBinding
import com.onepaytaxi.driver.interfaces.APIResult
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON
import com.onepaytaxi.driver.utils.SessionSave
import org.json.JSONObject
import java.util.UUID

class DriverRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDriverRegisterBinding
    private var phone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {

        // Save device id
        if (CommonData.mDevice_id.isNotEmpty()) {
            SessionSave.saveSession("mDevice_id", CommonData.mDevice_id, this)
        }

        // Keyboard action
        binding.edtMobileno.setOnEditorActionListener { _, actionId, event ->
            if ((event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)
                || actionId == EditorInfo.IME_ACTION_DONE
            ) {
                binding.continuePhone.performClick()
            }
            false
        }

        // Show keyboard
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.edtMobileno, InputMethodManager.SHOW_IMPLICIT)

        // Mobile number validation
        binding.edtMobileno.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    val firstChar = s[0].toString()
                    if (firstChar in listOf("0", "2", "3", "4", "5")) {
                        binding.edtMobileno.setText("")
                    } else {
                        binding.edtMobileno.filters =
                            arrayOf(InputFilter.LengthFilter(16))
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Back click
        binding.backClick.setOnClickListener {
            onBackPressed()
        }

        // Continue click
        binding.continuePhone.setOnClickListener {
            continueWithMobile()
        }

        // Status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
    }

    private fun continueWithMobile() {
        try {
            phone = binding.edtMobileno.text.toString().trim()

            var uuid = ""

            if (CommonData.mDevice_id.isEmpty()) {
                uuid = UUID.randomUUID().toString()
                CommonData.mDevice_id = uuid
            }

            if (SessionSave.getSession("sDevice_id", this).isEmpty()) {
                uuid = UUID.randomUUID().toString()
                SessionSave.saveSession("sDevice_id", uuid, this)
            }

            SessionSave.saveSession("mDevice_id", CommonData.mDevice_id, this)

            if (phone.isNotEmpty()) {

                val json = JSONObject()
                json.put("phone", phone)
                json.put("country_code", SessionSave.getSession("country_code", this))
                json.put("device_id", SessionSave.getSession("sDevice_id", this))

                val token = SessionSave.getSession(CommonData.DEVICE_TOKEN, this)
                json.put(
                    "device_token",
                    if (token.isNullOrEmpty())
                        SessionSave.getSession("sDevice_id", this)
                    else token
                )

                json.put("device_type", "1")

                SignIn("type=driver_signupwith_phone", json)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            // Toast if you have utility, otherwise use Toast.makeText
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, DriverLoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
        super.onBackPressed()
    }

    // ================= API =================

    private inner class SignIn(url: String, json: JSONObject) : APIResult {

        init {
            APIService_Retrofit_JSON(
                this@DriverRegisterActivity,
                this,
                json,
                false
            ).execute(url)
        }

        override fun getResult(isSuccess: Boolean, result: String?) {
            try {
                binding.continuePhone.isEnabled = true

                if (isSuccess && result != null) {
                    val json = JSONObject(result)

                    if (json.getInt("status") == 1) {

                        SessionSave.saveSession("phone", phone, this@DriverRegisterActivity)

                        if (json.has("otp")) {
                            SessionSave.saveSession(
                                "otp_number",
                                json.getString("otp"),
                                this@DriverRegisterActivity
                            )
                        }

                        if (json.has("phone_exist")) {
                            if (json.getInt("phone_exist") != 3) {

                                val i = Intent(
                                    this@DriverRegisterActivity,
                                    OtpVerificationActivity::class.java
                                )
                                val bundle = Bundle()
                                bundle.putString("phone_exist", json.getString("phone_exist"))
                                bundle.putString(
                                    "phone",
                                    json.getJSONObject("detail").getString("phone")
                                )
                                bundle.putString(
                                    "country",
                                    json.getJSONObject("detail").getString("country_code")
                                )
                                i.putExtras(bundle)
                                startActivity(i)
                            }
                        }

                    } else {
                        alert_view(
                            this@DriverRegisterActivity,
                            "Message",
                            json.getString("message"),
                            "Ok",
                            ""
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }


    private var alertDialog: Dialog? = null

    fun alert_view(
        context: Context,
        title: String,
        message: String,
        successTxt: String,
        failureTxt: String
    ) {
        try {
            if (alertDialog?.isShowing == true) {
                alertDialog?.dismiss()
            }

            val view = View.inflate(context, R.layout.alert_view, null)

            alertDialog = Dialog(context, R.style.NewDialog).apply {
                setContentView(view)
                setCancelable(true)
            }


            val titleText = alertDialog!!.findViewById<TextView>(R.id.title_text)
            val messageText = alertDialog!!.findViewById<TextView>(R.id.message_text)
            val buttonSuccess = alertDialog!!.findViewById<Button>(R.id.button_success)
            val buttonFailure = alertDialog!!.findViewById<Button>(R.id.button_failure)

            buttonFailure.visibility = View.GONE

            titleText.text = title
            messageText.text = message
            buttonSuccess.text = successTxt

            buttonSuccess.setOnClickListener {
                alertDialog?.dismiss()
            }

            if (!(context as AppCompatActivity).isFinishing) {
                alertDialog?.show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
