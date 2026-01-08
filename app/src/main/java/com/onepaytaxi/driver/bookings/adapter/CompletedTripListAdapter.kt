package com.onepaytaxi.driver.bookings.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.bookings.interfaces.CompletedTrip
import com.onepaytaxi.driver.bookings.model.ResponsePastBooking
import com.onepaytaxi.driver.utils.SessionSave


class CompletedTripListAdapter(
    val completedTrip: CompletedTrip,
    upComingList: List<ResponsePastBooking.Detail.PastBooking>,
    mContext: Context
) :
    RecyclerView.Adapter<CompletedTripListAdapter.ViewHolder>() {

    val upComingList = upComingList
    var mContext = mContext


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textViewUserName: TextView
        var textViewApplePay: TextView
        var trip_id: TextView
        var textViewPrice: TextView
        var textViewDistance: TextView
        var textViewPickupAddress: TextView
        var textViewDropOffAddress: TextView
        var completed_trip_lay: CardView


        init {


            textViewUserName = itemView.findViewById(R.id.textViewUserName) as TextView
            trip_id = itemView.findViewById(R.id.trip_id) as TextView
            textViewApplePay = itemView.findViewById(R.id.textViewApplePay) as TextView
            textViewPrice = itemView.findViewById(R.id.textViewPrice) as TextView
            textViewPickupAddress = itemView.findViewById(R.id.textViewPickupAddress) as TextView
            textViewDistance = itemView.findViewById(R.id.textViewDistance) as TextView
            textViewDropOffAddress = itemView.findViewById(R.id.textViewDropOffAddress) as TextView
            completed_trip_lay = itemView.findViewById(R.id.completed_trip_lay) as CardView


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutInflater.from(parent.context).inflate(R.layout.completed_trips_items, parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount() = upComingList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {



        holder.textViewUserName.setText("${upComingList[position].actual_pickup_time } ")
        holder.textViewApplePay.setText("${upComingList[position].payment_type } ")
        holder.trip_id.setText("#${upComingList[position]._id.toString() } ")
        val currencySymbol = SessionSave.getSession("site_currency", mContext) ?: "₹" // Provide a default if null

        holder.textViewPrice.setText("${currencySymbol}${upComingList[position].amt } ")
     //   holder.textViewDistance.setText("${upComingList[position].travelled_distance }  KM ")
        holder.textViewPickupAddress.setText("${upComingList[position].pickup_location } ")
        holder.textViewDropOffAddress.setText(" ${upComingList[position].drop_location } ")


        holder.completed_trip_lay.setOnClickListener {


                    completedTrip.showTripDetails(upComingList[position])


            }

        }







}