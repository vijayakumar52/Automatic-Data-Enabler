package com.adsonik.autodataenabler

import android.annotation.SuppressLint
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.support.annotation.RequiresApi
import com.vijay.androidutils.Logger
import com.vijay.androidutils.PrefUtils


/**
 * Created by vijay-3593 on 24/12/17.
 */

class AlarmReceiver : BroadcastReceiver() {
    val TAG = AlarmReceiver::class.java.simpleName
    var context: Context? = null
    override fun onReceive(context: Context, intent: Intent) {
        Logger.d(TAG, "onReceive called")
        this.context = context
        doYourWork()
        MainActivity.scheduleTask(context)
    }

    fun doYourWork() {
        val whiteListedApps = PrefUtils.getStringSet(this.context, MainActivity.PREF_WHITELISTED_APPS)
        val isMyAppRunning = isMyActivityRunning(whiteListedApps)
        if (isMyAppRunning) {
            enableWifi(true)
        } else {
            enableWifi(false)
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun isMyActivityRunning(packageInfo: Set<String>): Boolean {
        val currentTime = System.currentTimeMillis()
        val usageStatsManager = context?.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val queryEvents = usageStatsManager.queryEvents(currentTime - 30000, currentTime) as UsageEvents

        while (queryEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            queryEvents.getNextEvent(event)
            val packageName: String = event.packageName
            val iterator: Iterator<String> = packageInfo.iterator()
            while (iterator.hasNext()) {
                val whiteListedName = iterator.next();
                if (whiteListedName.equals(packageName)) {
                    return true
                }
            }
        }
        return false
    }

    @SuppressLint("WifiManagerLeak")
    fun enableWifi(enable: Boolean) {
        var wifi = this.context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (enable) {
            if (!wifi.isWifiEnabled) {
                wifi.isWifiEnabled = true
            }
        } else {
            if (wifi.isWifiEnabled) {
                wifi.isWifiEnabled = false
            }
        }
    }
}
