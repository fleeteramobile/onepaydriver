package com.mayan.sospluginmodlue

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mayan.sospluginmodlue.model.ApiRequestData
import com.mayan.sospluginmodlue.model.ContactsData
import com.mayan.sospluginmodlue.model.EmergencyListData
import com.mayan.sospluginmodlue.service.CoreClient
import com.mayan.sospluginmodlue.service.SOSService
import com.mayan.sospluginmodlue.service.ServiceGenerator
import com.mayan.sospluginmodlue.util.SessionSave
import kotlinx.android.synthetic.main.sos__activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

private const val RECORD_REQUEST_CODE = 400
private const val MY_PERMISSIONS_REQUEST_GPS = 200
class SOSActivity : AppCompatActivity(), ItemClicked {
    override fun deleteItemClicked(id: Int) {
        callDeleteApi(id)
    }
    private var langcountry=""

    var contactsDataList: ArrayList<ContactsData> = ArrayList()
//    var skeletonScreen: RecyclerViewSkeletonScreen? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SessionSave.getSession("Lang", this@SOSActivity) == "") {
            SessionSave.saveSession("Lang", "en", this@SOSActivity)

        }
        if (SessionSave.getSession("Lang_Country", this@SOSActivity) == "") {
            SessionSave.saveSession("Lang_Country", "en_US", this@SOSActivity)
        }
        print("langggg"+SessionSave.getSession("Lang", this@SOSActivity)+":"+SessionSave.getSession("Lang_Country", this@SOSActivity))

        val config = Configuration()
        if(SessionSave.getSession("Lang", this@SOSActivity).equals("pr")){
            langcountry = "pt_PT"
            val arry = langcountry.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            config.locale = Locale( arry[0], arry[1])
            Locale.setDefault(Locale(arry[0], arry[1]))
            this@SOSActivity.baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        }else {
            langcountry = SessionSave.getSession("Lang_Country", this@SOSActivity)
            val arry = langcountry.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            config.locale = Locale(SessionSave.getSession("Lang", this@SOSActivity), arry[1])
            Locale.setDefault(Locale(SessionSave.getSession("Lang", this@SOSActivity), arry[1]))
            this@SOSActivity.baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        }

        setContentView(R.layout.sos__activity_main)

        checkPermissionLocation()
        submit_button?.setOnClickListener {
            if (isPermissionsGranted()) {
                openContactIntent()
            } else {
                makeRequest()
            }
        }
        rv_contact_list?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rv_contact_list?.adapter = ContactAdapter(contactsDataList, this, this)
        no_data_lay?.visibility = View.GONE
//        skeletonScreen = Skeleton.bind(rv_contact_list)
//                .adapter(rv_contact_list.adapter)
//                .shimmer(true)
//                .angle(20)
//                .frozen(false)
//                .duration(1200)
//                .count(3)
//                .load(R.layout.sos__empty_list_item)
//                .show()
//
//        val alphaAdapter = SlideInLeftAnimationAdapter(rv_contact_list.adapter)
//        alphaAdapter.setDuration(5000)
//        rv_contact_list.adapter = alphaAdapter
        emergency.setOnClickListener {


            val listOfTestObject = object : TypeToken<List<ContactsData>>() {

            }.type
            val s = Gson().toJson(contactsDataList, listOfTestObject)
            SessionSave.saveSession("contact_sos_list", s, this@SOSActivity)
            //getLastLocation()
            SOSService.startLocationService(this@SOSActivity)
        }
        back.setOnClickListener {
            finish()
        }

        callApi()
    }

    private fun checkPermissionLocation() {

        Handler().postDelayed({
            try {
                if (ActivityCompat.checkSelfPermission(this@SOSActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this@SOSActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) run {
                    val alertBuilder = AlertDialog.Builder(this@SOSActivity)
                    alertBuilder.setCancelable(true)
                    alertBuilder.setMessage(resources.getString(R.string.str_loc))
                    alertBuilder.setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                        ActivityCompat.requestPermissions(this@SOSActivity,
                                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                                MY_PERMISSIONS_REQUEST_GPS)
                        dialog.dismiss()
                    }
                    alertBuilder.setNegativeButton(resources.getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }
                    val alert = alertBuilder.create()

                    alert?.setOnShowListener {
                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this@SOSActivity, R.color.button_accept))
                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this@SOSActivity, R.color.black))
                    }
                    alert.show()
                } else {
                    println("____________+")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 100)
    }


    private fun callApi() {
        println("__________drviere" + SessionSave.getSession("sos_id", this@SOSActivity))
        val client = ServiceGenerator(this@SOSActivity,"").createService(CoreClient::class.java)
        val request = ApiRequestData.getEmergencyRequestData(SessionSave.getSession("user_type", this@SOSActivity), SessionSave.getSession("sos_id", this@SOSActivity).toInt(), SessionSave.getSession("company_id", this@SOSActivity))

        val loginResponse = client.emergencyList(request, SessionSave.getSession("Lang", this@SOSActivity))

        loginResponse.enqueue(object : Callback<EmergencyListData> {
            override fun onFailure(call: Call<EmergencyListData>?, t: Throwable?) {
                errorMessage(getString(R.string.please_try_again))
            }

            override fun onResponse(call: Call<EmergencyListData>?, response: Response<EmergencyListData>?) {
                val emergencyListData = response?.body()
                if (emergencyListData?.status == 1) {
                  //  skeletonScreen?.hide()
                    if (emergencyListData.detail.size > 0) {
                        rv_contact_list.visibility = View.VISIBLE
                        contactsDataList = emergencyListData.detail
                        rv_contact_list?.adapter = ContactAdapter(contactsDataList, this@SOSActivity, this@SOSActivity)
                        ContactsReachedLimitUI()

                        val listOfTestObject = object : TypeToken<List<ContactsData>>() {

                        }.type
                        val s = Gson().toJson(contactsDataList, listOfTestObject)
                        SessionSave.saveSession("contact_sos_list", s, this@SOSActivity)
                    } else {
                        noDataLayUI()
                    }
                } else {
                    noDataLayUI()
                }
            }

        })


    }

    private fun callAddApi(phno: String, name: String, c_code: String) {

        val client = ServiceGenerator(this@SOSActivity,"").createService(CoreClient::class.java)
        val request = ApiRequestData.AddContactRequestData(SessionSave.getSession("sos_id", this@SOSActivity).toInt(), SessionSave.getSession("user_type", this@SOSActivity), phno, name, c_code, SessionSave.getSession("company_id", this@SOSActivity))

        val loginResponse = client.addContact(request, SessionSave.getSession("Lang", this@SOSActivity))
        showAddLoading()

        loginResponse.enqueue(object : Callback<EmergencyListData> {
            override fun onFailure(call: Call<EmergencyListData>?, t: Throwable?) {
                println(t?.localizedMessage)
                errorMessage(getString(R.string.please_try_again))
            }

            override fun onResponse(call: Call<EmergencyListData>?, response: Response<EmergencyListData>?) {
                val emergencyListData = response?.body()
                if (emergencyListData?.status == 1) {
               //     skeletonScreen?.hide()
                    if (emergencyListData.detail.size > 0) {
                        rv_contact_list.visibility = View.VISIBLE
                        contactsDataList = emergencyListData.detail
                        rv_contact_list?.adapter = ContactAdapter(contactsDataList, this@SOSActivity, this@SOSActivity)
                        ContactsReachedLimitUI()
                        val listOfTestObject = object : TypeToken<List<ContactsData>>() {

                        }.type
                        val s = Gson().toJson(contactsDataList, listOfTestObject)
                        SessionSave.saveSession("contact_sos_list", s, this@SOSActivity)
                    } else {
                        errorMessage(getString(R.string.please_try_again))
                    }
                } else {
                    // more_info.setText(emergencyListData?.message)
                    Toast.makeText(this@SOSActivity, emergencyListData?.message, Toast.LENGTH_LONG).show()
                    hideAddLoading()
                }
            }

        })


    }


    private fun callDeleteApi(id: Int) {

        val client = ServiceGenerator(this@SOSActivity,"").createService(CoreClient::class.java)
        val request = ApiRequestData.DeleteContactRequestData(SessionSave.getSession("sos_id", this@SOSActivity).toInt(), SessionSave.getSession("user_type", this@SOSActivity), id, SessionSave.getSession("company_id", this@SOSActivity))

        val loginResponse = client.deleteContact(request, SessionSave.getSession("Lang", this@SOSActivity))

        loginResponse.enqueue(object : Callback<EmergencyListData> {
            override fun onFailure(call: Call<EmergencyListData>?, t: Throwable?) {
                hideAddLoading()
                errorMessage(getString(R.string.please_try_again))
            }

            override fun onResponse(call: Call<EmergencyListData>?, response: Response<EmergencyListData>?) {
                hideAddLoading()
                val emergencyListData = response?.body()
                if (emergencyListData?.status == 1) {
                 //   skeletonScreen?.hide()


                    if (emergencyListData.detail.size > 0) {
                        rv_contact_list.visibility = View.VISIBLE

                        for (contactData in contactsDataList) {
                            if (emergencyListData.detail.contains(contactData)) {
                                println("contact *" + contactData.contact_name)
                            } else {
                                println("contact **" + contactData.contact_name)
                                (rv_contact_list.adapter as ContactAdapter).remove(contactData.contact_id!!)
                                break
                            }

                        }
                        contactsDataList = emergencyListData.detail
                        rv_contact_list?.adapter = ContactAdapter(contactsDataList, this@SOSActivity, this@SOSActivity)
                        val listOfTestObject = object : TypeToken<List<ContactsData>>() {

                        }.type
                        val s = Gson().toJson(contactsDataList, listOfTestObject)
                        SessionSave.saveSession("contact_sos_list", s, this@SOSActivity)
                        ContactsReachedLimitUI()
                    } else {
                        SessionSave.saveSession("contact_sos_list", "", this@SOSActivity)
                        contactsDataList = emergencyListData.detail
                        noDataLayUI()
                    }
                } else {
//                    more_info.setText(emergencyListData?.message)
                    Toast.makeText(this@SOSActivity, emergencyListData?.message, Toast.LENGTH_LONG).show()
                }
            }

        })


    }

    private fun openContactIntent() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    private fun isPermissionsGranted(): Boolean {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("", "Permission to record denied")
//            makeRequest()
            return false
        }
        return true
    }

    private fun makeRequest() {
        val alertBuilder = AlertDialog.Builder(this@SOSActivity)
        alertBuilder.setCancelable(true)
        alertBuilder.setMessage(resources.getString(R.string.sos_contact))
        alertBuilder.setPositiveButton(android.R.string.yes) { dialog, which ->
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    RECORD_REQUEST_CODE)
            dialog.dismiss()
        }
        alertBuilder.setNegativeButton(android.R.string.no) { dialog, which -> dialog.dismiss() }
        val alert = alertBuilder.create()

        alert!!.setOnShowListener {
            if (alert != null) {
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.button_accept))
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.black))
            }
        }
        alert.show()


    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {


        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                } else {
                    openContactIntent()
                }
            }
            else -> { // Note the block
                print("x is neither 1 nor 2")
            }
        }
    }


    fun ContactsReachedLimitUI() {
        if (contactsDataList.size == 3) {
            no_data_lay?.visibility = View.GONE
            rv_contact_list?.visibility = View.VISIBLE
            submit_button.visibility = View.GONE
            more_info.visibility = View.VISIBLE
            adding_progress.hide()
            more_info.text = getString(R.string.contact_limit_reached)
        } else if (contactsDataList.size == 0) {
            noDataLayUI()
        } else {
            no_data_lay?.visibility = View.GONE
            rv_contact_list?.visibility = View.VISIBLE
            hideAddLoading()
            more_info.visibility = View.VISIBLE
            more_info.text = getString(R.string.sos_more_info)
        }


    }

    private fun showAddLoading() {
        adding_progress.show()
        submit_button.visibility = View.GONE
    }


    private fun hideAddLoading() {
        adding_progress.hide()
        submit_button.visibility = View.VISIBLE

    }

    fun errorMessage(message: String) {
     //   skeletonScreen?.hide()
        no_data_lay?.visibility = View.VISIBLE
        rv_contact_list?.visibility = View.GONE
        long_description?.visibility = View.GONE
        short_description?.visibility = View.VISIBLE
        short_description.text = message
        hideAddLoading()
    }

    fun noDataLayUI() {
     //   skeletonScreen?.hide()
        no_data_lay?.visibility = View.VISIBLE
        rv_contact_list?.visibility = View.GONE
        long_description?.visibility = View.VISIBLE
        hideAddLoading()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, datas: Intent?) {
        if (resultCode == Activity.RESULT_OK && datas != null) {
            val contactData = datas.data
            val c = contentResolver.query(contactData!!, null, null, null, null)
            if (c!!.moveToFirst()) {

                var phoneNumber = ""
                var emailAddress = ""
                val name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID))
                //http://stackoverflow.com/questions/866769/how-to-call-android-contacts-list   our upvoted answer

                var hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                if (hasPhone.equals("1", ignoreCase = true))
                    hasPhone = "true"
                else
                    hasPhone = "false"

                if (java.lang.Boolean.parseBoolean(hasPhone)) {
                    val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null)
                    while (phones!!.moveToNext()) {
                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    }
                    phones.close()
                }

                // Find Email Addresses
                val emails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null)
                while (emails!!.moveToNext()) {
                    emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                }
                emails.close()

                Log.d("curs", "$name num$phoneNumber mail$phoneNumber")
                println("cruss" + name + "__" + emailAddress)
                phoneNumber = phoneNumber.replace("-", "").replace("\\s".toRegex(), "").replace(SessionSave.getSession("default", this@SOSActivity), "")

                var isAlreadyPresent: Boolean = false
                for (contats in contactsDataList) {
                    if (contats.contact_number.equals(phoneNumber)) {
                        isAlreadyPresent = true
                    }
                }
                if (isAlreadyPresent) {
                    // more_info.setText(getString(R.string.already_added))
                    Toast.makeText(this@SOSActivity, getString(R.string.already_added), Toast.LENGTH_LONG).show()
                } else {
                    println("numbereee nn" + phoneNumber)
                    if (phoneNumber.length < 7)
                        Toast.makeText(this@SOSActivity, getString(R.string.contact_min_alert), Toast.LENGTH_LONG).show()
                    else if (phoneNumber.length > 15)
                        Toast.makeText(this@SOSActivity, getString(R.string.contact_min_alert), Toast.LENGTH_LONG).show()
                    else
                        callAddApi(phoeNumberWithOutCountryCode(phoneNumber), name, countryCodefromPhoneNumber(phoneNumber))
                }
                println("_________" + contactsDataList.size)

                no_data_lay?.visibility = View.GONE
            }
            c.close()
        }
    }

    fun countryCodefromPhoneNumber(phoneNumber: String): String {
        if (phoneNumber.contains("+") && phoneNumber.length > 10) {
            var phoneNumbers = phoneNumber.substring(0, phoneNumber.length - 10)
            println("numbereee countrycode" + phoneNumbers)
            return phoneNumbers


        } else return ""
    }

    fun phoeNumberWithOutCountryCode(phoneNumber: String): String {
        if (phoneNumber.contains("+") && phoneNumber.length > 10) {
            var phoneNumbers = phoneNumber.substring(phoneNumber.length - 10, phoneNumber.length)
            println("numbereee phonen" + phoneNumbers)
            return phoneNumbers
        } else return phoneNumber
    }
}