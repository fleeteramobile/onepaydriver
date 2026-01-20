package com.onepaytaxi.driver.pushmessage


import android.app.Activity
import android.app.Application
import android.os.Bundle

object AppVisibility : Application.ActivityLifecycleCallbacks {

    private var activityReferences = 0
    private var isChangingConfigurations = false

    fun isAppInForeground(): Boolean {
        return activityReferences > 0 && !isChangingConfigurations
    }

    override fun onActivityStarted(activity: Activity) {
        activityReferences++
    }

    override fun onActivityStopped(activity: Activity) {
        isChangingConfigurations = activity.isChangingConfigurations
        activityReferences--
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
