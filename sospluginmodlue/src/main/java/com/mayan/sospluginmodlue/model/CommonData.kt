package com.mayan.sospluginmodlue.model

import android.app.ActivityManager
import android.content.Context
import com.mayan.sospluginmodlue.service.SOSService

class CommonData {
    /**
     * Returns true if this is a foreground service.
     *
     * @param context The [Context].
     */
    companion object {
        fun serviceIsRunningInForeground(context: Context): Boolean {
            val manager = context.getSystemService(
                    Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(
                    Integer.MAX_VALUE)) {
                if (SOSService::class.java.name == service.service.className) {
                    if (service.foreground) {
                        return true
                    }
                }
            }
            return false
        }
    }


}
