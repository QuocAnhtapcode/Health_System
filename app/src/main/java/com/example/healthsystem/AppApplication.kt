package com.example.healthsystem

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "voice_channel", // Channel ID khớp với service
            "Voice Recognition",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Kênh dành cho dịch vụ nhận diện giọng nói"
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
