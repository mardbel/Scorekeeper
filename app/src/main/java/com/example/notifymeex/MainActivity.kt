package com.example.notifymeex

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.NotificationCompat


class MainActivity : AppCompatActivity() {

    private lateinit var notifyMeButton: Button
    private lateinit var updateMeButton: Button
    private lateinit var cancelMeButton: Button
    private val PRIMARY_CHANNEL_ID: String = "primary_notification_channel"
    private lateinit var mNotificationManager: NotificationManager
    private val NOTIFICATION_ID = 0
    private val ACTION_UPDATE_NOTIFICATION = "com.example.notifymeex.ACTION_UPDATE_NOTIFICATION"
    private val mReceiver = NotificationReceiver()

    inner class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateNotification()
        }

    }

    private fun sendNotification() {
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val pendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT)
        val notifyBuilder = getNotificationBuilder()!!
        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build())
        notifyBuilder.addAction(R.drawable.ic_update, "update Notification", pendingIntent)
        setNotificationButtonState(false, true, true)
    }

    private fun updateNotification(){
        val androidBitMap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.mascot_1)
        val notifyBuilder = getNotificationBuilder()!!
        notifyBuilder.setStyle(NotificationCompat.BigPictureStyle()
            .bigPicture(androidBitMap)
            .setBigContentTitle("Notification Updated!"))
        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build())
        setNotificationButtonState(false, false, true)
    }

    private fun cancelNotification(){
        mNotificationManager.cancel(NOTIFICATION_ID)
        setNotificationButtonState(true, false, false)
    }

    private fun createNotificationChannel() {
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel("$PRIMARY_CHANNEL_ID", "Mascot Notification",
                    NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.description = "Notification from Mascot"
            channel.lightColor = Color.RED
            mNotificationManager.createNotificationChannel(channel)
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder? {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID,
                notificationIntent, FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(this, "$PRIMARY_CHANNEL_ID")
                .setContentTitle("You've been notified!")
                .setContentText("This is your application text.")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
    }

    private fun setNotificationButtonState(isNotifyEnabled: Boolean, isUpdateEnabled: Boolean, isCancelEnabled: Boolean){
        notifyMeButton.setEnabled(isNotifyEnabled)
        updateMeButton.setEnabled(isUpdateEnabled)
        cancelMeButton.setEnabled(isCancelEnabled)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notifyMeButton = findViewById(R.id.notify)
        notifyMeButton.setOnClickListener() {
            sendNotification()
        }
        createNotificationChannel()

        updateMeButton = findViewById(R.id.update)
        updateMeButton.setOnClickListener(){
            updateNotification()
        }

        cancelMeButton = findViewById(R.id.cancel)
        cancelMeButton.setOnClickListener(){
            cancelNotification()
        }
        setNotificationButtonState(true, false, false)

        registerReceiver(mReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION))
    }

    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        super.onDestroy()

    }
}