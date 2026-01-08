package com.onepaytaxi.driver

import androidx.appcompat.app.AppCompatActivity


open class BaseActivity : AppCompatActivity() {


     companion object {
        @JvmStatic
        var lastInteractionTime = System.currentTimeMillis()

    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        lastInteractionTime = System.currentTimeMillis()
        println("user interacted $lastInteractionTime")
    }



}