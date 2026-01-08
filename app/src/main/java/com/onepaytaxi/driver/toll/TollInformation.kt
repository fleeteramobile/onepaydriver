package com.onepaytaxi.driver.toll

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.interfaces.APIResult
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON
import com.onepaytaxi.driver.utils.SessionSave
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONObject

class TollInformation: BottomSheetDialogFragment() {
    private var lay_pick: LinearLayout? = null
    private var et_type: AppCompatTextView? = null
    private var et_place_name: EditText? = null
    private var et_amount: EditText? = null
    private var btn_add_amount: LinearLayout? = null
var paymentType = ""
    var ExtrasArrayList: ArrayList<TripAddonFare> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.top_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lay_pick = view.findViewById(R.id.lay_pick)
        et_type = view.findViewById(R.id.et_type)

        et_place_name = view.findViewById(R.id.et_place_name)
        et_amount = view.findViewById(R.id.et_amount)
        btn_add_amount = view.findViewById(R.id.btn_add_amount)
        lay_pick!!.setOnClickListener {
            val url = "type=get_trip_addon_fare_list"

            ExtrasList(url, requireActivity(), this).fetchExtrasList()  // Pass reference of the fragment to ExtrasList


        }
        et_type!!.setOnClickListener {
            val url = "type=get_trip_addon_fare_list"

            ExtrasList(url, requireActivity(), this).fetchExtrasList()  // Pass reference of the fragment to ExtrasList
        }
        btn_add_amount!!.setOnClickListener {
            if (paymentType.equals(""))
            {
                Toast.makeText(requireActivity(), "Select the payment type", Toast.LENGTH_SHORT).show()

            }
            if (et_place_name!!.text.toString().equals(""))
            {
                Toast.makeText(requireActivity(), "Enter the place name", Toast.LENGTH_SHORT).show()

            }

           else if (et_amount!!.text.toString().equals(""))
            {
                Toast.makeText(requireActivity(), "Enter the amount", Toast.LENGTH_SHORT).show()

            }
            else{
                val url = "type=add_trip_addon_fare"

                addToll(requireActivity(),paymentType,et_place_name!!.text.toString(),et_amount!!.text.toString(),url,et_type!!.text.toString())

                dismiss()
            }

            }




    }




    class ExtrasList(
        private val url: String?,
        private val activity: FragmentActivity,
        private val fragment: TollInformation // Passing a reference of the fragment
    ) : APIResult {

        fun fetchExtrasList() {
            try {
                APIService_Retrofit_JSON(activity, this, true).execute(url)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            try {
                if (isSuccess) {
                    val extrasArrayList: ArrayList<TripAddonFare> = ArrayList()
                    val json = JSONObject(result)
                    json.getJSONArray("trip_addon_fare").let {
                        for (i in 0 until it.length()) {
                            val json_data = it.getJSONObject(i)
                            extrasArrayList.add(
                                TripAddonFare(
                                    json_data.getString("_id"),
                                    json_data.getString("fare_type"),
                                    json_data.getString("status")
                                )
                            )
                        }
                    }
                    fragment.ExtrasArrayList = extrasArrayList // Update the fragment's ExtrasArrayList
                    fragment.showRecyclerViewDialog() // Call showRecyclerViewDialog() from the fragment
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private class addToll(
        private val requireActivity: FragmentActivity,
        paymentType: String,
        et_place_name: String,
        et_amount: String,
        url: String,
        private val type: String
    ) :
        APIResult {
        init {
            try {
                val j = JSONObject()
                j.put("driver_id", SessionSave.getSession("Id", requireActivity))
                j.put("trip_id", SessionSave.getSession("trip_id", requireActivity))
                j.put("name", et_place_name)
                j.put("amount", et_amount)
                j.put("fare_type", paymentType)

                APIService_Retrofit_JSON(requireActivity, this, j, false).execute(url)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            try {
                if (isSuccess) {
                    val json = JSONObject(result)
                    println(json.getJSONObject("data").getString("total"))

                    Toast.makeText(requireActivity,"Total ${type} amount is  ${ json.getJSONObject("data").getString("total")}" , Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



     fun showRecyclerViewDialog() {
        // Inflate the dialog view
        val dialogView =
            LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_recyclerview, null)

        // Create the dialog using AlertDialog.Builder
        val dialog = AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .create()

        // Set up RecyclerView in the dialog
        val recyclerView: RecyclerView = dialogView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        // Sample data
        println("Output_result_extras_size ${ExtrasArrayList.size.toString()}")

        // Set up the adapter
        val adapter = MyAdapter(ExtrasArrayList) { selectedItem ->
            paymentType = selectedItem._id
            et_type!!.setText(selectedItem.fare_type)
          //  Toast.makeText(requireActivity(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
            dialog.dismiss() // Dismiss dialog after item selection
        }
        recyclerView.adapter = adapter

        // Show the dialog
        dialog.show()
    }
}