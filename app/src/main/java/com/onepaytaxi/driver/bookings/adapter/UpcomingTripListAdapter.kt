package com.onepaytaxi.driver.bookings.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.bookings.interfaces.UpcomeingTrip
import com.onepaytaxi.driver.bookings.model.ResponseUpcomingTrips
import com.onepaytaxi.driver.utils.SessionSave


class UpcomingTripListAdapter(
    val completedTrip: UpcomeingTrip,
    upComingList: List<ResponseUpcomingTrips.Detail.PendingBooking>,
    mContext: Context
) :
    RecyclerView.Adapter<UpcomingTripListAdapter.ViewHolder>() {

    val upComingList = upComingList
    var mContext = mContext


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textViewUserName: TextView
        var textViewApplePay: TextView
        var trip_id: TextView
        var textViewPrice: TextView
        var textViewDistance: TextView
        var textViewDiscount: TextView
        var textViewPickupAddress: TextView
        var textViewDropOffAddress: TextView
        var btn_get_in_contact: TextView
        var btn_cancel: TextView
        var btn_start: TextView
        var completed_trip_lay: CardView


        init {


            textViewUserName = itemView.findViewById(R.id.textViewUserName) as TextView
            trip_id = itemView.findViewById(R.id.trip_id) as TextView
            textViewApplePay = itemView.findViewById(R.id.textViewApplePay) as TextView
            textViewDiscount = itemView.findViewById(R.id.textViewDiscount) as TextView
            textViewPrice = itemView.findViewById(R.id.textViewPrice) as TextView
            textViewPickupAddress = itemView.findViewById(R.id.textViewPickupAddress) as TextView
            textViewDistance = itemView.findViewById(R.id.textViewDistance) as TextView
            textViewDropOffAddress = itemView.findViewById(R.id.textViewDropOffAddress) as TextView
            completed_trip_lay = itemView.findViewById(R.id.completed_trip_lay) as CardView
            btn_get_in_contact = itemView.findViewById(R.id.btn_get_in_contact) as TextView
            btn_cancel = itemView.findViewById(R.id.btn_cancel) as TextView
            btn_start = itemView.findViewById(R.id.btn_start) as TextView


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutInflater.from(parent.context).inflate(R.layout.upcoming_trip_list_items, parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount() = upComingList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.textViewUserName.setText("${upComingList[position].passenger_name} ")
        holder.textViewDiscount.setText("${upComingList[position].pickup_time.toString()} ")

        holder.trip_id.setText("#${upComingList[position]._id.toString()} ")
        val currencySymbol =
            SessionSave.getSession("site_currency", mContext) ?: "₹" // Provide a default if null

        holder.textViewPrice.setText("${currencySymbol}${upComingList[position].approx_fare} ")
        //   holder.textViewDistance.setText("${upComingList[position].travelled_distance }  KM ")
        holder.textViewPickupAddress.setText("${upComingList[position].pickup_location} ")
        holder.textViewDropOffAddress.setText(" ${upComingList[position].drop_location} ")

        if(upComingList[position].trip_approval.equals("0"))
        {
            holder.btn_start.setText("Track Trip")
        }
        else{
            holder.btn_start.setText("Start Trip")
        }

        if(upComingList[position].os_trip_type.equals("1"))
        {
            holder.textViewApplePay.setText("Oneway")
        }
        else{
            holder.textViewApplePay.setText("Round Trip")
        }


        holder.btn_get_in_contact.setOnClickListener {


            completedTrip.callCustomer(upComingList[position])


        }
        holder.btn_start.setOnClickListener {


                completedTrip.startTrip(upComingList[position])




        }
        holder.btn_cancel.setOnClickListener {
            completedTrip.decline(upComingList[position])

        }

    }


}