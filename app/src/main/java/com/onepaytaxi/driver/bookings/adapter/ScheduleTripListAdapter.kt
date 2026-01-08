package com.onepaytaxi.driver.bookings.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.bookings.interfaces.ScheduleTrip
import com.onepaytaxi.driver.bookings.model.ResponseNewSchduleBooking
import com.onepaytaxi.driver.utils.SessionSave


class ScheduleTripListAdapter(
    val scheduleTrip: ScheduleTrip,
    upComingList: List<ResponseNewSchduleBooking.Detail.ShowBooking>,
    mContext: Context
) :
    RecyclerView.Adapter<ScheduleTripListAdapter.ViewHolder>() {

    val upComingList = upComingList
    var mContext = mContext


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textViewUserName: TextView
        var trip_accept: TextView
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
            trip_accept = itemView.findViewById(R.id.trip_accept) as TextView


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutInflater.from(parent.context).inflate(R.layout.schedule_trips_items, parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount() = upComingList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var textViewUserName: TextView
        var textViewApplePay: TextView
        var textViewPrice: TextView
        var textViewDistance: TextView
        var textViewPickupAddress: TextView
        var textViewDropOffAddress: TextView
        var completed_trip_lay: CardView


        holder.textViewUserName.setText("${upComingList[position].pickup_time } ")
        holder.textViewApplePay.setText("Outstation ")
        holder.trip_id.setText("#${upComingList[position].passengers_log_id.toString() } ")
        val currencySymbol = SessionSave.getSession("site_currency", mContext) ?: "₹" // Provide a default if null

        holder.textViewPrice.setText("${currencySymbol}${upComingList[position].approx_fare.toString() } ")
      //  holder.textViewDistance.setText("${upComingList[position].travelled_distance }  KM ")
        holder.textViewPickupAddress.setText("${upComingList[position].pickup_location } ")
        holder.textViewDropOffAddress.setText(" ${upComingList[position].drop_location } ")


        holder.trip_accept.setOnClickListener {


            scheduleTrip.acceptScheduleTrip(upComingList[position])


            }

        }







}