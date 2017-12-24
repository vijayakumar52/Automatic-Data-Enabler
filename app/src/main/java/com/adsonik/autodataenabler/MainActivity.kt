package com.adsonik.autodataenabler

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.vijay.androidutils.PrefUtils
import com.vijay.androidutils.ToastUtils


/**
 * Created by vijay-3593 on 24/12/17.
 */


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val masterSwitch = bind<Switch>(this, R.id.masterSwitch)
        val conRadioGroup = bind<RadioGroup>(this, R.id.conRadioGrp)
        val btnPickApps = bind<Button>(this, R.id.btnPickApps)
        val lvActiveApps = bind<ListView>(this, R.id.lvActiveApps)
        val btnActivate = bind<Button>(this, R.id.btnActivate)

        masterSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                scheduleTask(this)
                val id = conRadioGroup.checkedRadioButtonId
                setCurrentPreference(id)
            } else {
                cancelTask(this)
            }
        }

        val activeApps = PrefUtils.getStringSet(this, PREF_WHITELISTED_APPS)
        val activeAppsList = ArrayList<String>()
        if (activeApps != null) {
            activeAppsList.addAll(activeApps)
        }
        lvActiveApps.adapter = ActiveAppsListAdapter(this, activeAppsList)
        lvActiveApps.emptyView = findViewById<TextView>(R.id.tvEmpty)

        btnActivate.setOnClickListener { view ->
            val activeAppsAdapter = lvActiveApps.adapter as ActiveAppsListAdapter
            val allItems = activeAppsAdapter.allItems
            if (allItems.size > 0) {
                val activeAppsSet = HashSet<String>()
                activeAppsSet.addAll(allItems)
                addApps(activeAppsSet)
            } else {
                ToastUtils.showToast(this, R.string.toast_please_select_atlease_one_app)
            }
        }
    }

    fun <T : View> bind(activity: Activity, @IdRes res: Int): T {
        @Suppress("UNCHECKED_CAST")
        return activity.findViewById(res) as T
    }

    fun setCurrentPreference(id: Int) {
        var preference = 0;
        if (R.id.radioWifi == id) {
            preference = 0
        } else if (R.id.radio2G == id) {
            preference = 1;
        }
        PrefUtils.setPrefValueInt(this, PREF_PREFERENCE, preference);
    }


    companion object {
        fun scheduleTask(context: Context) {
            val interval = 60 * 1000
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            val pendingIntent = getAlarmPendingIntent(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, pendingIntent)
            }
            ToastUtils.showToast(context, R.string.alarm_scheduled)
        }

        fun getAlarmPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent
        }

        fun cancelTask(context: Context) {
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(getAlarmPendingIntent(context))
            ToastUtils.showToast(context, R.string.alarm_canceled)
        }

        val PREF_WHITELISTED_APPS: String = "whiteListedApps"
        val PREF_PREFERENCE: String = "preference"
    }

    fun addApps(apps: Set<String>) {
        PrefUtils.putStringSet(this, PREF_WHITELISTED_APPS, apps)
    }
}
