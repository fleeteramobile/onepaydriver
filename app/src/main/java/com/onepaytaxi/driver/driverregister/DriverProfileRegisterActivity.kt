package com.onepaytaxi.driver.driverregister

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.data.CommonData
import com.onepaytaxi.driver.databinding.ActivityDriverProfileRegisterBinding
import com.onepaytaxi.driver.imageupload.RetrofitClient
import com.onepaytaxi.driver.imageupload.UploadResponse
import com.onepaytaxi.driver.interfaces.APIResult
import com.onepaytaxi.driver.interfaces.ClickInterface
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON
import com.onepaytaxi.driver.utils.NC
import com.onepaytaxi.driver.utils.NetworkStatus
import com.onepaytaxi.driver.utils.SessionSave
import com.onepaytaxi.driver.utils.Utils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class DriverProfileRegisterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    ClickInterface {
    private var dialog1:android.app.Dialog? = null
    // Form Data
    private var gender = "male"
    private var licenseImgUrl: String? = ""
    private var profileImgUrl: String? = ""
    private var licenseBackImgUrl: String? = ""
    private var imageType = 1 // 1: Front, 2: Back, 3: Profile
    private var driver_id = ""
    // Dialogs
    private var progressDialog: Dialog? = null
    private var imageUri: Uri? = null

    companion object {
        const val REQUEST_GALLERY = 1001
        const val REQUEST_CAMERA = 1002
    }

     lateinit var binding: ActivityDriverProfileRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverProfileRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

      //  setupGenderSpinner()
       setupClickListeners()
    }

//    private fun setupGenderSpinner() {
//        val adapter = ArrayAdapter.createFromResource(
//            this,
//            R.array.gender_array, android.R.layout.simple_spinner_item
//        )
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.edtGender.adapter = adapter
//        binding.edtGender.onItemSelectedListener = this
//    }

private fun setupClickListeners() {
        binding.apply {
            // Navigation
            backTripDetails.setOnClickListener { onBackPressed() }

            // Dates
            edtDriverLicenseExp.setOnClickListener { pickDate() }

            // Image Upload Areas
            llDriverLicTxt.setOnClickListener {
                imageType = 1
                showImagePickerDialog()
            }
            llDriverLicBackTxt.setOnClickListener {
                imageType = 2
                showImagePickerDialog()
            }
            llProfilePicTxt.setOnClickListener {
                imageType = 3
                showImagePickerDialog()
            }

            // Submit
            btnNxt.setOnClickListener { validateAndSubmit() }
        }
    }
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val selected = p0?.getItemAtPosition(p2).toString()
        gender = if (selected == "Select gender") "" else selected
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}


     fun validateAndSubmit() {

        binding.apply {
            val fName = edtFirstName.text.toString().trim()
            val lName = edtLastName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val pwd = "123456"
            val confirmPwd = "123456"
            val licId = edtDriverLicense.text.toString().trim()
            val licExp = edtDriverLicenseExp.text.toString().trim()

            when {
                fName.isEmpty() -> showToast("Enter First Name")
                gender.isEmpty() -> showToast("Select Gender")
                email.isEmpty() -> showToast("Enter Email")
//                pwd.length < 5 -> showToast("Password too short")
//                pwd != confirmPwd -> showToast("Passwords do not match")
//                licId.isEmpty() -> showToast("Enter License ID")
//                licExp.isEmpty() -> showToast("Select License Expiry")
//                licenseImgUrl.isNullOrEmpty() -> showToast("Upload License Front")
//                profileImgUrl.isNullOrEmpty() -> showToast("Upload Profile Photo")
                else -> performRegistration(fName, lName, email, pwd, licId, licExp)
            }
        }
    }

    private fun performRegistration(fName: String, lName: String, email: String, pwd: String, licId: String, licExp: String) {
        try {
            val data = JSONObject().apply {
                put("driver_id", SessionSave.getSession("reg_driver_Id", this@DriverProfileRegisterActivity))
                put("firstname", fName)
                put("lastname", lName)
                put("gender", "male")
                put("email", email)
                put("password", pwd)
                put("driver_license_id", licId)
                put("driver_license_expire_date", licExp)
                put("profile_picture", profileImgUrl)
                put("driver_licence", licenseImgUrl)
                put("license_back_side", licenseBackImgUrl)
                put("aadhar_number", "")
                put("dob", "")
                put("dob", "")
            }

            // Call your API helper
            RegistrationTask("type=add_driver", data)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    inner class RegistrationTask internal constructor(url: String?, data: JSONObject?) :
        APIResult {
        var msg = ""

        init {
            try {
                if (NetworkStatus.isOnline(this@DriverProfileRegisterActivity)) {
                    APIService_Retrofit_JSON(
                        this@DriverProfileRegisterActivity,
                        this,
                        data,
                        false
                    ).execute(url)
                } else {
                    Utils.alert_view(
                        this@DriverProfileRegisterActivity,
                        NC.getString(R.string.message),
                        NC.getString(R.string.check_net_connection),
                        NC.getString(R.string.ok),
                        "",
                        true,
                        this@DriverProfileRegisterActivity,
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
                            //CToast.ShowToast(DriverRegisterActStepOne.this, msg);
                            if (json.has("details") && json.getJSONObject("details")
                                    .has("driver_id")
                            ) {
                                driver_id = json.getJSONObject("details").getString("driver_id")
                                SessionSave.saveSession(
                                    "driver_name",
                                    binding.edtFirstName!!.getText().toString().trim { it <= ' ' },
                                    this@DriverProfileRegisterActivity
                                )
                                SessionSave.saveSession(
                                    "reg_driver_Id",
                                    driver_id,
                                    this@DriverProfileRegisterActivity
                                )
                                SessionSave.saveSession(
                                    CommonData.DRIVER_RESULT,
                                    result,
                                    this@DriverProfileRegisterActivity
                                )
                                SessionSave.saveSession(
                                    "company_id",
                                    json.getJSONObject("details").getString("company_id"),
                                    this@DriverProfileRegisterActivity
                                )
                                if (json.getJSONObject("details").has("model_details")) {
                                    SessionSave.saveSession(
                                        "model_details",
                                        json.getJSONObject("details").getString("model_details"),
                                        this@DriverProfileRegisterActivity
                                    )
                                }
//                                val intent = Intent(
//                                    this@DriverProfileRegisterActivity,
//                              //      AddFleetActivity::class.java
//                                )
//                                startActivity(intent)

                            }
                        } else {
                            msg = json.getString("message")
                            dialog1 = Utils.alert_view(
                                this@DriverProfileRegisterActivity,
                                NC.getString(R.string.message),
                                msg,
                                NC.getString(R.string.ok),
                                "",
                                true,
                                this@DriverProfileRegisterActivity,
                                ""
                            )
                        }
                    } else {
                        runOnUiThread {
                            // CToast.ShowToast(com.seero.driver.DriverRegisterActStepOne.this, NC.getString(R.string.server_error));
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
    private fun pickDate() {
        val c = Calendar.getInstance()
        val datePicker = DatePickerDialog(this, R.style.DatePickerTheme, { _, year, month, day ->
            val selectedDate = String.format("%02d-%02d-%d", day, month + 1, year)
            binding.edtDriverLicenseExp.setText(selectedDate)
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

        datePicker.datePicker.minDate = System.currentTimeMillis()
        datePicker.show()
    }

     fun showImagePickerDialog() {
        val options = arrayOf("Gallery", "Camera")
        AlertDialog.Builder(this@DriverProfileRegisterActivity)
            .setTitle("Select Image")
            .setItems(options) { _, which ->
                if (which == 0) pickFromGallery() else captureFromCamera()
            }.show()
    }

    // --- Image Handling Logic ---

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    private fun captureFromCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Picture")
        }
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        }
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val uri = if (requestCode == REQUEST_GALLERY) data?.data else imageUri
            uri?.let { processAndUpload(it) }
        }
    }

    private fun processAndUpload(uri: Uri) {
        val path = getRealPathFromURI(uri) ?: return
        val compressedFile = compressImage(File(path))

        // Show image in UI immediately
        when (imageType) {
            1 -> binding.imgVLicense.setImageURI(Uri.fromFile(compressedFile))
            2 -> binding.imgVLicenseBack.setImageURI(Uri.fromFile(compressedFile))
            3 -> binding.imgVProfile.setImageURI(Uri.fromFile(compressedFile))
        }

       uploadImageFile(compressedFile)
    }//

    private fun uploadImageFile(file: File) {
        showLoading()
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

        val body = when (imageType) {
            1 -> MultipartBody.Part.createFormData("driver_licence", file.name, requestFile)
            2 -> MultipartBody.Part.createFormData("license_back_side", file.name, requestFile)
            else -> MultipartBody.Part.createFormData("profile_picture", file.name, requestFile)
        }

        val driverId = SessionSave.getSession("reg_driver_Id", this).toRequestBody("text/plain".toMediaTypeOrNull())

        RetrofitClient.api.uploadLicenseBackSideImage(
            license_back_side = body,
            userId = driverId,
            lang = SessionSave.getSession("Lang", this),
            dt = "a",
            i = SessionSave.getSession("reg_driver_Id", this),
            pv = "2",
            k = SessionSave.getSession(CommonData.FIREBASE_KEY, this)
        ).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                hideLoading()
                if (response.isSuccessful) {
                    when (imageType) {
                        1 -> licenseImgUrl = response.body()?.url
                        2 -> licenseBackImgUrl = response.body()?.url
                        3 -> profileImgUrl = response.body()?.url
                    }
                    showToast("Uploaded Successfully")
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                hideLoading()
                showToast("Upload Failed: ${t.message}")
            }
        })
    }

    // --- Utility Methods ---

    private fun compressImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val compressedFile = File(cacheDir, "temp_img_${System.currentTimeMillis()}.jpg")
        FileOutputStream(compressedFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
        }
        return compressedFile
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(contentUri, proj, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(index)
        }
        return null
    }

    private fun showLoading() {
        progressDialog = Dialog(this, R.style.dialogwinddow).apply {
            setContentView(R.layout.progress_bar)
            setCancelable(false)
            val iv = findViewById<ImageView>(R.id.giff)
            Glide.with(this@DriverProfileRegisterActivity).load(R.raw.loading_anim).into(iv)
            show()
        }
    }

    private fun hideLoading() {
        progressDialog?.dismiss()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this@DriverProfileRegisterActivity, msg, Toast.LENGTH_SHORT).show()


    }

    override fun positiveButtonClick(
        dialog: DialogInterface?,
        id: Int,
        s: String?
    ) {

    }

    override fun negativeButtonClick(
        dialog: DialogInterface?,
        id: Int,
        s: String?
    ) {

    }


}