package com.adsonik.autodataenabler

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.rvalerio.fgchecker.AppChecker
import com.vijay.androidutils.Logger
import com.vijay.androidutils.PrefUtils


/**
 * Created by vijay-3593 on 24/12/17.
 */

class AlarmReceiver : BroadcastReceiver() {
    public val TAG = AlarmReceiver::class.java.simpleName
    override fun onReceive(context: Context, intent: Intent) {
        Logger.d(TAG, "onReceive called")
        MainActivity.scheduleTask(context)
        doYourWork(context)
    }

    fun doYourWork(context: Context) {
        val whiteListedApps = PrefUtils.getStringSet(context, MainActivity.PREF_WHITELISTED_APPS)
        val isMyAppRunning = isMyActivityRunning(context, whiteListedApps)
        if (isMyAppRunning) {
            enableWifi(context, true)
        } else {
            enableWifi(context, false)
        }
    }


    fun isMyActivityRunning(context: Context, packageInfo: Set<String>): Boolean {
        val packageName = getTopPackageName(context)
        if (packageName != null) {
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


    fun getTopPackageName(context: Context): String? {
        var topPackageName: String? = null
        /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val mUsageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        // We get usage stats for the last 10 seconds
        val stats = mUsageStatsManager.queryEvents(time - 1000 * 10, time)
        while (stats.hasNextEvent()) {
            val event = UsageEvents.Event()
            stats.getNextEvent(event)
            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                return event.packageName;
            }
        }
    }*/

        val appChecker = AppChecker()
        topPackageName = appChecker.getForegroundApp(context)
        return topPackageName
    }

    @SuppressLint("WifiManagerLeak", "WifiManagerPotentialLeak")
    fun enableWifi(context: Context, enable: Boolean) {
        var wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (enable) {
            if (!wifi.isWifiEnabled) {
                wifi.isWifiEnabled = true
                Logger.d(TAG, "Wifi Enabled")
            }
        } else {
            if (wifi.isWifiEnabled) {
                wifi.isWifiEnabled = false
                Logger.d(TAG, "Wifi Disabled")
            }
        }
    }
}
