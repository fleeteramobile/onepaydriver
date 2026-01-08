package com.onepaytaxi.driver

import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_map_zoom.*
import kotlin.math.max
import kotlin.math.min


class MapZoomAct : BaseActivity() {
    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var mScaleFactor = 1.0f
    private lateinit var ivImageZoom:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       /* Fresco.initialize(this)*/
        setContentView(R.layout.activity_map_zoom)

         ivImageZoom = findViewById(R.id.photoDrawView)
        mScaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
   //     val imageViewTarget = DrawableImageViewTarget(giff)
       /* Glide.with(this)
                .load(R.raw.loading_anim)
                .into(imageViewTarget)*/
        if (intent != null && intent.getStringExtra("IMAGE_URI") != null) {
            val imageUrl = intent.getStringExtra("IMAGE_URI")
            println("MapZoomAct ____$imageUrl")
            imageUrl?.run {
            //   photoDrawView.setPhotoUri(Uri.parse(imageUrl), this@MapZoomAct, loadingLayout, tool_bar_lay)

            }
            Glide.with(this)
                    .load(Uri.parse(imageUrl))
                    .into(ivImageZoom)
        }
    }



    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        if (motionEvent != null) {
            mScaleGestureDetector?.onTouchEvent(motionEvent)
        }
        return true
    }



    override fun onResume() {
        super.onResume()
        backIcon.setOnClickListener {
            finish()
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = max(0.1f, min(mScaleFactor, 10.0f))
            ivImageZoom.scaleX = mScaleFactor
            ivImageZoom.scaleY = mScaleFactor
            return true
        }
    }
}
