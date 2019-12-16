package com.example.confapp.event.notification

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.JobIntentService
import android.util.Log
import com.example.confapp.NotificationUtils
import com.example.confapp.R
import com.example.confapp.event.EventScrollingActivity
import com.example.confapp.model.CEvent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*



@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EventNotificationService : JobIntentService() {

    private lateinit var mNotification: Notification
    private val mNotificationId: Int = 1000

    @SuppressLint("NewApi")
    private fun createChannel() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library

            val context = this.applicationContext
            val notificationManager : NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.parseColor("#e8334a")
            notificationChannel.description = getString(R.string.event_notification_channel_description)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    companion object {
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, EventNotificationService::class.java, 404, work)
        }

        const val CHANNEL_ID = "com.example.confapp.event.notification.CHANNEL_ID"
        const val CHANNEL_NAME = "Event start time"
    }

    override fun onHandleWork(intent: Intent) {

        //Create Channel
        createChannel()


        var timestamp: Long = 0
        if (intent.extras != null) {
            timestamp = intent.extras!!.getLong("timestamp")
        }

        if (timestamp > 0) {

            val context = this.applicationContext
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notifyIntent = Intent(this, EventScrollingActivity::class.java)

            val evtId = intent.extras.getString(NotificationUtils.TAG_EVENT_ID)

            val eventName = intent.extras.getString(NotificationUtils.TAG_EVENT_NAME)
            val eventLocation = intent.extras.getString(NotificationUtils.TAG_EVENT_LOCATION)

            val title = "Event starting soon!"
            val message = "Event $eventName is starting in 1 hour!\n" +
                    "Location of event: $eventLocation"

            notifyIntent.putExtra(NotificationUtils.TAG_TITLE, title)
            notifyIntent.putExtra(NotificationUtils.TAG_MESSAGE, message)
            notifyIntent.putExtra(NotificationUtils.TAG_NOTIFICATION, true)
            notifyIntent.putExtra(NotificationUtils.TAG_EVENT_ID, evtId)
            notifyIntent.putExtra(NotificationUtils.TAG_CLICKED_FROM_NOTIFICATION, true)

            notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp

            val pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val res = this.resources
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mNotification = Notification.Builder(this, CHANNEL_ID)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_event_red)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setStyle(Notification.BigTextStyle()
                        .bigText(message))
                    .setContentText(message).build()
            } else {

                mNotification = Notification.Builder(this)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_event_red)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(title)
                    .setStyle(Notification.BigTextStyle()
                        .bigText(message))
                    .setSound(uri)
                    .setContentText(message).build()

            }
            // mNotificationId is a unique int for each notification that you must define
            notificationManager.notify(mNotificationId, mNotification)
        }
    }
}