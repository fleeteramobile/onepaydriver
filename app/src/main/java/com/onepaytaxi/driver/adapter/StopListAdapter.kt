package com.onepaytaxi.driver.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.onepaytaxi.driver.R

class StopListAdapter(private val mContext: Context, private var data: ArrayList<String>) : RecyclerView.Adapter<StopListAdapter.CustomViewHolder>() {
    init {
        setisExpanded(false)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(mContext)
        var view: View? = null

        view = inflater.inflate(R.layout.stop_list_lay, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {


        if (position == 0 && !isExpanded && data.size != 1) {
            holder.collapseIv.visibility = View.VISIBLE
            holder.collapseIv.setImageResource(R.drawable.ic_arrow_down)
        } else if (position == 0 && isExpanded) {
            holder.collapseIv.visibility = View.VISIBLE
           // holder.collapseIv.setImageResource(R.drawable.ic_arrow_up)
        } else {
            holder.collapseIv.visibility = View.INVISIBLE
        }
        if (itemCount == 1 && data.size > 0) {
            holder.stopTxt.text = data[data.size - 1]
        } else {
            holder.stopTxt.text = data[position]
        }
        holder.stopTxt.post {
            holder.stopTxt.isSelected = true
        }

    }


    override fun getItemCount(): Int {
        return if (!isExpanded) {
            1
        } else
            data.size
    }


    /**
     * View holder class member this contains in every row in list.
     */
    inner class CustomViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var stopTxt: TextView = v.findViewById(R.id.stop_txt)
        var collapseIv: ImageView = v.findViewById(R.id.collapse_iv)

        init {
            collapseIv.setOnClickListener {
                setisExpanded(!isExpanded)
                notifyDataSetChanged()
            }
        }

    }


    var isExpanded = false

    fun setisExpanded(expand: Boolean) {
        isExpanded = expand
    }


}
