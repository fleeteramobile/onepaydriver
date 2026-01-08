package com.mayan.sospluginmodlue

import android.app.AlertDialog
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mayan.sospluginmodlue.model.ContactsData
import kotlinx.android.synthetic.main.sos__contact_list_item.view.*

class ContactAdapter(val item: ArrayList<ContactsData>, val mContext: Context, val itemClicked: ItemClicked) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(LayoutInflater.from(mContext).inflate(R.layout.sos__contact_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {

        holder.tvContact?.text = item[position].contact_name
        holder.tvPhoneNumber?.text = item[position].country_code + " " + item[position].contact_number

        holder.imageDelete.tag = holder.imageProgress

    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return item.size

    }

    fun remove(contactID: Int) {
        for (i in item.indices) {
            if (item[i].contact_id == contactID) {
                item.removeAt(i)
                notifyItemRemoved(i)
                return
            }
        }


    }

    fun add(data: ContactsData, position: Int) {
        item.add(position, data)
        notifyItemInserted(position)
    }

    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val tvContact = view.tv_contact
        val tvPhoneNumber = view.tv_phno
        val imageDelete = view.img_delete
        val imageProgress = view.img_progress

        init {
            imageDelete.setOnClickListener {


                val builder = AlertDialog.Builder(mContext)
                // Set the alert dialog title
                builder.setTitle("")
                // Display a message on alert dialog
                builder.setMessage(mContext.resources.getString(R.string.alert_delete_contact))
                builder.setPositiveButton(mContext.resources.getString(R.string.ok)) { dialog, which ->
                    view.visibility = View.GONE
                    if (view.tag is View)
                        view.visibility = View.VISIBLE
                    itemClicked.deleteItemClicked(item[adapterPosition].contact_id!!)
                    dialog.dismiss()
                }
                // Display a negative button on alert dialog
                builder.setNegativeButton(mContext.resources.getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                val dialogs: AlertDialog = builder.create()
                dialogs.setOnShowListener {
                    if (dialogs != null) {
                        dialogs.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(mContext, R.color.button_accept))
                        dialogs.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(mContext, R.color.black))
                    }
                }
                dialogs.show()
            }
        }


    }
}





