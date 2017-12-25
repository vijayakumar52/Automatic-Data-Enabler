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
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.vijay.androidutils.DialogUtils
import com.vijay.androidutils.PrefUtils
import com.vijay.androidutils.ToastUtils


/**
 * Created by vijay-3593 on 24/12/17.
 */


class MainActivity : AppCompatActivity(), AdapterView.OnItemLongClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val masterSwitch = bind<Switch>(this, R.id.masterSwitch)
        val conRadioGroup = bind<RadioGroup>(this, R.id.conRadioGrp)
        val btnPickApps = bind<TextView>(this, R.id.btnPickApps)
        val lvActiveApps = bind<ListView>(this, R.id.lvActiveApps)

        var curPreference = PrefUtils.getPrefValueInt(this, PREF_PREFERENCE);
        if (curPreference == -1) {
            curPreference = 0
        }
        if (curPreference == 0) {
            conRadioGroup.check(R.id.radioWifi)
        } else if (curPreference == 1) {
            conRadioGroup.check(R.id.radio2G)
        }
        masterSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                scheduleTask(this)
                val id = conRadioGroup.checkedRadioButtonId
                setCurrentPreference(id)
            } else {
                cancelTask(this)
            }
        }

        lvActiveApps.adapter = ActiveAppsListAdapter(this, getActiveAppsFromPref())
        lvActiveApps.emptyView = findViewById<TextView>(R.id.tvEmpty)

        btnPickApps.setOnClickListener { _ ->
            val intent = Intent(this, AppDisplay::class.java)
            intent.putStringArrayListExtra(EXTRA_RESULE, getActiveAppsFromPref())
            startActivityForResult(intent, REQUEST_CODE)
        }

        lvActiveApps.onItemLongClickListener = this
    }

    fun getActiveAppsFromPref(): ArrayList<String> {
        val list = ArrayList<String>()
        val storedVal = PrefUtils.getStringSet(this, PREF_WHITELISTED_APPS)
        if (storedVal != null) {
            list.addAll(storedVal)
        }
        return list
    }

    override fun onItemLongClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long): Boolean {
        if (p0 != null) {
            var adapter = p0.getAdapter() as ActiveAppsListAdapter
            val packName = adapter.getItem(p2)
            val title = resources.getString(R.string.title_remove_app)
            val content = resources.getString(R.string.title_remove_app_content)
            val posText = resources.getString(R.string.title_remove)
            val negText = resources.getString(R.string.title_cancel)
            DialogUtils.getInstance().twoButtonDialog(this, title, content, posText, negText, true, MaterialDialog.SingleButtonCallback { dialog, which ->
                if (which == DialogAction.POSITIVE) {
                    removeApp(packName)
                    ToastUtils.showToast(this, R.string.toast_app_removed)
                    val lvActiveApps = bind<ListView>(this, R.id.lvActiveApps)
                    lvActiveApps.adapter = ActiveAppsListAdapter(this, getActiveAppsFromPref())
                }

            }, null)
        }
        return false
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


    public companion object {
        fun scheduleTask(context: Context) {
            val interval = 5 * 5000
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            val pendingIntent = getAlarmPendingIntent(context)
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
        val REQUEST_CODE = 1010
        @JvmField
        val EXTRA_RESULE = "packages"
    }

    fun addApps(apps: Set<String>) {
        PrefUtils.putStringSet(this, PREF_WHITELISTED_APPS, apps)
    }

    fun removeApp(packName: String) {
        val storedList = PrefUtils.getStringSet(this, PREF_WHITELISTED_APPS);
        storedList.remove(packName)
        PrefUtils.putStringSet(this, PREF_WHITELISTED_APPS, storedList)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                val result = data.getStringArrayListExtra(EXTRA_RESULE)
                val listView = (findViewById<ListView>(R.id.lvActiveApps))
                listView.adapter = ActiveAppsListAdapter(this, result)

                val activeAppsSet = HashSet<String>()
                activeAppsSet.addAll(result)
                addApps(activeAppsSet)
            }
        }
    }
}
