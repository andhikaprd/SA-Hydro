package com.example.smartagro

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Logika untuk menampilkan notifikasi saat pesan FCM diterima
        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
    }

    private fun showNotification(title: String?, message: String?) {
        val channelId = "smart_agro_temp_alert"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Membuat Notification Channel untuk Android Oreo (8.0) ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Peringatan Suhu SmartAgro",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi untuk suhu kritis pada sistem SmartAgro"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent untuk membuka aplikasi saat notifikasi diklik
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Membangun notifikasi
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ganti dengan ikon yang sesuai
            .setContentTitle(title ?: "Peringatan SmartAgro")
            .setContentText(message ?: "Suhu air melebihi batas aman!")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        // Kirim token ke server backend/Firebase jika diperlukan untuk push spesifik ke device ini
        super.onNewToken(token)
    }
}
