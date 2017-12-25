package com.adsonik.autodataenabler

import android.annotation.SuppressLint
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.support.annotation.RequiresApi
import com.vijay.androidutils.Logger
import com.vijay.androidutils.PrefUtils
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


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
        val preference = PrefUtils.getPrefValueInt(this.context, MainActivity.PREF_PREFERENCE)
        val whiteListedApps = PrefUtils.getStringSet(this.context, MainActivity.PREF_WHITELISTED_APPS)
        val isMyAppRunning = isMyActivityRunning(whiteListedApps)
        if (isMyAppRunning) {
            if (preference == 0) {
                enableWifi(true)
            } else if (preference == 1) {
                enableMobileData(true)
            }
        } else {
            enableWifi(false)
            enableMobileData(false)
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

    fun enableMobileData(enable: Boolean) {
        var conManager = this.context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mobile = conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        if (enable) {
            if (!mobile.isConnected) {
                setMobileData(true)
            }
        } else {
            if (mobile.isConnected) {
                setMobileData(false)
            }
        }
    }

    private fun setMobileData(enabled: Boolean) {
        var conmanClass: Class<*>? = null
        var conManager = this.context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try {
            conmanClass = Class.forName(conManager.javaClass.getName())
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        var iConnectivityManagerField: Field? = null
        try {
            iConnectivityManagerField = conmanClass!!.getDeclaredField("mService")
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }

        iConnectivityManagerField!!.isAccessible = true
        var iConnectivityManager: Any? = null
        try {
            iConnectivityManager = iConnectivityManagerField.get(conManager)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        var iConnectivityManagerClass: Class<*>? = null
        try {
            iConnectivityManagerClass = Class.forName(iConnectivityManager!!.javaClass.name)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        var setMobileDataEnabledMethod: Method? = null
        try {
            setMobileDataEnabledMethod = iConnectivityManagerClass!!.getDeclaredMethod("setMobileDataEnabled", java.lang.Boolean.TYPE)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }

        setMobileDataEnabledMethod!!.isAccessible = true

        try {
            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

}
