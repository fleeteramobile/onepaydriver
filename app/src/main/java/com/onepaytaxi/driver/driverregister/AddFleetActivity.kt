package com.onepaytaxi.driver.driverregister

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.onepaytaxi.driver.Login.LoginActivity
import com.onepaytaxi.driver.R


import com.onepaytaxi.driver.data.ColorInfo
import com.onepaytaxi.driver.data.CommonData
import com.onepaytaxi.driver.data.VehicleModelInfo
import com.onepaytaxi.driver.data.apiData.ModelListInfo
import com.onepaytaxi.driver.data.apiData.StateInfo
import com.onepaytaxi.driver.data.apiData.VehicleListInfo
import com.onepaytaxi.driver.data.apiData.VehiclePrefixInfo
import com.onepaytaxi.driver.imageupload.RetrofitClient
import com.onepaytaxi.driver.imageupload.UploadResponse
import com.onepaytaxi.driver.interfaces.APIResult
import com.onepaytaxi.driver.interfaces.ClickInterface

import com.onepaytaxi.driver.service.APIService_Retrofit_JSON
import com.onepaytaxi.driver.utils.CToast

import com.onepaytaxi.driver.utils.NC
import com.onepaytaxi.driver.utils.NetworkStatus
import com.onepaytaxi.driver.utils.SessionSave
import com.onepaytaxi.driver.utils.Utils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class AddFleetActivity : AppCompatActivity(), ClickInterface {
    private var headTitle: TextView? = null
    private var btnSubmit: TextView? = null
    var mshowDialogs: Dialog? = null

    var mshowDialog: Dialog? = null
    private var leftIcon: ImageView? = null
    private var imgVPcoLicense: ImageView? = null
    private var imgVVehicleMot: ImageView? = null
    private var imgVVehicleInsurance: ImageView? = null
    private var imgVVehicleImage: ImageView? = null
    private var imgVLogbook: ImageView? = null
    private var imgVVehicleRegImage: ImageView? = null

    private var kmEdt: EditText? = null
    private var prefixPlateNumEdt: EditText? = null
    private var plateNumEdt: EditText? = null
    private var edtVehicleManufacturer: EditText? = null
    private var edtVehicleModel: EditText? = null
    private var edtServiceType: EditText? = null
    private var edtVehicleOwnerName: EditText? = null
    private var edtYearOfManufacturer: EditText? = null
    private var edtVechileInsurance: EditText? = null
    private var edtVechileInsuranceExpiry: EditText? = null
    private var edtVechileBody: EditText? = null
    private var edtDateOfRegistration: EditText? = null
    private var edtVechileKeeper: EditText? = null

    private var llVehiclePcoLicTxt: LinearLayout? = null
    private var llVehicleMotTxt: LinearLayout? = null
    private var llVehicleInsuranceTxt: LinearLayout? = null
    private var llVehicleImgTxt: LinearLayout? = null
    private var llVehicleLogTxt: LinearLayout? = null
    private var llVehicleRegImgTxt: LinearLayout? = null

    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var c: Calendar? = null

    private var cameraDialog: Dialog? = null
    private var dialog1: Dialog? = null

    private val MY_PERMISSIONS_REQUEST_CAMERA = 113
    private var imageUri: Uri? = null

    private var registerCheckBox: AppCompatCheckBox? = null

    private var destinationFileName = "Image11"
    private var imageType = 11

    private var stateAdapter: StateAdapter? = null
    private var vehicleAdapter: VehicleAdapter? = null
    private var modellistAdapter: ModellistAdapter? = null
    private var vehicleInfoListAdapter: VehicleInfoListAdapter? = null
    private var vehiclemodelAdapter: VehicleModelAdapter? = null

    private var stateInfos: ArrayList<StateInfo> = arrayListOf()
    private var vehiclePrefixInfos: ArrayList<VehiclePrefixInfo> = arrayListOf()
    private var modelListInfos: ArrayList<ModelListInfo> = arrayListOf()
    private var vehicleListInfos: ArrayList<VehicleListInfo> = arrayListOf()
    private var vehicleModlelInfos: ArrayList<VehicleModelInfo> = arrayListOf()

    private var vehicleImg = ""
    private val logbookImg = ""
    private var encodeImg = ""
    private var vehicleRegImg = ""
    private val response = ""
    private val driverId = ""
    private val companyId = ""
    private val validityDate = ""

    private var alertDialog: AlertDialog? = null

    private var stateId = ""
    private var stateName = ""
    private var platePrefixId: String? = null
    private var platePrefix = ""
    private var modelId: String? = null
    private var modelName = ""
    private var manufacturerId: String? = null
    private var manufacturerName: String? = null
    private val manufacturerModel = ""
    private var vehicleId = ""
    private var vehicleName = ""

    private val REQUEST_CODE = 99

    private var edtThreeDigit: EditText? = null
    private var edtTenDigit: EditText? = null
    private var edtFiveDigit: EditText? = null
    private var edtVechileReference: EditText? = null

    private val insuranceDate = ""
    private val selectedTime = ""
    private val alreadyExist = "0"

    private val numberOfColumns = 5

    private var idColor: RecyclerView? = null
    private var colorInfos: ArrayList<ColorInfo> = arrayListOf()
    private var colorAdapter: ColorAdapter? = null
     var colorId: String? = null
     var colorName = ""

    private var backBtn: TextView? = null
    private var backTripDetails: ImageView? = null

    private var loadingDialog: Dialog? = null
    private val REQUEST_GALLERY = 1001
    private val REQUEST_CAMERA = 1002
    private var image_type = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_fleet)
        kmEdt = findViewById(R.id.km_edt)
        prefixPlateNumEdt = findViewById(R.id.prefix_plate_num_edt)
        plateNumEdt = findViewById(R.id.plate_num_edt)
        edtVehicleManufacturer = findViewById(R.id.edt_vehicle_manufacturer)
        edtVehicleModel = findViewById(R.id.edt_vehicle_model)
        edtServiceType = findViewById(R.id.edt_service_type)
        edtVehicleOwnerName = findViewById(R.id.edt_vehicle_owner_name)
        edtYearOfManufacturer = findViewById(R.id.edt_year_of_manufacturer)
        llVehicleImgTxt = findViewById(R.id.ll_vehicle_img_txt)
        imgVVehicleImage = findViewById(R.id.imgV_vehicle_image)
        registerCheckBox = findViewById(R.id.register_CheckBox)
        btnSubmit = findViewById(R.id.btn_submit)
        backBtn = findViewById(R.id.back_btn)

        backTripDetails = findViewById(R.id.back_trip_details)
        llVehicleRegImgTxt = findViewById(R.id.ll_vehicle_reg_img_txt)
        imgVVehicleRegImage = findViewById(R.id.imgV_vehicle_reg_image)

        SessionSave.saveSession("model", "", this@AddFleetActivity)

        idColor = findViewById<RecyclerView>(R.id.id_color)
        idColor!!.setLayoutManager(GridLayoutManager(this, numberOfColumns))


        edtYearOfManufacturer!!.setOnClickListener(View.OnClickListener { pickyear() })

        llVehicleImgTxt!!.setOnClickListener(View.OnClickListener {
         
            image_type = 1
            showImagePickerDialog()
            
        })

      

        llVehicleRegImgTxt!!.setOnClickListener(View.OnClickListener {
           
            image_type = 2
            showImagePickerDialog()
           
        })



        backTripDetails!!.setOnClickListener(View.OnClickListener { onBackPressed() })

        kmEdt!!.setOnClickListener(View.OnClickListener { openDialog() })

        prefixPlateNumEdt!!.setOnClickListener(View.OnClickListener { openVehicleDialog() })

        edtServiceType!!.setOnClickListener(View.OnClickListener { openModellist() })
        edtVehicleManufacturer!!.setOnClickListener(View.OnClickListener {
            openVehicleInfoListDialog()
            // edt_vehicle_model.setText(NC.getResources().getString(R.string.vehicle_model));
        })

        edtVehicleModel!!.setOnClickListener(View.OnClickListener {
            showDialog()
            if (SessionSave.getSession("model", this@AddFleetActivity).equals("", ignoreCase = true)) {
                // CToast.ShowToast(AddFleetAct.this, "Kindly select vehicle manufacturer");
                closeDialog()
            } else {
                try {
                    val vehicle_model_Array =
                        JSONArray(SessionSave.getSession("model", this@AddFleetActivity))
                    vehicleModlelInfos = java.util.ArrayList()
                    if (vehicle_model_Array.length() > 0) {
                        for (i in 0 until vehicle_model_Array.length()) {
                            val vehicleModelInfo = VehicleModelInfo()
                            vehicleModelInfo._id =
                                vehicle_model_Array.getJSONObject(i).getString("_id")
                            vehicleModelInfo.name =
                                vehicle_model_Array.getJSONObject(i).getString("name")
                            vehicleModlelInfos.add(vehicleModelInfo)
                        }
                        openVehicleModelListDialog()
                    } else {
                        CToast.ShowToast(
                            this@AddFleetActivity,
                            "No vehicle models available for this manufacturer"
                        )
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })


        try {
            val vehicle_state_Array =
                JSONArray(SessionSave.getSession("vehicle_state_list", this@AddFleetActivity))
            stateInfos = java.util.ArrayList()
            for (i in 0 until vehicle_state_Array.length()) {
                val stateInfo = StateInfo()
                stateInfo.state_id = vehicle_state_Array.getJSONObject(i).getString("state_id")
                stateInfo.state_name = vehicle_state_Array.getJSONObject(i).getString("state_name")
                stateInfos.add(stateInfo)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        try {
            val vehicle_state_Array =
                JSONArray(SessionSave.getSession("vehicle_plate_prefix_list", this@AddFleetActivity))
            vehiclePrefixInfos = java.util.ArrayList()
            for (i in 0 until vehicle_state_Array.length()) {
                val vehiclePrefixInfo = VehiclePrefixInfo()
                vehiclePrefixInfo._id = vehicle_state_Array.getJSONObject(i).getString("_id")
                vehiclePrefixInfo.plate_prefix =
                    vehicle_state_Array.getJSONObject(i).getString("plate_prefix")
                vehiclePrefixInfos.add(vehiclePrefixInfo)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        try {
            val vehicle_state_Array =
                JSONArray(SessionSave.getSession("model_details", this@AddFleetActivity))

            println("vehicle_state_Array $vehicle_state_Array")

            modelListInfos = ArrayList()

            for (i in 0 until vehicle_state_Array.length()) {

                val jsonObj = vehicle_state_Array.getJSONObject(i)
                val modelName = jsonObj.getString("model_name")

                // ❌ Skip Rental & Outstation
                if (modelName.equals("Rental", ignoreCase = true) ||
                    modelName.equals("Outstaion", ignoreCase = true)
                ) {
                    continue
                }

                val modelListInfo = ModelListInfo()
                modelListInfo._id = jsonObj.getString("_id")
                modelListInfo.model_name = modelName

                modelListInfos.add(modelListInfo)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


        try {
            val vehicle_state_Array =
                JSONArray(SessionSave.getSession("vehicle_info_list", this@AddFleetActivity))
            vehicleListInfos = java.util.ArrayList()
            for (i in 0 until vehicle_state_Array.length()) {
                val vehicleListInfo = VehicleListInfo()
                vehicleListInfo._id = vehicle_state_Array.getJSONObject(i).getString("_id")
                vehicleListInfo.manufacturer_name =
                    vehicle_state_Array.getJSONObject(i).getString("manufacturer_name")
                vehicleListInfo.model = vehicle_state_Array.getJSONObject(i).getString("model")
                vehicleListInfos.add(vehicleListInfo)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        try {
            colorInfos = java.util.ArrayList()
            val color_Array =
                JSONArray(SessionSave.getSession("vehicle_color_list", this@AddFleetActivity))
            for (i in 0 until color_Array.length()) {
                val colorInfo = ColorInfo()
                colorInfo._id = color_Array.getJSONObject(i).getString("_id")
                colorInfo.color = color_Array.getJSONObject(i).getString("color")
                colorInfos.add(colorInfo)
            }
            colorAdapter = ColorAdapter(this@AddFleetActivity, colorInfos)
            idColor!!.setAdapter(colorAdapter)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }


        btnSubmit!!.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(kmEdt!!.getText().toString().trim { it <= ' ' })) {
                CToast.ShowToast(this@AddFleetActivity, NC.getString(R.string.select_state))
            } else if (TextUtils.isEmpty(
                    prefixPlateNumEdt!!.getText().toString().trim { it <= ' ' })
            ) {
                CToast.ShowToast(this@AddFleetActivity, NC.getString(R.string.Plate_prefix_num))
            } else if (TextUtils.isEmpty(plateNumEdt!!.getText().toString().trim { it <= ' ' })) {
                CToast.ShowToast(this@AddFleetActivity, NC.getString(R.string.Plate_number))
            } else if (TextUtils.isEmpty(
                    edtVehicleManufacturer!!.getText().toString().trim { it <= ' ' })
            ) {
                CToast.ShowToast(this@AddFleetActivity, NC.getString(R.string.vehicle_manufacturer))
            } else if (TextUtils.isEmpty(
                    edtVehicleModel!!.getText().toString().trim { it <= ' ' })
            ) {
                CToast.ShowToast(this@AddFleetActivity, NC.getString(R.string.vehicle_model_error))
            } else if (TextUtils.isEmpty(
                    edtServiceType!!.getText().toString().trim { it <= ' ' })
            ) {
                CToast.ShowToast(this@AddFleetActivity, NC.getString(R.string.service_type_error))
            } else if (TextUtils.isEmpty(
                    edtVehicleOwnerName!!.getText().toString().trim { it <= ' ' })
            ) {
                CToast.ShowToast(this@AddFleetActivity, NC.getString(R.string.owner_name_error))
            } else if (TextUtils.isEmpty(colorName.trim { it <= ' ' })) {
                CToast.ShowToast(this@AddFleetActivity, NC.getString(R.string.color_error))
            } else if (TextUtils.isEmpty(
                    edtYearOfManufacturer!!.getText().toString().trim { it <= ' ' })
            ) {
                CToast.ShowToast(
                    this@AddFleetActivity,
                    NC.getString(R.string.year_of_manufacturer_error)
                )
            } else if (TextUtils.isEmpty(vehicleImg.trim { it <= ' ' })) {
                CToast.ShowToast(this@AddFleetActivity, NC.getString(R.string.upload_vehicle_img))
            } else if (!registerCheckBox!!.isChecked()) {
                CToast.ShowToast(this@AddFleetActivity, "Please confirm the vehicle registration")
            } else {

                try {
                    val j = JSONObject().apply {
                        put("driver_id", SessionSave.getSession("reg_driver_Id", this@AddFleetActivity))
                        put("company_id", SessionSave.getSession("company_id", this@AddFleetActivity))

                        put("state_id", stateId)
                        put("plate_prefix_id", platePrefixId)
                        put("plate_number", plateNumEdt!!.text.toString().trim())
                        put("taxi_no", "$stateName$platePrefix/${plateNumEdt!!.text.toString().trim()}")
                        put("taxi_owner_name", edtVehicleOwnerName!!.text.toString().trim())
                        put("taxi_manufacturer_id", manufacturerId)
                        put("taxi_manufacturer", manufacturerName)
                        put("taxi_make_id", vehicleId)
                        put("taxi_make", vehicleName)

                        put("taxi_model", modelId)
                        put("taxi_manufacturing_year", edtYearOfManufacturer!!.text.toString().trim())
                        put("taxi_colour_id", colorId)
                        put("taxi_colour", colorName)
                        put("taxi_image", vehicleImg)
                        put("vehicle_registration_license", vehicleRegImg)
                    }
                    val url = "type=add_fleet"


                    AddFleet(url,j)

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        })

    }

    inner class AddFleet internal constructor(url: String?, data: JSONObject?) :
        APIResult {
        var msg = ""

        init {
            try {
                if (NetworkStatus.isOnline(this@AddFleetActivity)) {
                    APIService_Retrofit_JSON(
                        this@AddFleetActivity,
                        this,
                        data,
                        false
                    ).execute(url)
                } else {
                    Utils.alert_view(
                        this@AddFleetActivity,
                        NC.getString(R.string.message),
                        NC.getString(R.string.check_net_connection),
                        NC.getString(R.string.ok),
                        "",
                        true,
                        this@AddFleetActivity,
                        "4"
                    )
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            if (isSuccess) {
                try {
                    if (isSuccess) {
                        val json = JSONObject(result)
                        if (json.getInt("status") == 1) {
                            msg = json.getString("message")

                            //  success_dialog(json.getJSONObject("details").getString("skip_video"));

                            //SessionSave.saveSession("taxi_id", json.getString("taxi_id"), AddFleetAct.this);
                            CToast.ShowToast(this@AddFleetActivity, msg)
                            //SessionSave.saveSession(CommonData.DRIVER_RESULT, "", AddFleetAct.this);
                            val i = Intent(this@AddFleetActivity, LoginActivity::class.java)
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            //                        final Bundle bundle = new Bundle();
//                        bundle.putString("already_exist", json.getString("already_exist"));
//                        i.putExtras(bundle);
                            startActivity(i)
                        } else {
                            msg = json.getString("message")
                            dialog1 = Utils.alert_view(
                                this@AddFleetActivity,
                                NC.getString(R.string.message),
                                msg,
                                NC.getString(R.string.ok),
                                "",
                                true,
                                this@AddFleetActivity,
                                ""
                            )
                        }
                    } else {
                        runOnUiThread {
                            // CToast.ShowToast(AddFleetAct.this, NC.getString(R.string.server_error));
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                //CToast.ShowToast(MyStatus.this, NC.getString(R.string.server_error));
            }
        }
    }
    private fun openVehicleInfoListDialog() {
        if (alertDialog != null && alertDialog!!.isShowing) {
            alertDialog!!.dismiss()
        }
        val builder = AlertDialog.Builder(this@AddFleetActivity)
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.reason_dialog, viewGroup, false)
        builder.setView(dialogView)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(true)
        val title = dialogView.findViewById<TextView>(R.id.tv_title)
        title.text = NC.getString(R.string.select_manufacturer)
        val reason_recycle = dialogView.findViewById<RecyclerView>(R.id.reason_recycle)
        val layoutManagercancel: LinearLayoutManager
        layoutManagercancel = LinearLayoutManager(this@AddFleetActivity)
        reason_recycle.layoutManager = layoutManagercancel
        vehicleInfoListAdapter =
            VehicleInfoListAdapter(this@AddFleetActivity, vehicleListInfos)
        reason_recycle.adapter = vehicleInfoListAdapter
        SessionSave.saveSession("model", "", this@AddFleetActivity)
        edtVehicleModel!!.setText(NC.getString(R.string.vehicle_model))
        alertDialog!!.show()
    }

    private fun openModellist() {
        if (alertDialog != null && alertDialog!!.isShowing) {
            alertDialog!!.dismiss()
        }
        val builder = AlertDialog.Builder(this@AddFleetActivity)
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.reason_dialog, viewGroup, false)
        builder.setView(dialogView)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(true)
        val title = dialogView.findViewById<TextView>(R.id.tv_title)
        title.text = NC.getString(R.string.select_service_type)
        val reason_recycle = dialogView.findViewById<RecyclerView>(R.id.reason_recycle)
        val layoutManagercancel: LinearLayoutManager
        layoutManagercancel = LinearLayoutManager(this@AddFleetActivity)
        reason_recycle.layoutManager = layoutManagercancel
        modellistAdapter = ModellistAdapter(this@AddFleetActivity, modelListInfos)
        reason_recycle.adapter = modellistAdapter
        alertDialog!!.show()
    }

    private fun openDialog() {
        if (alertDialog != null && alertDialog!!.isShowing) {
            alertDialog!!.dismiss()
        }
        val builder = AlertDialog.Builder(this@AddFleetActivity)
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.reason_dialog, viewGroup, false)
        builder.setView(dialogView)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(true)
        val title = dialogView.findViewById<TextView>(R.id.tv_title)
        title.text = NC.getString(R.string.select_state_type)
        val reason_recycle = dialogView.findViewById<RecyclerView>(R.id.reason_recycle)
        val layoutManagercancel: LinearLayoutManager
        layoutManagercancel = LinearLayoutManager(this@AddFleetActivity)
        reason_recycle.layoutManager = layoutManagercancel
        stateAdapter = StateAdapter(this@AddFleetActivity, stateInfos)
        reason_recycle.adapter = stateAdapter
        alertDialog!!.show()
    }

    private fun openVehicleDialog() {
        if (alertDialog != null && alertDialog!!.isShowing) {
            alertDialog!!.dismiss()
        }
        val builder = AlertDialog.Builder(this@AddFleetActivity)
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.reason_dialog, viewGroup, false)
        builder.setView(dialogView)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(true)
        val title = dialogView.findViewById<TextView>(R.id.tv_title)
        title.text = NC.getString(R.string.selet_prefix_type)
        val reason_recycle = dialogView.findViewById<RecyclerView>(R.id.reason_recycle)
        val layoutManagercancel: LinearLayoutManager
        layoutManagercancel = LinearLayoutManager(this@AddFleetActivity)
        reason_recycle.layoutManager = layoutManagercancel
        vehicleAdapter = VehicleAdapter(this@AddFleetActivity, vehiclePrefixInfos)
        reason_recycle.adapter = vehicleAdapter
        alertDialog!!.show()
    }


    private fun openVehicleModelListDialog() {
        closeDialog()
        if (alertDialog != null && alertDialog!!.isShowing) {
            alertDialog!!.dismiss()
        }
        val builder = AlertDialog.Builder(this@AddFleetActivity)
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.reason_dialog, viewGroup, false)
        builder.setView(dialogView)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(true)
        val title = dialogView.findViewById<TextView>(R.id.tv_title)
        title.text = NC.getString(R.string.select_vehicle_model)
        val reason_recycle = dialogView.findViewById<RecyclerView>(R.id.reason_recycle)
        val layoutManagercancel: LinearLayoutManager
        layoutManagercancel = LinearLayoutManager(this@AddFleetActivity)
        reason_recycle.layoutManager = layoutManagercancel
        vehiclemodelAdapter = VehicleModelAdapter(this@AddFleetActivity, vehicleModlelInfos)
        reason_recycle.adapter = vehiclemodelAdapter
        alertDialog!!.show()
    }
    private fun pickyear() {
        if (alertDialog != null && alertDialog!!.isShowing) {
            alertDialog!!.dismiss()
        }
        val builder = android.app.AlertDialog.Builder(this@AddFleetActivity)
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.month_year_picker_dialog, viewGroup, false)
        builder.setView(dialogView)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(true)
        val cal = Calendar.getInstance()
        val yearPicker = dialogView.findViewById<NumberPicker>(R.id.picker_year)
        val okbtn = dialogView.findViewById<Button>(R.id.okbtn)
        val cancelbtn = dialogView.findViewById<Button>(R.id.cancelbtn)
        val year = cal[Calendar.YEAR]
        yearPicker.setMinValue(year - 16)
        yearPicker.setMaxValue(year)
        yearPicker.value = year
        alertDialog!!.show()
        okbtn.setOnClickListener {
            alertDialog!!.dismiss()
            edtYearOfManufacturer!!.setText(yearPicker.value.toString())
        }
        cancelbtn.setOnClickListener { alertDialog!!.dismiss() }
    }

    inner class ColorAdapter(var context: Context, private val colorData: List<ColorInfo>) :
        RecyclerView.Adapter<ColorAdapter.ViewHolder>() {
        var selectpos = -1
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(this@AddFleetActivity)
            var view: View? = null
            view = inflater.inflate(R.layout.color_list, parent, false)
            //Colorchange.ChangeColor((ViewGroup) view, AddFleetActStepOne.this);
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (selectpos == position) {
                holder.color_image_shadow.setVisibility(View.VISIBLE)
            } else {
                holder.color_image_shadow.setVisibility(View.GONE)
            }

//            holder.reasontxt.setOnClickListener(v -> {
//               // model_id = modelData.get(position).model_id;
//                //model_name = modelData.get(position).model_name;
//                notifyDataSetChanged();
//            });
            holder.lay_reason.setOnClickListener { v: View? ->
                //  model_id = modelData.get(position).model_id;
                // model_name = modelData.get(position).model_name;

                //  holder.color_image_shadow.setVisibility(View.VISIBLE);
                selectpos = position
                colorId = colorData[position]._id
                colorName = colorData[position].color
                notifyDataSetChanged()
            }
            holder.color_image.getDrawable().setColorFilter(
                Color.parseColor(colorData[position].color),
                PorterDuff.Mode.MULTIPLY
            )
        }

        override fun getItemCount(): Int {
            return colorData.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var color_image: ImageView
            var color_image_shadow: ImageView
            var lay_reason: RelativeLayout

            init {
                color_image = itemView.findViewById(R.id.color_image)
                color_image_shadow = itemView.findViewById(R.id.color_image_shadow)
                lay_reason = itemView.findViewById(R.id.lay_reason)
            }
        }
    }


   inner  class StateAdapter(var context: Context, private val stateData: List<StateInfo>) :
        RecyclerView.Adapter<StateAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(this@AddFleetActivity)
            var view: View? = null
            view = inflater.inflate(R.layout.reason_list, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.reasontxt.text = stateData[position].state_name
            holder.reasontxt.setOnClickListener { v: View? ->
                stateId = stateData[position].state_id
                stateName = stateData[position].state_name
                notifyDataSetChanged()
                canceldialog()
            }
            holder.lay_reason.setOnClickListener { v: View? ->
                stateId = stateData[position].state_id
                stateName = stateData[position].state_name
                notifyDataSetChanged()
                canceldialog()
            }
        }

        override fun getItemCount(): Int {
            return stateData.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var reasontxt: TextView
            var check1: AppCompatImageView
            var lay_reason: RelativeLayout

            init {
                reasontxt = itemView.findViewById(R.id.reason_txt)
                check1 = itemView.findViewById(R.id.check1)
                lay_reason = itemView.findViewById(R.id.lay_reason)
            }
        }
    }


    inner class VehicleAdapter(
        var context: Context,
        private val vehiclePrefixData: List<VehiclePrefixInfo>
    ) :
        RecyclerView.Adapter<VehicleAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(this@AddFleetActivity)
            var view: View? = null
            view = inflater.inflate(R.layout.reason_list, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.reasontxt.text = vehiclePrefixData[position].plate_prefix
            holder.reasontxt.setOnClickListener { v: View? ->
                platePrefixId = vehiclePrefixData[position]._id
                platePrefix = vehiclePrefixData[position].plate_prefix
                notifyDataSetChanged()
                cancelVehicle()
            }
            holder.lay_reason.setOnClickListener { v: View? ->
                platePrefixId = vehiclePrefixData[position]._id
                platePrefix = vehiclePrefixData[position].plate_prefix
                notifyDataSetChanged()
                cancelVehicle()
            }
        }

        override fun getItemCount(): Int {
            return vehiclePrefixData.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var reasontxt: TextView
            var check1: AppCompatImageView
            var lay_reason: RelativeLayout

            init {
                reasontxt = itemView.findViewById(R.id.reason_txt)
                check1 = itemView.findViewById(R.id.check1)
                lay_reason = itemView.findViewById(R.id.lay_reason)
            }
        }
    }


    inner class ModellistAdapter(
        var context: Context,
        private val modelListData: List<ModelListInfo>
    ) :
        RecyclerView.Adapter<ModellistAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(this@AddFleetActivity)
            var view: View? = null
            view = inflater.inflate(R.layout.reason_list, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.reasontxt.text = modelListData[position].model_name
            holder.reasontxt.setOnClickListener { v: View? ->
                modelId = modelListData[position]._id
                modelName = modelListData[position].model_name
                notifyDataSetChanged()
                cancelModelDialog()
            }
            holder.lay_reason.setOnClickListener { v: View? ->
                modelId = modelListData[position]._id
                modelName = modelListData[position].model_name
                notifyDataSetChanged()
                cancelModelDialog()
            }
        }

        override fun getItemCount(): Int {
            return modelListData.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var reasontxt: TextView
            var check1: AppCompatImageView
            var lay_reason: RelativeLayout

            init {
                reasontxt = itemView.findViewById(R.id.reason_txt)
                check1 = itemView.findViewById(R.id.check1)
                lay_reason = itemView.findViewById(R.id.lay_reason)
            }
        }
    }


    inner class VehicleInfoListAdapter(
        var context: Context,
        private val VehiclelistinfoData: List<VehicleListInfo>
    ) :
        RecyclerView.Adapter<VehicleInfoListAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(this@AddFleetActivity)
            var view: View? = null
            view = inflater.inflate(R.layout.reason_list, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.reasontxt.text = VehiclelistinfoData[position].manufacturer_name

//            SessionSave.saveSession("model", ""
//                    , AddFleetAct.this);
            holder.reasontxt.setOnClickListener { v: View? ->
                edtVehicleModel!!.setText(NC.getString(R.string.vehicle_model))
                SessionSave.saveSession(
                    "model", VehiclelistinfoData[position].model, this@AddFleetActivity
                )
                manufacturerId = VehiclelistinfoData[position]._id
                manufacturerName = VehiclelistinfoData[position].manufacturer_name
                notifyDataSetChanged()
                cancelVehicleListDialog()
            }
            holder.lay_reason.setOnClickListener { v: View? ->
                edtVehicleModel!!.setText(NC.getString(R.string.vehicle_model))
                SessionSave.saveSession(
                    "model", VehiclelistinfoData[position].model, this@AddFleetActivity
                )
                manufacturerId = VehiclelistinfoData[position]._id
                manufacturerName = VehiclelistinfoData[position].manufacturer_name
                notifyDataSetChanged()
                cancelVehicleListDialog()
            }
        }

        override fun getItemCount(): Int {
            return VehiclelistinfoData.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var reasontxt: TextView
            var check1: AppCompatImageView
            var lay_reason: RelativeLayout

            init {
                reasontxt = itemView.findViewById(R.id.reason_txt)
                check1 = itemView.findViewById(R.id.check1)
                lay_reason = itemView.findViewById(R.id.lay_reason)
            }
        }
    }


    inner class VehicleModelAdapter(
        var context: Context,
        private val VehicleModelinfoData: List<VehicleModelInfo>
    ) :
        RecyclerView.Adapter<VehicleModelAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(this@AddFleetActivity)
            var view: View? = null
            view = inflater.inflate(R.layout.reason_list, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.reasontxt.text = VehicleModelinfoData[position].name
            holder.reasontxt.setOnClickListener { v: View? ->
                vehicleId = VehicleModelinfoData[position]._id
                vehicleName = VehicleModelinfoData[position].name
                notifyDataSetChanged()
                cancelVehicleModelDialog()
            }
            holder.lay_reason.setOnClickListener { v: View? ->
                vehicleId = VehicleModelinfoData[position]._id
                vehicleName = VehicleModelinfoData[position].name
                notifyDataSetChanged()
                cancelVehicleModelDialog()
            }
        }

        override fun getItemCount(): Int {
            return VehicleModelinfoData.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var reasontxt: TextView
            var check1: AppCompatImageView
            var lay_reason: RelativeLayout

            init {
                reasontxt = itemView.findViewById(R.id.reason_txt)
                check1 = itemView.findViewById(R.id.check1)
                lay_reason = itemView.findViewById(R.id.lay_reason)
            }
        }
    }


    private fun cancelVehicleListDialog() {
        if (alertDialog != null) {
            alertDialog!!.dismiss()
        }
        edtVehicleManufacturer!!.setText(manufacturerName)
    }

    private fun cancelVehicleModelDialog() {
        if (alertDialog != null) {
            alertDialog!!.dismiss()
        }
        edtVehicleModel!!.setText(vehicleName)
    }

    private fun cancelModelDialog() {
        if (alertDialog != null) {
            alertDialog!!.dismiss()
        }
        edtServiceType!!.setText(modelName)
    }

    private fun cancelVehicle() {
        if (alertDialog != null) {
            alertDialog!!.dismiss()
        }
        prefixPlateNumEdt!!.setText(platePrefix)
    }

    private fun canceldialog() {
        if (alertDialog != null) {
            alertDialog!!.dismiss()
        }
        kmEdt!!.setText(stateName)
    }
    fun showDialog() {
        try {
            if (NetworkStatus.isOnline(this@AddFleetActivity)) {
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.dismiss()
                val view = View.inflate(this@AddFleetActivity, R.layout.progress_bar, null)
                loadingDialog = Dialog(this@AddFleetActivity, R.style.dialogwinddow)
                loadingDialog!!.setContentView(view)
                loadingDialog!!.setCancelable(false)
                if (this != null) loadingDialog!!.show()
                val iv = loadingDialog!!.findViewById<ImageView>(R.id.giff)
                val imageViewTarget = DrawableImageViewTarget(iv)
                Glide.with(this)
                    .load(R.raw.loading_anim)
                    .into(imageViewTarget)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //method to close dialog
    fun closeDialog() {
        try {
            if (loadingDialog != null) if (loadingDialog!!.isShowing) loadingDialog!!.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun positiveButtonClick(dialog: DialogInterface?, id: Int, s: String?) {
        dialog!!.dismiss()
    }

    override fun negativeButtonClick(dialog: DialogInterface?, id: Int, s: String?) {
        dialog!!.dismiss()
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Select from Gallery", "Capture from Camera")
        AlertDialog.Builder(this)
            .setTitle("Choose Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> pickFromGallery()
                    1 -> captureFromCamera()
                }
            }
            .show()
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    private fun captureFromCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        }
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_GALLERY -> {
                    val selectedImage = data?.data
                    selectedImage?.let {
                        val originalFile = File(getRealPathFromURI(it))
                        val compressedFile = compressImage(originalFile, 75) // Compress to 75% quality

                        when (image_type) {
                            1 -> imgVVehicleImage?.setImageURI(Uri.fromFile(compressedFile))
                            2 -> imgVVehicleRegImage?.setImageURI(Uri.fromFile(compressedFile))
                        }
showLoadings(this@AddFleetActivity)
                        uploadImage(
                            compressedFile,
                            SessionSave.getSession("reg_driver_Id", this),
                            image_type
                        )
                    }
                }

                REQUEST_CAMERA -> {
                    imageUri?.let { uri ->
                        val originalFile = File(getRealPathFromURI(uri))
                        val compressedFile = compressImage(originalFile, 75) // Compress to 75% quality

                        when (image_type) {
                            1 -> imgVVehicleImage?.setImageURI(Uri.fromFile(compressedFile))
                            2 -> imgVVehicleRegImage?.setImageURI(Uri.fromFile(compressedFile))
                        }

                        uploadImage(
                            compressedFile,
                            SessionSave.getSession("reg_driver_Id", this),
                            image_type
                        )
                    }
                }
            }
        }
    }

    /**
     * Compress image before upload
     * @param file Original image file
     * @param quality Compression quality (0–100)
     * @return Compressed image file
     */
    private fun compressImage(file: File, quality: Int = 75): File {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val compressedFile = File(cacheDir, "compressed_${System.currentTimeMillis()}.jpg")

        FileOutputStream(compressedFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }

        Log.d("UploadDebug", "Original Size: ${file.length() / 1024} KB")
        Log.d("UploadDebug", "Compressed Size: ${compressedFile.length() / 1024} KB")

        return compressedFile
    }
    private fun getRealPathFromURI(uri: Uri): String {
        var path = ""
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            path = it.getString(columnIndex)
            it.close()
        }
        return path
    }





    // Your existing uploadImage() method
    private fun uploadImage(file: File, driver_id: String, image_type: Int) {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body: MultipartBody.Part? = when (image_type) {
            1 -> MultipartBody.Part.createFormData("taxi_image", file.name, requestFile)
            2 -> MultipartBody.Part.createFormData("vehicle_registration_license", file.name, requestFile)

            else -> null
        }

        val fieldName = when (image_type) {
            1 -> "taxi_image"
            2 -> "vehicle_registration_license"
            else -> "unknown_field"
        }

        val userIdPart = driver_id.toRequestBody("text/plain".toMediaTypeOrNull())

        val lang = SessionSave.getSession("Lang", this)
        val iValue = SessionSave.getSession("reg_driver_Id", this).toIntOrNull() ?: 0
        val pvValue = 2 // hardcoded as per your example
        val kValue = SessionSave.getSession(CommonData.FIREBASE_KEY, this)


        Log.d("UploadDebug", "Field Name : $fieldName")

        Log.d("UploadDebug", "File Path: ${file.absolutePath}")
        Log.d("UploadDebug", "File Name: ${file.name}")
        Log.d("UploadDebug", "Image Type: $image_type")
        Log.d("UploadDebug", "Driver ID: $driver_id")

        // If you want to peek into request body length
        Log.d("UploadDebug", "File Size: ${file.length()} bytes")

        if (body != null) {
            RetrofitClient.api.uploadLicenseBackSideImage(
                license_back_side = body,
                userId = userIdPart,
                lang = lang,
                dt = "a",
                i = iValue.toString(),
                pv = pvValue.toString(),
                k = kValue
            ).enqueue(object : Callback<UploadResponse> {
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful) {
                       cancelLoadings()

                        when(image_type)
                        {
                            1-> vehicleImg = response.body()!!.url
                            2-> vehicleRegImg =  response.body()!!.url

                        }
                        Toast.makeText(this@AddFleetActivity, "Uploaded", Toast.LENGTH_SHORT).show()
                    } else {
                     cancelLoadings()
                        Toast.makeText(this@AddFleetActivity, "Upload Failed", Toast.LENGTH_SHORT).show()
                    }
                }


                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                   cancelLoadings()
                    println("error1_files  ${t.message} " )

                    Toast.makeText(this@AddFleetActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    fun showLoadings(context: Context) {
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
            if (mshowDialog != null) if (mshowDialog!!.isShowing && this@AddFleetActivity != null) mshowDialog!!.dismiss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }
    // Safe file path retriever
    @SuppressLint("Range")
    private fun getRealPathFromURI(context: Context, uri: Uri): String? {
        var filePath: String? = null

        if (DocumentsContract.isDocumentUri(context, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            when {
                uri.authority == "com.android.externalstorage.documents" -> {
                    val split = docId.split(":")
                    if (split[0].equals("primary", true)) {
                        filePath = Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }
                }
                uri.authority == "com.android.providers.downloads.documents" -> {
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        docId.toLong()
                    )
                    filePath = getDataColumn(context, contentUri, null, null)
                }
                uri.authority == "com.android.providers.media.documents" -> {
                    val split = docId.split(":")
                    val contentUri = when (split[0]) {
                        "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        else -> null
                    }
                    filePath = contentUri?.let { getDataColumn(context, it, "_id=?", arrayOf(split[1])) }
                }
            }
        } else if ("content".equals(uri.scheme, true)) {
            filePath = getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, true)) {
            filePath = uri.path
        }
        return filePath
    }

    @SuppressLint("Range")
    private fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(column))
            }
        } finally {
            cursor?.close()
        }
        return null
    }

}