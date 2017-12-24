package com.adsonik.autodataenabler

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.IBinder
import com.vijay.androidutils.PrefUtils
import com.vijay.androidutils.ToastUtils
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * Created by vijay-3593 on 24/12/17.
 */

class RunningTaskValidator : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val title = this.resources.getString(R.string.noti_title)
        val content = this.resources.getString(R.string.noti_content)
        val bitmap = BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher)
        val intent = Intent(this, MainActivity::class.java)
        val id = 12345
        val notification = NotificationUtils.showSilentNotification(this, title, content, R.mipmap.ic_launcher, bitmap, id, intent, false, true)
        startForeground(id, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        doYourWork()
        MainActivity.scheduleTask(this)
        return super.onStartCommand(intent, flags, startId)
    }

    fun doYourWork() {
        val preference = PrefUtils.getPrefValueInt(this, MainActivity.PREF_PREFERENCE)
        val whiteListedApps = PrefUtils.getStringSet(this, MainActivity.PREF_WHITELISTED_APPS)
        val isMyAppRunning = isMyActivityRunning(whiteListedApps)
        if (preference == 0) {
            enableWifi(isMyAppRunning)
        } else if (preference == 1) {
            enableMobileData(isMyAppRunning)
        }
    }


    fun isMyActivityRunning(packageInfo: Set<String>): Boolean {
        val activityManager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val foregroundTaskInfo = activityManager.getRunningTasks(1)[0]

        val iterator = packageInfo.iterator()
        while (iterator.hasNext()) {
            val packageName = iterator.next()
            if (packageName.equals(foregroundTaskInfo.topActivity.packageName)) {
                return true
            }
        }
        return false
    }

    fun enableWifi(isActive: Boolean) {
        var wifi = this.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (isActive) {
            if (!wifi.isWifiEnabled()) {
                wifi.setWifiEnabled(true)
                ToastUtils.showToast(this, R.string.toast_wifi_enabled)
            }
        } else {
            if (wifi.isWifiEnabled()) {
                wifi.setWifiEnabled(false)
                ToastUtils.showToast(this, R.string.toast_wifi_disabled)
            }
        }
    }

    fun enableMobileData(isActive: Boolean) {
        var conManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mobile = conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        if (isActive) {
            if (!mobile.isConnected()) {
                setMobileData(true)
                ToastUtils.showToast(this, R.string.toast_mobiledata_enabled)
            }
        } else {
            if (mobile.isConnected()) {
                setMobileData(false)
                ToastUtils.showToast(this, R.string.toast_mobiledata_disabled)
            }
        }

    }

    private fun setMobileData(enabled: Boolean) {
        var conmanClass: Class<*>? = null
        var conManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
