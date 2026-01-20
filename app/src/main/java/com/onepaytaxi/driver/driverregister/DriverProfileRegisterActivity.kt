package com.onepaytaxi.driver.driverregister

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import java.util.Calendar

class DriverProfileRegisterActivity : AppCompatActivity(),
    ClickInterface {

    lateinit var binding: ActivityDriverProfileRegisterBinding

    private var gender = "male"
    private var imageType = 1 // 1-License Front, 2-License Back, 3-Profile
    private var driver_id = ""
    private var licenseImgUrl: String? = ""
    private var licenseBackImgUrl: String? = ""
    private var profileImgUrl: String? = ""
    private var driver_aadhar_back_side: String? = ""
    private var driver_aadhar: String? = ""

    private var progressDialog: Dialog? = null
    private lateinit var cameraImageUri: Uri
    private var dialog1: android.app.Dialog? = null
    // -------------------- Activity Result APIs --------------------

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { handleSelectedImage(it) }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                handleSelectedImage(cameraImageUri)
            }
        }

    // -------------------- onCreate --------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverProfileRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClicks()
    }

    // -------------------- Clicks --------------------

    private fun setupClicks() {
        binding.apply {

            backTripDetails.setOnClickListener { onBackPressed() }

            binding.edtDob.setOnClickListener {
                pickDate(binding.edtDob)
            }

            binding.edtDriverLicenseExp.setOnClickListener {
                pickDate(binding.edtDriverLicenseExp)
            }


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
            llDriverAadharPicTxt.setOnClickListener {
                imageType = 4
                showImagePickerDialog()
            }
            llDriverAadharBackSidePicTxt.setOnClickListener {
                imageType = 5
                showImagePickerDialog()
            }

            btnNxt.setOnClickListener { validateAndSubmit() }
        }
    }

    // -------------------- Validation --------------------

    private fun validateAndSubmit() {
        binding.apply {
            val fName = edtFirstName.text.toString().trim()
            val lName = edtLastName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val licId = edtDriverLicense.text.toString().trim()
            val licExp = edtDriverLicenseExp.text.toString().trim()

            when {
                fName.isEmpty() -> showToast("Enter First Name")
                email.isEmpty() -> showToast("Enter Email")
                else -> registerDriver(fName, lName, email, licId, licExp)
            }
        }
    }

    // -------------------- API Registration --------------------

    private fun registerDriver(
        fName: String,
        lName: String,
        email: String,
        licId: String,
        licExp: String
    ) {
        val data = JSONObject().apply {
            put(
                "driver_id",
                SessionSave.getSession("reg_driver_Id", this@DriverProfileRegisterActivity)
            )
            put("firstname", fName)
            put("lastname", lName)
            put("gender", gender)
            put("email", email)
            put("password", "123456")
            put("repassword", "123456")
            put("driver_dob", binding.edtDob.text.toString().trim())
            put("driver_address", binding.edtAddress.text.toString().trim())
            put("national_id", binding.edtDriverAadhar.text.toString().trim())
            put("driver_license_id", licId)
            put("driver_license_expire_date", licExp)
            put("profile_picture", profileImgUrl)
            put("driver_licence", licenseImgUrl)
            put("license_back_side", licenseBackImgUrl)
            put("driver_aadhar", driver_aadhar)
            put("driver_aadhar_back_side", driver_aadhar_back_side)
        }

        RegistrationTask("type=add_driver", data)
    }

    inner class RegistrationTask(url: String?, data: JSONObject?) : APIResult {

        init {
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
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            var msg = ""

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
                                val intent = Intent(
                                    this@DriverProfileRegisterActivity,
                                    AddFleetActivity::class.java
                                )
                                startActivity(intent)

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

    // -------------------- Date Picker --------------------

    private fun pickDate(targetView: EditText) {
        val cal = Calendar.getInstance()

        DatePickerDialog(
            this,
            R.style.DatePickerTheme,
            { _, year, month, dayOfMonth ->
                val date = String.format(
                    "%02d-%02d-%04d",
                    dayOfMonth,
                    month + 1,
                    year
                )
                targetView.setText(date)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }


    // -------------------- Image Picker --------------------

    private fun showImagePickerDialog() {
        AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setItems(arrayOf("Gallery", "Camera")) { _, which ->
                if (which == 0) openGallery() else openCamera()
            }.show()
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun openCamera() {
        cameraImageUri = createImageUri()
        cameraLauncher.launch(cameraImageUri)
    }

    private fun createImageUri(): Uri {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )!!
    }

    private fun handleSelectedImage(uri: Uri) {

        when (imageType) {
            1 -> binding.imgVLicense.setImageURI(uri)
            2 -> binding.imgVLicenseBack.setImageURI(uri)
            3 -> binding.imgVProfile.setImageURI(uri)
            4 -> binding.imgVDriverAadhar.setImageURI(uri)
            5 -> binding.imgVDriverAadharBackSide.setImageURI(uri)
        }

        val file = uriToFile(uri)
        uploadImageFile(file)
    }

    private fun uriToFile(uri: Uri): File {
        val file = File(cacheDir, "img_${System.currentTimeMillis()}.jpg")
        contentResolver.openInputStream(uri)!!.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    // -------------------- Upload --------------------

    private fun uploadImageFile(file: File) {
        showLoading()

        val body = MultipartBody.Part.createFormData(
            when (imageType) {
                1 -> "driver_licence"
                2 -> "license_back_side"
                3 -> "profile_picture"
                4 -> "driver_aadhar"
                5 -> "driver_aadhar_back_side"
                else -> "driver_aadhar_back_side"
            },
            file.name,
            file.asRequestBody("image/*".toMediaTypeOrNull())
        )

        val driverId = SessionSave.getSession("reg_driver_Id", this)
            .toRequestBody("text/plain".toMediaTypeOrNull())

        RetrofitClient.api.uploadLicenseBackSideImage(
            body,
            driverId,
            SessionSave.getSession("Lang", this),
            "a",
            SessionSave.getSession("reg_driver_Id", this),
            "2",
            SessionSave.getSession(CommonData.FIREBASE_KEY, this)
        ).enqueue(object : Callback<UploadResponse> {

            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                hideLoading()
                if (response.isSuccessful) {
                    when (imageType) {
                        1 -> licenseImgUrl = response.body()?.url
                        2 -> licenseBackImgUrl = response.body()?.url
                        3 -> profileImgUrl = response.body()?.url
                        4 -> driver_aadhar = response.body()?.url
                        5 -> driver_aadhar_back_side = response.body()?.url
                    }
                    showToast("Image Uploaded")
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                hideLoading()
                showToast(t.message ?: "Upload Failed")
            }
        })
    }

    // -------------------- UI Helpers --------------------

    private fun showLoading() {
        progressDialog = Dialog(this, R.style.dialogwinddow).apply {
            setContentView(R.layout.progress_bar)
            setCancelable(false)
            Glide.with(this@DriverProfileRegisterActivity)
                .load(R.raw.loading_anim)
                .into(findViewById<ImageView>(R.id.giff))
            show()
        }
    }

    private fun hideLoading() {
        progressDialog?.dismiss()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun positiveButtonClick(dialog: DialogInterface?, id: Int, s: String?) {}
    override fun negativeButtonClick(dialog: DialogInterface?, id: Int, s: String?) {}
}
