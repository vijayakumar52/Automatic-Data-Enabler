package com.adsonik.autodataenabler

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import java.util.*

/**
 * Created by vijay-3593 on 24/12/17.
 */

object NotificationUtils {

    fun showSilentNotification(context: Context, title: String, content: String, sIcon: Int, lIcon: Bitmap, id: Int,
                               intent: Intent, showIndeterminateProgress: Boolean, persistent: Boolean) : Notification{
        var channel: NotificationChannel? = null
        val channelID = context.packageName + ".noti.channel.silent" //No i18N
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.resources.getString(R.string.ui_notification_low_priority)
            val notificationChannel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(channelID,
                        channelName, NotificationManager.IMPORTANCE_LOW)
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            notificationChannel.enableLights(false)
            notificationChannel.setShowBadge(false)

            channel = notificationChannel
        }
        return showNotification(context, title, content, sIcon, lIcon, id, intent, showIndeterminateProgress, false, channelID, channel, persistent)

    }

    fun showBadgeNotification(context: Context, title: String, content: String, sIcon: Int,
                              lIcon: Bitmap?, id: Int, intent: Intent, showIndeterminateProgress: Boolean, persistent: Boolean) : Notification {
        var channel: NotificationChannel? = null
        val channelID = context.packageName + "noti.channel.badge" //No i18N
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.resources.getString(R.string.ui_notification_default)
            val notificationChannel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(channelID,
                        channelName, NotificationManager.IMPORTANCE_HIGH)
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            notificationChannel.enableLights(true)
            notificationChannel.setShowBadge(true)

            channel = notificationChannel
        }
        return showNotification(context, title, content, sIcon, lIcon, id, intent, showIndeterminateProgress, true, channelID, channel, persistent)
    }

    fun showNotification(context: Context, title: String?, content: String?, sIcon: Int, lIcon: Bitmap?, id: Int, intent: Intent?,
                         showIndeterminateProgress: Boolean, enableSound: Boolean, channelID: String, channel: NotificationChannel?, persistent: Boolean) : Notification{

        val notificationBuilder = NotificationCompat.Builder(context, channelID)
        notificationBuilder
                .setSmallIcon(sIcon)
                .setColor(Color.parseColor("#4190F2")) //No i18N
                .setAutoCancel(true)

        if (intent != null) {
            val pIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_ONE_SHOT)
            notificationBuilder.setContentIntent(pIntent)
        }

        if (title != null) {
            notificationBuilder.setContentTitle(title)
        }
        if (enableSound) {
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            notificationBuilder.setSound(defaultSoundUri)
        }
        if (content != null) {
            notificationBuilder.setContentText(content)
        }

        notificationBuilder.setOngoing(persistent)

        if (lIcon != null) {
            notificationBuilder.setLargeIcon(lIcon)
        }
        if (showIndeterminateProgress) {
            notificationBuilder.setProgress(0, 0, true)
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel!!)
        }
        val notification = notificationBuilder.build()
        notificationManager.notify(id, notification)
        return notification
    }

    fun showNotification(context: Context, title: String, content: String,
                         sIcon: Int, id: Int, intent: Intent, persistent: Boolean) : Notification{
        return showBadgeNotification(context, title, content, sIcon, null, id, intent, false, persistent)
    }


    fun cancelNotification(context: Context, notifyId: Int) {
        val ns = Context.NOTIFICATION_SERVICE
        val notifyManager = context.getSystemService(ns) as NotificationManager
        notifyManager.cancel(notifyId)
    }

    /*
    public static void cancelAllNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.getActiveNotifications();
        notificationManager.cancelAll();
    }*/

    fun cancelNotifications(context: Context, ids: ArrayList<Int>) {
        for (i in ids.indices) {
            cancelNotification(context, ids[i])
        }
    }
}
