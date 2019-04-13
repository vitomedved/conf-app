package com.example.confapp.event.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.confapp.NotificationUtils

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val service = Intent(context, EventNotificationService::class.java)

        service.putExtra("reason", intent.getStringExtra("reason"))

        val timestamp = intent.getLongExtra("timestamp", 0)

        service.putExtra(NotificationUtils.TAG_TIMESTAMP, intent.getLongExtra(NotificationUtils.TAG_TIMESTAMP, 0))
        service.putExtra(NotificationUtils.TAG_EVENT_ID, intent.getStringExtra(NotificationUtils.TAG_EVENT_ID))
        service.putExtra(NotificationUtils.TAG_EVENT_NAME, intent.getStringExtra(NotificationUtils.TAG_EVENT_NAME))
        service.putExtra(NotificationUtils.TAG_EVENT_LOCATION, intent.getStringExtra(NotificationUtils.TAG_EVENT_LOCATION))
        service.putExtra(NotificationUtils.TAG_CLICKED_FROM_NOTIFICATION, intent.getStringExtra(NotificationUtils.TAG_CLICKED_FROM_NOTIFICATION))

        EventNotificationService.enqueueWork(context, service)
    }

}