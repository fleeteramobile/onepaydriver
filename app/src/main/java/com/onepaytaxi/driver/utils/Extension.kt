/*
 * *
 *  * Created by Nethaji on 27/6/20 1:18 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 27/6/20 12:32 PM
 *
 */
package com.onepaytaxi.driver.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils
import android.util.Base64
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.FontRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.interfaces.ClickInterface


import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


fun Context.getCompatFont(@FontRes fontRes: Int): Typeface =
    ResourcesCompat.getFont(this, fontRes) ?: Typeface.DEFAULT

fun View.getCompatSize(@DimenRes dimenRes: Int): Int =
    resources.getDimension(dimenRes).toInt()

fun View.getCompatColor(@ColorRes colorRes: Int): Int =
    ContextCompat.getColor(this.context, colorRes)

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.toast(msg: Int) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}


fun ImageView.loadCircularImage(image: Any) {
    Glide.with(context)
        .load(
            when (image) {
                is Int -> ContextCompat.getDrawable(context, image)
                is Bitmap -> image
                else -> image
            }
        )
        .circleCrop()
        .error(R.drawable.flag_india)
        .into(this)
}


fun ImageView.loadImage(image: Any) {
    Glide.with(context)
        .load(
            when (image) {
                is Int -> ContextCompat.getDrawable(context, image)
                is Bitmap -> image
                else -> image
            }
        )
        .error(R.drawable.flag_india)
              .into(this)
}


fun customDialog(mContext: Context?,msg: String) {
    // TODO Auto-generated method stub
    val view = View.inflate(mContext, R.layout.custom_msg_popup, null)
    val mDialog = Dialog(mContext!!, R.style.dialogwinddow_trans)
    mDialog.setContentView(view)
    mDialog.setCancelable(true)
    mDialog.show()
    val window: Window? = mDialog.getWindow()
    window!!.setLayout(
        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
        LinearLayoutCompat.LayoutParams.WRAP_CONTENT)

    //        final TextView t = mDialog.findViewById(R.id.f_textview);
//        t.setText(NC.getResources().getString(R.string.email));
    val mail = mDialog.findViewById<AppCompatTextView>(R.id.msg_txt)

    mail.setText(msg)

    val OK = mDialog.findViewById<LinearLayout>(R.id.okbtn)
    //  val Cancel = mDialog.findViewById<TextView>(R.id.cancelbtn)


    OK.setOnClickListener(object : View.OnClickListener {

        override fun onClick(arg0: View) {
            mDialog.dismiss()

        }
    })

}




fun customDialogyerno(mContext: Context?,msg: String,yes: String,no: String,postive_dialogInterface: ClickInterface,s: String) {
    // TODO Auto-generated method stub
    val view = View.inflate(mContext, R.layout.custom_msg_popup_yn, null)
    val mDialog = Dialog(mContext!!, R.style.dialogwinddow_trans)
    mDialog.setContentView(view)
    mDialog.setCancelable(true)
    mDialog.show()
    val window: Window? = mDialog.getWindow()
    window!!.setLayout(
        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
        LinearLayoutCompat.LayoutParams.WRAP_CONTENT)

    //        final TextView t = mDialog.findViewById(R.id.f_textview);
//        t.setText(NC.getResources().getString(R.string.email));
    val mail = mDialog.findViewById<AppCompatTextView>(R.id.msg_txt)

    mail.setText(msg)

    val yesBtn = mDialog.findViewById<LinearLayout>(R.id.yesbtn)
    val noBtn = mDialog.findViewById<LinearLayout>(R.id.nobtn)

    val txtyes = mDialog.findViewById<AppCompatTextView>(R.id.txtyes)
    val txtno = mDialog.findViewById<AppCompatTextView>(R.id.txtno)
    //  val Cancel = mDialog.findViewById<TextView>(R.id.cancelbtn)
    txtyes.setText(yes)
    txtno.setText(no)
    val exitDialog = androidx.appcompat.app.AlertDialog.Builder(mContext)

    yesBtn.setOnClickListener(object : View.OnClickListener {

        override fun onClick(arg0: View) {
            postive_dialogInterface.positiveButtonClick(mDialog, 0, s);
        //    exitDialog.setPositiveButton(yes, postive_dialogInterface)
            mDialog.dismiss()
        }
    })

    noBtn.setOnClickListener(object : View.OnClickListener {

        override fun onClick(arg0: View) {
        //    negative_dialogInterface.onClick(mDialog,1)
            postive_dialogInterface.negativeButtonClick(mDialog, 0, s);

            mDialog.dismiss()

        }
    })





















    //    val mDialogView = LayoutInflater.from(mContext).inflate(R.layout.custom_msg_popup_yn, null)
//    val mBuilder = androidx.appcompat.app.AlertDialog.Builder(mContext!!)
//        .setView(mDialogView)
//        .setTitle("")
//    //show dialog
//    val  mAlertDialog = mBuilder.show()
//    val  wed = mBuilder.setPositiveButton(yes, postive_dialogInterface)
//    mBuilder.setNegativeButton(no, postive_dialogInterface)
//    val mail = mDialogView.findViewById<AppCompatTextView>(R.id.msg_txt)
//    mail.setText(msg)
//
//    val yesBtn = mDialogView.findViewById<LinearLayout>(R.id.yesbtn)
//    val noBtn = mDialogView.findViewById<LinearLayout>(R.id.nobtn)
//
//    val txtyes = mDialogView.findViewById<AppCompatTextView>(R.id.txtyes)
//    val txtno = mDialogView.findViewById<AppCompatTextView>(R.id.txtno)
//    //  val Cancel = mDialog.findViewById<TextView>(R.id.cancelbtn)
//    txtyes.setText(yes)
//    txtno.setText(no)
//
//    yesBtn.setOnClickListener {
//        //dismiss dialog
//        postive_dialogInterface.onClick(mBuilder)
//        mAlertDialog.dismiss()
//    }
//    //cancel button click of custom layout
//    noBtn.setOnClickListener {
//        //dismiss dialog
//
//        mAlertDialog.dismiss()
//    }

}


fun customDill(mContext: Context?,msg: String,yes: String,no: String,postive_dialogInterface: ClickInterface,s: String) {
    // TODO Auto-generated method stub
    val view = View.inflate(mContext, R.layout.custom_msg_popup_yn, null)
    val mDialog = Dialog(mContext!!, R.style.dialogwinddow_trans)
    mDialog.setContentView(view)
    mDialog.setCancelable(true)
    mDialog.show()
    val window: Window? = mDialog.getWindow()
    window!!.setLayout(
        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
        LinearLayoutCompat.LayoutParams.WRAP_CONTENT)

    //        final TextView t = mDialog.findViewById(R.id.f_textview);
//        t.setText(NC.getResources().getString(R.string.email));
    val mail = mDialog.findViewById<AppCompatTextView>(R.id.msg_txt)

    mail.setText(msg)

    val yesBtn = mDialog.findViewById<LinearLayout>(R.id.yesbtn)
    val noBtn = mDialog.findViewById<LinearLayout>(R.id.nobtn)

    val txtyes = mDialog.findViewById<AppCompatTextView>(R.id.txtyes)
    val txtno = mDialog.findViewById<AppCompatTextView>(R.id.txtno)
    //  val Cancel = mDialog.findViewById<TextView>(R.id.cancelbtn)
    txtyes.setText(yes)
    txtno.setText(no)
    val exitDialog = androidx.appcompat.app.AlertDialog.Builder(mContext)

    yesBtn.setOnClickListener(object : View.OnClickListener {

        override fun onClick(arg0: View) {
            postive_dialogInterface.positiveButtonClick(mDialog, 0, s);
        //    exitDialog.setPositiveButton(yes, postive_dialogInterface)
            mDialog.dismiss()
        }
    })

    noBtn.setOnClickListener(object : View.OnClickListener {

        override fun onClick(arg0: View) {
        //    negative_dialogInterface.onClick(mDialog,1)
            postive_dialogInterface.negativeButtonClick(mDialog, 0, s);

            mDialog.dismiss()

        }
    })





















    //    val mDialogView = LayoutInflater.from(mContext).inflate(R.layout.custom_msg_popup_yn, null)
//    val mBuilder = androidx.appcompat.app.AlertDialog.Builder(mContext!!)
//        .setView(mDialogView)
//        .setTitle("")
//    //show dialog
//    val  mAlertDialog = mBuilder.show()
//    val  wed = mBuilder.setPositiveButton(yes, postive_dialogInterface)
//    mBuilder.setNegativeButton(no, postive_dialogInterface)
//    val mail = mDialogView.findViewById<AppCompatTextView>(R.id.msg_txt)
//    mail.setText(msg)
//
//    val yesBtn = mDialogView.findViewById<LinearLayout>(R.id.yesbtn)
//    val noBtn = mDialogView.findViewById<LinearLayout>(R.id.nobtn)
//
//    val txtyes = mDialogView.findViewById<AppCompatTextView>(R.id.txtyes)
//    val txtno = mDialogView.findViewById<AppCompatTextView>(R.id.txtno)
//    //  val Cancel = mDialog.findViewById<TextView>(R.id.cancelbtn)
//    txtyes.setText(yes)
//    txtno.setText(no)
//
//    yesBtn.setOnClickListener {
//        //dismiss dialog
//        postive_dialogInterface.onClick(mBuilder)
//        mAlertDialog.dismiss()
//    }
//    //cancel button click of custom layout
//    noBtn.setOnClickListener {
//        //dismiss dialog
//
//        mAlertDialog.dismiss()
//    }

}

fun Activity.showDialAlertSingle(title: String) {
    val exitDialog = androidx.appcompat.app.AlertDialog.Builder(this)
    exitDialog.setMessage(title)
    exitDialog.setPositiveButton(
        "OK"
    ) { dialog, which ->
        dialog.dismiss()
        dialog.cancel()
    }
    exitDialog.show()
}







fun bitMapToFile(bitmapImage: Bitmap, context: Context): File {
    var file: File? = null
    try {
        val f =
            File(context.getCacheDir(), (Calendar.getInstance().timeInMillis).toString() + ".jpg")
        f.createNewFile()

        val bos = ByteArrayOutputStream()
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 70, bos)
        val bitmapdata = bos.toByteArray()
        val fos = FileOutputStream(f)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        file = f
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return file!!
}

fun getBitmapFromURL(src: String?): Bitmap? {
    return try {
        val url = URL(src)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input: InputStream = connection.inputStream
        BitmapFactory.decodeStream(input)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun bitMapToString(bitmap: Bitmap?): String {
    return try {
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 60, baos)
        val b = baos.toByteArray()
        Base64.encodeToString(b, Base64.DEFAULT)
    } catch (e: Exception) {
        println("GET______" + e.toString())
        ""
    }
}

public fun toRequestBody(value: String): RequestBody {
    return value.toRequestBody("text/plain".toMediaTypeOrNull())
}

fun dismissKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (null != activity.currentFocus)
        imm.hideSoftInputFromWindow(activity.currentFocus!!.applicationWindowToken, 0)
}


fun dismissKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.showKeyboard() {
    this.requestFocus()
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun Fragment.blockInput(progressBar: ProgressBar) {
    progressBar.visibility = View.VISIBLE
    activity?.window?.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )
}

fun Fragment.unblockInput(progressBar: ProgressBar) {
    progressBar.visibility = View.GONE
    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun Activity.blockInput(progressBar: ProgressBar) {
    progressBar.visibility = View.VISIBLE
    window.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )
}

fun Activity.unblockInput(progressBar: ProgressBar) {
    progressBar.visibility = View.GONE
    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun Activity.unblockInput() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun isConnected(activity: Activity): Boolean {
    val connectivityManager =
        activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.activeNetworkInfo != null
}

fun String.isSuccess(): Boolean = this == "1"

fun String.isFailure(): Boolean = this == "failure"

fun stingChangeInToLong(number: String): Long {
    val stt = number.replace("\\D+".toRegex(), "")
    return stt.toLong()
}





fun isLocationEnabled(context: Context): Boolean {
    var locationMode = 0
    val locationProviders: String
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        locationMode = try {
            Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
            return false
        }
        locationMode != Settings.Secure.LOCATION_MODE_OFF
    } else {
        locationProviders = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.LOCATION_PROVIDERS_ALLOWED
        )
        !TextUtils.isEmpty(locationProviders)
    }
}
