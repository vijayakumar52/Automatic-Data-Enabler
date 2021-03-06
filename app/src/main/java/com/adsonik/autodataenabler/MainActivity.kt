package com.adsonik.autodataenabler

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlarmManager
import android.app.AppOpsManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.Spanned
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Switch
import android.widget.TextView
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
        val btnPickApps = bind<TextView>(this, R.id.btnPickApps)
        val lvActiveApps = bind<ListView>(this, R.id.lvActiveApps)

        lvActiveApps.adapter = ActiveAppsListAdapter(this, getActiveAppsFromPref())
        lvActiveApps.emptyView = findViewById<TextView>(R.id.tvEmpty)
        lvActiveApps.onItemLongClickListener = this

        validateCheckbox()

        masterSwitch.setOnClickListener { view ->
            if (masterSwitch.isChecked) {
                val activeApps = PrefUtils.getStringSet(this, PREF_WHITELISTED_APPS)
                if (activeApps != null) {
                    if (!needsUsageStatsPermission()) {
                        scheduleTask(this)
                        ToastUtils.showToast(this, R.string.alarm_scheduled)
                    } else {
                        val title = resources.getString(R.string.title_permission_not_available)
                        var content = resources.getString(R.string.dialog_content_permission_not_available).toSpanned()
                        val posText = resources.getString(R.string.title_open_settings).toUpperCase()
                        val negText = resources.getString(R.string.title_cancel)
                        DialogUtils.getInstance().twoButtonDialog(this, title, content, posText, negText, false, { dialog, which ->
                            if (which == DialogAction.POSITIVE) {
                                requestUsageStatsPermission()
                            } else {
                                validateCheckbox()
                            }
                        }, {
                            validateCheckbox()
                        })
                    }
                } else {
                    validateCheckbox()
                    ToastUtils.showToast(this, R.string.ui_select_atleast_one_apps)
                }
            } else {
                cancelTask(this)
                ToastUtils.showToast(this, R.string.alarm_canceled)
            }
        }

        btnPickApps.setOnClickListener { _ ->
            val intent = Intent(this, AppDisplay::class.java)
            intent.putStringArrayListExtra(EXTRA_RESULE, getActiveAppsFromPref())
            startActivityForResult(intent, REQUEST_CODE)
        }

    }

    fun String.toSpanned(): Spanned {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            return Html.fromHtml(this)
        }
    }

    fun validateCheckbox() {
        val masterSwitch = bind<Switch>(this, R.id.masterSwitch)
        val status: Boolean = PrefUtils.getPrefValueBoolean(this, PREF_STATUS)
        masterSwitch.isChecked = status
    }

    private fun needsUsageStatsPermission(): Boolean {
        return postLollipop() && !hasUsageStatsPermission(this)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun requestUsageStatsPermission() {
        if (!hasUsageStatsPermission(this)) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    private fun postLollipop(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.packageName)
        return mode == AppOpsManager.MODE_ALLOWED
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


    companion object {
        fun scheduleTask(context: Context) {
            val interval = 5 * 1000
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            val pendingIntent = getAlarmPendingIntent(context)
            alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + interval, pendingIntent)
            PrefUtils.setPrefValueBoolean(context, PREF_STATUS, true)
        }

        fun getAlarmPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent
        }

        fun cancelTask(context: Context) {
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(getAlarmPendingIntent(context))
            PrefUtils.setPrefValueBoolean(context, PREF_STATUS, false)
        }

        val PREF_WHITELISTED_APPS: String = "whiteListedApps"
        val PREF_STATUS: String = "status"
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
