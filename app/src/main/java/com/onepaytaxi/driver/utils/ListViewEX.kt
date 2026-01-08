package com.onepaytaxi.driver.utils


import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.data.BookLaterData
import java.util.*


class ListViewEX : LinearLayout {
    @JvmOverloads
    constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr)

   /* @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes)*/

    private lateinit var stopArray: ArrayList<HashMap<String, String>>

    var type: String = ""
    var lang: String = ""


    init {
        orientation = VERTICAL
    }

    fun setData(stopArray: ArrayList<HashMap<String, String>>, types: String, language: String) {
        removeAllViews()
        this.stopArray = stopArray
        this.type = types
        this.lang = language
        if (stopArray.size != 0) {
            println("ARRRAR SIZE ${stopArray.size}")
            stopArray.removeAt(stopArray.size - 1)
            for (i in 0 until stopArray.size) {
                println("ARRRAR data ${stopArray[i]["KEY"]}")
                val data = BookLaterData(stopArray[i]["KEY"].toString(), stopArray[i]["VALUE"].toString())
                createLayout(data, i)
            }
        }
    }

    private fun createLayout(stopData: BookLaterData, i: Int) {


        if (lang == "ar" || lang == "fa") {
            val mainLayout = LinearLayout(context)
            val mainLayParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            mainLayParams.setMargins(resources.getDimension(R.dimen.dp_5).toInt(), resources.getDimension(R.dimen.dp_5).toInt(), resources.getDimension(R.dimen.dp_5).toInt(), resources.getDimension(R.dimen.dp_5).toInt())
            mainLayout.layoutParams = mainLayParams
            mainLayout.orientation = HORIZONTAL

            val txtLable = AppCompatTextView(context)
            val txtColon = AppCompatTextView(context)
            val txtValue = AppCompatTextView(context)

            val txtValueParams = LayoutParams(0, LayoutParams.MATCH_PARENT)
            txtValueParams.weight = 1f
            txtValue.apply {
                id = i
                layoutParams = txtValueParams
                text = stopData.value
                gravity = Gravity.CENTER_VERTICAL
                isSingleLine = true
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.MARQUEE
                marqueeRepeatLimit = -1
            }
            txtValue.post {
                txtValue.isSelected = true
            }
            mainLayout.addView(txtValue)

            val colonTxtParams = LayoutParams(0, LayoutParams.MATCH_PARENT)
            colonTxtParams.weight = 0.1f
            txtColon.apply {
                layoutParams = colonTxtParams
                gravity = Gravity.CENTER_VERTICAL
                text = ":"
            }
            mainLayout.addView(txtColon)


            val childParams = LayoutParams(0, LayoutParams.MATCH_PARENT)
            childParams.weight = 1f
            txtLable.apply {
                layoutParams = childParams
                gravity = Gravity.CENTER_VERTICAL
                text = stopData.key
                typeface = Typeface.DEFAULT_BOLD
            }
            mainLayout.addView(txtLable)
            addView(mainLayout)
        } else {

            val mainLayout = LinearLayout(context)
            val mainLayParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            mainLayParams.setMargins(resources.getDimension(R.dimen.dp_5).toInt(), resources.getDimension(R.dimen.dp_5).toInt(), resources.getDimension(R.dimen.dp_5).toInt(), resources.getDimension(R.dimen.dp_5).toInt())
            mainLayout.layoutParams = mainLayParams
            mainLayout.orientation = HORIZONTAL

            val txtLable = AppCompatTextView(context)
            val txtColon = AppCompatTextView(context)
            val txtValue = AppCompatTextView(context)

            val childParams = LayoutParams(0, LayoutParams.MATCH_PARENT)
            childParams.weight = 1f
            txtLable.apply {
                layoutParams = childParams
                gravity = Gravity.CENTER_VERTICAL
                text = stopData.key
                typeface = Typeface.DEFAULT_BOLD
            }
            mainLayout.addView(txtLable)

            val colonTxtParams = LayoutParams(0, LayoutParams.MATCH_PARENT)
            colonTxtParams.weight = 0.1f
            txtColon.apply {
                layoutParams = colonTxtParams
                gravity = Gravity.CENTER_VERTICAL
                text = ":"
            }
            mainLayout.addView(txtColon)
            val txtValueParams = LayoutParams(0, LayoutParams.MATCH_PARENT)
            txtValueParams.weight = 1f
            txtValue.apply {
                id = i
                layoutParams = txtValueParams
                text = stopData.value
                gravity = Gravity.CENTER_VERTICAL
                isSingleLine = true
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.MARQUEE
                marqueeRepeatLimit = -1
            }
            txtValue.post {
                txtValue.isSelected = true
            }
            mainLayout.addView(txtValue)
            addView(mainLayout)
        }

    }

}