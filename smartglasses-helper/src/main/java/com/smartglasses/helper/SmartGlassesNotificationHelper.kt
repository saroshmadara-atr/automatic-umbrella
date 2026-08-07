// SPDX-License-Identifier: AGPL-3.0-or-later
// Based on NearbyGlasses by yjeanrenaud (https://github.com/yjeanrenaud/yj_nearbyglasses)

package com.smartglasses.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class SmartGlassesNotificationHelper(
    private val context: Context,
    private val detectionChannelName: String = "Smart Glasses Detection Alerts",
    private val serviceChannelName: String = "Background Scanning Service",
    private val smallIconResId: Int,
    private val scanningIconResId: Int,
    private val tapTargetActivity: Class<*>
) {

    companion object {
        const val CHANNEL_ID_DETECTION = "glasses_detection"
        const val CHANNEL_ID_SERVICE = "glasses_service"
        const val NOTIFICATION_ID_DETECTION = 1001
        const val NOTIFICATION_ID_SERVICE = 1002
    }

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val detectionChannel = NotificationChannel(
                CHANNEL_ID_DETECTION,
                detectionChannelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when smart glasses are detected nearby"
                enableVibration(true)
                setShowBadge(true)
            }

            val serviceChannel = NotificationChannel(
                CHANNEL_ID_SERVICE,
                serviceChannelName,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when the app is scanning in the background"
                setShowBadge(false)
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(detectionChannel)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    fun showDetectionNotification(event: SmartGlassesEvent) {
        val intent = Intent(context, tapTargetActivity).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val deviceName = event.deviceName ?: "Unknown device"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_DETECTION)
            .setSmallIcon(smallIconResId)
            .setContentTitle("Smart Glasses Detected")
            .setContentText("$deviceName detected (RSSI: ${event.rssi} dBm)")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Device: $deviceName\n" +
                            "RSSI: ${event.rssi} dBm\n" +
                            "Reason: ${event.detectionReason}\n" +
                            "Company: ${event.companyName}"
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(NOTIFICATION_ID_DETECTION, notification)
    }

    fun createServiceNotification(): Notification {
        val intent = Intent(context, tapTargetActivity)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID_SERVICE)
            .setSmallIcon(scanningIconResId)
            .setContentTitle("Scanning for smart glasses nearby")
            .setContentText("Background scanning is active")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }
}
