package com.dev007.livelocationtracker

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class LocationService : Service() {

    //SupervisorJob - if one job in this scope fails, it will make sure others to keep running.
    //Dispatchers.IO - because we making API CALSS.
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var locationClient: LocationClient

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        isMyServiceRunning = true
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, LocationApp.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Tracking Location...")
            .setContentText("Location null")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setDefaults(Notification.DEFAULT_ALL)//Set ringtone and vibrations - you can use
            .setPriority(NotificationCompat.PRIORITY_MAX)/*Heads-up Notifications - Will work after the re-install working for me! :(*/
            .setOngoing(true)/* can't swipe it away */

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient
            .getLocationUpdates(10000L/*10 sec*/)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->

                val lat = location.latitude.toString().takeLast(3)
                val long = location.longitude.toString().takeLast(3)
                val updatedNotification = notification.setContentText(
                    "Location: ($lat , $long)"
                )
                notificationManager.notify(NOTIFICATION_ID, updatedNotification.build())
            }
            .launchIn(serviceScope)
        startForeground(NOTIFICATION_ID, notification.build())
    }

    private fun stop() {
        stopForeground(true/*Which makes sure to remove the notification*/)
        stopSelf()//will stop the service
    }

    override fun onDestroy() {
        super.onDestroy()
        isMyServiceRunning = false
        serviceScope.cancel()
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        var isMyServiceRunning by mutableStateOf(false)
    }
}