package com.adsonik.autodataenabler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by vijay-3593 on 24/12/17.
 */

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, RunningTaskValidator::class.java)
        context.startService(serviceIntent)
    }
}
