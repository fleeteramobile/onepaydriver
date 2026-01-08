package com.onepaytaxi.driver.homepage

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task
import com.onepaytaxi.driver.Login.DriverLoginActivity
import com.onepaytaxi.driver.MainActivity

import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.bookings.BookingsActivity
import com.onepaytaxi.driver.bookings.schdule.ScheduleBookingActivity
import com.onepaytaxi.driver.data.CommonData
import com.onepaytaxi.driver.databinding.ActivityHomePageBinding
import com.onepaytaxi.driver.driverprofile.DriverProfileActivity
import com.onepaytaxi.driver.earnings.EarningsActivity
import com.onepaytaxi.driver.interfaces.APIResult
import com.onepaytaxi.driver.interfaces.ClickInterface

import com.onepaytaxi.driver.service.APIService_Retrofit_JSON
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON_NoProgress
import com.onepaytaxi.driver.service.LocationUpdate
import com.onepaytaxi.driver.service.NonActivity
import com.onepaytaxi.driver.settlement.SettlementActivity
import com.onepaytaxi.driver.utils.NC
import com.onepaytaxi.driver.utils.NetworkStatus.isOnline
import com.onepaytaxi.driver.utils.SessionSave
import com.onepaytaxi.driver.utils.Systems
import com.onepaytaxi.driver.utils.Utils
import com.onepaytaxi.driver.wallet.DriverWalletRechargeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject


class  HomePageActivity : AppCompatActivity(), ClickInterface {
    private lateinit var binding: ActivityHomePageBinding
    var dialog1: Dialog? = null
    var apistatus = ""
    var mshowDialog: Dialog? = null
    var apimessage = ""
    var ignoreSwitchChanges = false
    private var isAvailable = false
    var nonactiityobj = NonActivity()
    var checked = "OUT"
    var recentListMessage = ""
//    private val resolutionForResult =
//        registerForActivityResult(
//            ActivityResultContracts.StartIntentSenderForResult()
//        ) {
//            println("Location_test"+ " "+"2")
//
//            // User returned from location settings dialog
//            //checkDeviceLocationSettings()
//        }
//    private fun checkDeviceLocationSettings() {
//        println("Location_test"+ " "+"1")
//        val locationRequest = LocationRequest.Builder(
//            Priority.PRIORITY_HIGH_ACCURACY, 10000
//        ).setMinUpdateIntervalMillis(5000).build()
//
//        val builder = LocationSettingsRequest.Builder()
//            .addLocationRequest(locationRequest)
//            .setAlwaysShow(true) // force dialog if needed
//
//        val client = LocationServices.getSettingsClient(this)
//        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
//
//        task.addOnSuccessListener {
//            println("location_enabled" + " " + "1")
//
//            Toast.makeText(this, "✅ Location services are enabled.", Toast.LENGTH_SHORT).show()
//        }
//
//        task.addOnFailureListener { exception ->
//            if (exception is ResolvableApiException) {
//                try {
//                    val intentSenderRequest =
//                        IntentSenderRequest.Builder(exception.resolution).build()
//                 //   resolutionForResult.launch(intentSenderRequest)
//                    println("location_enabled" + " " + "0")
//
//                } catch (sendEx: Exception) {
//                    sendEx.printStackTrace()
//                }
//            } else {
//                println("location_enabled" + " " + "0")
//                Toast.makeText(this, "❌ Location settings are inadequate.", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initClicks()
        if (SessionSave.getSession("driver_type", this@HomePageActivity)
                .equals("D", ignoreCase = true)
        ) {
            AccountNotActivated(SessionSave.getSession("account_message", this@HomePageActivity))
        } else {
            SessionSave.saveSession("account_activate", true, this@HomePageActivity)
            // no_taxi_view.setVisibility(View.GONE)
            val window = window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = getResources().getColor(R.color.hdr_txt_alt)
            }
            Systems.out.println("nan---nOTyET Activated")
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          //  showSelfieDialog()
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        showLoadings(this@HomePageActivity)
        fetchRecentTrips()
        binding.greeting.text = "Hi, ${SessionSave.getSession("Name",this@HomePageActivity)}"

//        Glide.with(this@HomePageActivity)
//            .load(SessionSave.getSession("Picture",this@HomePageActivity)) // Your drawable resource
//            .into(binding.driverProfileImage)

    }

    private fun initClicks() {

        binding.cardBookings.setOnClickListener {
            startActivity(Intent(this, BookingsActivity::class.java)) // example

        }
        binding.logoutBtn.setOnClickListener {
            logout(this@HomePageActivity)

        }

        binding.cardEarnings.setOnClickListener {

            startActivity(Intent(this, EarningsActivity::class.java)) // example

        }

        binding.cardHistory.setOnClickListener {
            startActivity(Intent(this, SettlementActivity::class.java)) // example

        }

        binding.cardAccounts.setOnClickListener {
            startActivity(Intent(this, DriverProfileActivity::class.java)) // example

        }
        binding.schduleTrip.setOnClickListener {
            startActivity(Intent(this, ScheduleBookingActivity::class.java)) // example

        }

        binding.cardWallet.setOnClickListener {
            startActivity(Intent(this, DriverWalletRechargeActivity::class.java)) // example

        }
        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.nav_home -> {
                    startActivity(Intent(this, HomePageActivity::class.java))
                    true
                }

                R.id.nav_trips -> {
                    startActivity(Intent(this, BookingsActivity::class.java))
                    true
                }

                R.id.nav_wallet -> {
                    startActivity(Intent(this, SettlementActivity::class.java))

                    true
                }

                R.id.nav_profile -> {
                    startActivity(Intent(this, DriverProfileActivity::class.java))
                    true
                }

                R.id.nav_help -> {
                    openDialer(
                        SessionSave.getSession("driver_phone_number", this@HomePageActivity)
                    )
                    true
                }

                else -> false
            }
        }




        binding.mytrip.setOnClickListener {

            if (ignoreSwitchChanges) return@setOnClickListener

            // 🔹 Get current shift from session
            val currentShift = SessionSave.getSession(
                "shift_status",
                this@HomePageActivity
            )

            // 🔁 Toggle shift
            checked = if (currentShift.equals("IN", ignoreCase = true)) "OUT" else "IN"

            // Update availability flag
            isAvailable = checked == "IN"

            // Save shift locally
            SessionSave.saveSession(
                "shift_status",
                checked,
                this@HomePageActivity
            )

            // Call API to update shift
            RequestingCheckBox()

            // Update UI
            updateToggleUI()
        }
    }

    fun logout(context: Context?) {
        dialog1 = Utils.alert_view(
            this@HomePageActivity,
            NC.getString(R.string.message),
            NC.getString(R.string.confirmlogout),
            NC.getString(R.string.m_logout),
            NC.getString(R.string.cancel),
            true,
            this@HomePageActivity,
            "5"
        )
    }

    private fun openDialer(phoneNumber: String) {
        // Create a URI with the "tel:" scheme and the phone number
        val dialIntentUri = Uri.parse("tel:$phoneNumber")

        // Create an Intent with ACTION_DIAL to open the dialer app
        val dialIntent = Intent(Intent.ACTION_DIAL, dialIntentUri)

        // Check if there's an app that can handle this Intent
        if (dialIntent.resolveActivity(packageManager) != null) {
            startActivity(dialIntent)
        } else {
            Toast.makeText(this, "No dialer app found to handle this request.", Toast.LENGTH_SHORT)
                .show()
        }
    }
    private fun updateToggleUI() {
        if (isAvailable) {
            binding.mytrip!!.text = "Online"
            binding.mytrip!!.backgroundTintList = ContextCompat.getColorStateList(this, R.color.shift_active_green)

            nonactiityobj.startServicefromNonActivity(this@HomePageActivity)

        } else {
            binding.mytrip!!.text = "Offline"
            binding.mytrip!!.backgroundTintList = ContextCompat.getColorStateList(this, R.color.shift_inactive_red)
        }
    }

//    private fun checkDeviceLocationSettingsnew(callback: (Boolean) -> Unit) {
//        val locationRequest = LocationRequest.Builder(
//            Priority.PRIORITY_HIGH_ACCURACY, 10000
//        ).build()
//
//        val builder = LocationSettingsRequest.Builder()
//            .addLocationRequest(locationRequest)
//            .setAlwaysShow(true) // shows dialog if OFF
//
//        val client = LocationServices.getSettingsClient(this)
//        val task = client.checkLocationSettings(builder.build())
//
//        task.addOnSuccessListener {
//            // ✅ Location is ON
//            callback(true)
//        }
//
//        task.addOnFailureListener { exception ->
//            if (exception is ResolvableApiException) {
//                try {
//                    val intentSenderRequest =
//                        IntentSenderRequest.Builder(exception.resolution).build()
//                    resolutionForResult.launch(intentSenderRequest)
//                } catch (sendEx: Exception) {
//                    sendEx.printStackTrace()
//                }
//            }
//            // ❌ Location is OFF
//            callback(false)
//        }
//    }
    private fun fetchRecentTrips() {
        CoroutineScope(Dispatchers.IO).launch {
            val startTime = System.currentTimeMillis()
            println("Coroutine started at: $startTime")

            delay(1000)
            println("After delay: ${System.currentTimeMillis() - startTime} ms elapsed")


            if (this@HomePageActivity!= null) {
                try {
                    val j = JSONObject().apply {
                        put("driver_id", SessionSave.getSession("Id", this@HomePageActivity))
                        put(
                            "driver_type",
                            SessionSave.getSession("driver_type", this@HomePageActivity)
                        )
                        put(
                            "device_token",
                            SessionSave.getSession(CommonData.DEVICE_TOKEN, this@HomePageActivity)
                        )
                        put(
                            "device_id",
                            SessionSave.getSession("sDevice_id", this@HomePageActivity)
                        )
                    }

                    val pro_url = "type=driver_recent_trip_list"
                    if (SessionSave.getSession("Id", this@HomePageActivity).isNotEmpty()) {
                        val beforeApi = System.currentTimeMillis()
                        GetTripData(pro_url, j)

                        println("After delay_API took: ${System.currentTimeMillis() - beforeApi} ms")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    override fun positiveButtonClick(dialog: DialogInterface?, id: Int, s: String?) {
        when (s) {


            "5" -> {
                dialog!!.dismiss()
                try {
                    val js = JSONObject()
                    js.put("driver_id", SessionSave.getSession("Id", this@HomePageActivity))
                    js.put(
                        "shiftupdate_id",
                        SessionSave.getSession("Shiftupdate_Id", this@HomePageActivity)
                    )
                    val urls = "type=user_logout"
                    Logout(urls, js)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }


        }
    }

    override fun negativeButtonClick(dialog: DialogInterface?, id: Int, s: String?) {

    }


    inner class GetTripData(url: String?, data: JSONObject?) : APIResult {
        init {
            try {
                if (isOnline(this@HomePageActivity)) {
                    APIService_Retrofit_JSON_NoProgress(
                        this@HomePageActivity,
                        this,
                        data,
                        false
                    ).execute(
                        url
                    )
                } else {
                    Log.d("No Internet Connection", "No Internet")
                }
            } catch (e: java.lang.Exception) {
                // TODO: handle exception
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String?) {
            try {
                if (isSuccess) {
                    cancelLoadings()
                    val json = JSONObject(result)




                    val message = json.getString("message")
                    val status = json.getInt("status")


                    apistatus = json.getInt("status").toString()
                    apimessage = json.getString("message")
                    val driverThresholdSetting = json.getInt("driver_threshold_setting")
                    val driverThresholdAmount = json.getDouble("driver_threshold_amount")
                    val totalDaily = json.getString("total_amount")
                    val totalTrips = json.getInt("total_trips")
                    val modelId = json.getInt("model_id")
                    val driverWallet =
                        json.getInt("driver_wallet")


                    SessionSave.saveSession("driver_wallet",driverWallet.toString(),this@HomePageActivity)

binding.totalTrips.setText(totalTrips.toString())
                    binding.walletBalance.setText(driverWallet.toString())
                    binding.totalEarningsTxt.setText(totalDaily.toString())
// Round to 2 decimals




//





                    if (json.getInt("status") == 1 || json.getInt("status") == -4 || json.getInt("status") == -2 || json.getInt(
                            "status"
                        ) == -3
                    ) {

                        if (json.getInt("status") == 1) {

                        }
                        if (json.getInt("status") == -4) {
                            dialog1 = Utils.alert_view(
                                this@HomePageActivity,
                                NC.getString(R.string.message),
                                json.getString("message"),
                                NC.getString(R.string.ok),
                                "",
                                true,
                                this@HomePageActivity,
                                "3"
                            )


                        } else if (json.getInt("status") == -2) {
                            dialog1 = Utils.alert_view(
                                this@HomePageActivity,
                                NC.getString(R.string.message),
                                json.getString("message"),
                                NC.getString(R.string.ok),
                                NC.getString(R.string.cancel),
                                false,
                                this@HomePageActivity,
                                "1"
                            )


                        } else if (json.getInt("status") == -3) {

                            checked = "OUT"
                            binding.mytrip!!.isChecked = false
                            RequestingCheckBox()
                            dialog1 = Utils.alert_view_dialog(
                                this@HomePageActivity,
                                NC.getString(R.string.message),
                                json.getString("message"),
                                NC.getString(R.string.ok),
                                NC.getString(R.string.cancel),
                                false,
                                { dialogInterface, i -> //                                    Utils.closeDialog();
                                    dialogInterface.dismiss()
//                                    val intent1 = Intent(
//                                        this@HomePageActivity,
//                                        WebviewAct::class.java
//                                    )
//                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                    intent1.putExtra("fromMyStatus", "YES")
//                                    intent1.putExtra("type", "1")
//                                    startActivity(intent1)
//                                    finish()
                                },
                                { dialogInterface, i -> dialogInterface.dismiss() },
                                ""
                            )
                        }

                    } else if (json.getInt("status") == 10) {
                        SessionSave.saveSession("driver_type", "D", this@HomePageActivity)
                        SessionSave.saveSession("account_activate", false, this@HomePageActivity)
                        AccountNotActivated(
                            SessionSave.getSession(
                                "account_message",
                                this@HomePageActivity
                            )
                        )
                    } else if (json.getInt("status") == -4) {
                       // isWalletBalaceislow = false

                        dialog1 = Utils.alert_view(
                            this@HomePageActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),
                            NC.getString(R.string.ok),
                            "",
                            true,
                            this@HomePageActivity,
                            "3"
                        )
                    } else if (json.getInt("status") == 40) {
                     //   isWalletBalaceislow = true

                        //  enableDrivertoActiveState()
                        dialog1 = Utils.alert_view(
                            this@HomePageActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),
                            NC.getString(R.string.ok),
                            "",
                            true,
                            this@HomePageActivity,
                            "3"
                        )

                    } else if (json.getInt("status") == 41) {
                      //  isWalletBalaceislow = true

                        dialog1 = Utils.alert_view(
                            this@HomePageActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),
                            NC.getString(R.string.ok),
                            "",
                            true,
                            this@HomePageActivity,
                            "3"
                        )
                        recentListMessage = json.getString("message")

                        val window: Window = getWindow()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                            window.statusBarColor = getResources().getColor(R.color.btn_accept_primary)
                        }
                        if (SessionSave.getSession(
                                "shift_status",
                                this@HomePageActivity
                            ) == "IN"
                        ) {
                            nonactiityobj.startServicefromNonActivity(this@HomePageActivity)
                        }


                    } else if (json.getInt("status") == -1) {


                        //   enableDrivertoActiveState()
                    } else {

                    }
                } else {
                    cancelLoadings()
                    runOnUiThread(Runnable {
                        //  CToast.ShowToast(MyStatus.this, NC.getString(R.string.server_error));
                    })
                }
            } catch (e: JSONException) {
                cancelLoadings()
                e.printStackTrace()
            }
        }
    }
    fun AccountNotActivated(Message: String?) {


        //   no_taxi_view.setVisibility(View.VISIBLE)
        val window = window
        //  no_taxi_assign.setText(Message)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getResources().getColor(R.color.btn_accept_primary)
        }
        val i = Intent(this@HomePageActivity, LocationUpdate::class.java)
        stopService(i)
    }
    inner class Logout(url: String?, data: JSONObject) : APIResult {
        init {

            if (isOnline(this@HomePageActivity)) {
                APIService_Retrofit_JSON(this@HomePageActivity, this, data, false).execute(url)
            } else {
                dialog1 = Utils.alert_view(
                    this@HomePageActivity,
                    NC.getString(R.string.message),
                    NC.getString(R.string.please_check_internet),
                    NC.getString(R.string.ok),
                    "",
                    true,
                    this@HomePageActivity,
                    "2"
                )
            }
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            if (isSuccess) {
                try {
                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {
                        val locationServiceIntent =
                            Intent(this@HomePageActivity, LocationUpdate::class.java)
                        stopService(locationServiceIntent)


                        MainActivity.clearsession(this@HomePageActivity)
                        //   dialog1 = Utils.alert_view(MainActivity.this, NC.getResources().getString(R.string.message),json.getString("message"), NC.getResources().getString(R.string.ok), "", true, MainActivity.this, "7");
                        val length = CommonData.mActivitylist.size
                        if (length != 0) {
                            for (jv in 0 until length) {
                                CommonData.mActivitylist[jv].finish()
                            }
                        }
                        val intent = Intent(
                            this@HomePageActivity,
                            DriverLoginActivity::class.java
                        )
                        startActivity(intent)
                        finish()
                        //                        dialog1 = Utils.alert_view_dialog(MainActivity.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", false, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        }, (dialog, which) -> dialog.dismiss(), "");
                    } else if (json.getInt("status") == -4) {
                        if (json.has("trip_id")) {
                            SessionSave.saveSession(
                                "trip_id",
                                json.getString("trip_id"),
                                this@HomePageActivity
                            )
                            dialog1 = Utils.alert_view(
                                this@HomePageActivity,
                                NC.getString(R.string.message),
                                json.getString("message"),
                                NC.getString(R.string.ok),
                                "",
                                true,
                                this@HomePageActivity,
                                "3"
                            )
                        }
                    } else {
                        dialog1 = Utils.alert_view(
                            this@HomePageActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),
                            NC.getString(R.string.ok),
                            "",
                            true,
                            this@HomePageActivity,
                            "2"
                        )
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                // runOnUiThread(() -> ShowToast(MainActivity.this, NC.getString(R.string.server_error)));
            }
        }
    }

    inner class RequestingCheckBox : APIResult {

        init {
            showLoadings(this@HomePageActivity)
            val j = JSONObject()
            j.put("driver_id", SessionSave.getSession("Id", this@HomePageActivity))
            j.put("shiftstatus", checked)
            j.put("reason", "")
            Log.e("shiftbefore ", j.toString())
            println("shiftbefore " + " " + j.toString())
            j.put("update_id", SessionSave.getSession("Shiftupdate_Id", this@HomePageActivity))
            val requestingCheckBox = "type=driver_shift_status"

            APIService_Retrofit_JSON(this@HomePageActivity, this, j, false).execute(
                requestingCheckBox
            )
        }

        @SuppressLint("MissingPermission")
        override fun getResult(isSuccess: Boolean, result: String?) {
            if (isSuccess) {
                cancelLoadings()
                val mJSONObject = JSONObject(result)
                Toast.makeText(
                    this@HomePageActivity,
                    mJSONObject.getString("message"),
                    Toast.LENGTH_LONG
                ).show()

                when (mJSONObject.getInt("status")) {
                    1 -> {
                        if (checked == "IN") {
                            isAvailable = true
                            binding.mytrip!!.isChecked = true
                            SessionSave.saveSession("shift_status", "IN", this@HomePageActivity)
                            SessionSave.saveSession(
                                CommonData.SHIFT_OUT,
                                false,
                                this@HomePageActivity
                            )
                            SessionSave.saveSession(
                                "Shiftupdate_Id",
                                mJSONObject.getJSONObject("detail").getString("update_id"),
                                this@HomePageActivity
                            )

                            // ✅ update UI
                            updateToggleUI()

                            // Start background services
                            if (!SessionSave.getSession("driver_type", this@HomePageActivity)
                                    .equals("D", ignoreCase = true)
                            ) {
                                nonactiityobj.startServicefromNonActivity(this@HomePageActivity)
                            }

//                            val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
//                            registerReceiver(screenOffReceiver, filter)
//                            val serviceIntent =
//                                Intent(this@HomePageActivity, PersistentService::class.java)
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                startForegroundService(serviceIntent)
//                            } else {
//                                startService(serviceIntent)
//                            }

                        } else {
                            isAvailable = false
                            binding.mytrip!!.isChecked = false
                            SessionSave.saveSession("shift_status", "OUT", this@HomePageActivity)
                            SessionSave.saveSession("trip_id", "", this@HomePageActivity)
                            SessionSave.setWaitingTime(0L, this@HomePageActivity)
                            nonactiityobj.stopServicefromNonActivity(this@HomePageActivity)
//                            if (screenOffReceiver != null) {
//                                unregisterReceiver(screenOffReceiver)
//                            }

                            // ✅ update UI
                            updateToggleUI()
                        }
                    }

                    -4 -> {
                        isAvailable = true
                        updateToggleUI()
                        dialog1 = Utils.alert_view(
                            this@HomePageActivity,
                            NC.getString(R.string.message),
                            mJSONObject.getString("message"),
                            NC.getString(R.string.ok),
                            "",
                            true,
                            this@HomePageActivity,
                            "6"
                        )
                    }

                    -3 -> {
                        // 🔴 Wallet Low — Force Offline
                        isAvailable = false
                        checked = "OUT"

                        binding.mytrip!!.isChecked = false
                        RequestingCheckBox()
                        updateToggleUI()

                        dialog1 = Utils.alert_view_dialog(
                            this@HomePageActivity,
                            NC.getString(R.string.message),
                            mJSONObject.getString("message"),
                            NC.getString(R.string.ok),
                            NC.getString(R.string.cancel),
                            false,
                            { dialogInterface, _ -> dialogInterface.dismiss() },
                            { dialogInterface, _ -> dialogInterface.dismiss() },
                            ""
                        )
                    }

                    else -> {
                        dialog1 = Utils.alert_view(
                            this@HomePageActivity,
                            NC.getString(R.string.message),
                            mJSONObject.getString("message"),
                            NC.getString(R.string.ok),
                            "",
                            true,
                            this@HomePageActivity,
                            "6"
                        )
                    }
                }
            }
            else{
                cancelLoadings()
            }
        }


    }

    override fun onResume() {
        super.onResume()
        val needAnimation = SessionSave.getSession("need_animation", this, false)

        if (needAnimation) {
            SessionSave.saveSession("need_animation", false, this@HomePageActivity)
            checked = "OUT"
            RequestingCheckBox() // call API only for animation logic
        }

        val driverShift = SessionSave.getSession(
            "shift_status",
            this@HomePageActivity
        )

        if (driverShift.equals("IN", ignoreCase = true)) {
            // Driver is ONLINE
            isAvailable = true
            updateToggleUI()
        } else {
            // Driver is OFFLINE
            isAvailable = false
            updateToggleUI()
        }
        updateUI()

    }


    fun showLoadings(context: Context) {
        println("trip_loading"+" "+"iuhiuhiuhjkjn")
        try {
            if (mshowDialog != null) if (mshowDialog!!.isShowing) mshowDialog!!.dismiss()
            val view = View.inflate(context, R.layout.progress_bar, null)
            mshowDialog = Dialog(context, R.style.dialogwinddow)
            mshowDialog!!.setContentView(view)
            mshowDialog!!.setCancelable(false)
            mshowDialog!!.show()
            val iv = mshowDialog!!.findViewById<ImageView>(R.id.giff)
            val imageViewTarget = DrawableImageViewTarget(iv)
            Glide.with(context)
                .load(R.raw.loading_anim)
                .into<DrawableImageViewTarget>(imageViewTarget)
        } catch (e: Exception) {
            // TODO: handle exception
        }
    }




    private fun cancelLoadings() {

        try {
            if (mshowDialog != null) if (mshowDialog!!.isShowing && this@HomePageActivity != null) mshowDialog!!.dismiss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }
}