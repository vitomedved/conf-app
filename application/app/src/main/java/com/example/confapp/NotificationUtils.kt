package com.example.confapp

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import com.example.confapp.event.notification.AlarmReceiver
import com.example.confapp.model.CEvent
import java.util.*



class NotificationUtils {

    companion object {
        const val TAG_REASON = "reason"
        const val TAG_TIMESTAMP = "timestamp"
        const val TAG_EVENT_ID = "eventId"
        const val TAG_EVENT_NAME = "eventName"
        const val TAG_EVENT_LOCATION = "eventLocation"
        const val TAG_MESSAGE = "message"
        const val TAG_NOTIFICATION = "notification"
        const val TAG_TITLE = "title"
        const val TAG_CLICKED_FROM_NOTIFICATION = "clickedFromNotification"
    }

    fun setNotification(timeInMilliSeconds: Long, event: CEvent, activity: Activity) {
        if (timeInMilliSeconds > 0) {

            val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent().setClass(activity.applicationContext, AlarmReceiver::class.java)

            alarmIntent.putExtra(TAG_REASON, "notification")
            alarmIntent.putExtra(TAG_TIMESTAMP, timeInMilliSeconds)
            alarmIntent.putExtra(TAG_EVENT_ID, event.id)
            alarmIntent.putExtra(TAG_EVENT_NAME, event.name)
            alarmIntent.putExtra(TAG_EVENT_LOCATION, event.hall)
            alarmIntent.putExtra(TAG_CLICKED_FROM_NOTIFICATION, true)

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMilliSeconds

            val pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        }
    }
}