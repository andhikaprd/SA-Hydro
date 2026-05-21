package com.example.smartagro

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import android.Manifest

class NotificationHelper(private val context: Context) {
    // ID khusus untuk jalur notifikasi SmartAgro
    private val channelId = "smartagro_alerts"
    private val notificationId = 101

    init {
        createNotificationChannel()
    }

    // Wajib untuk Android 8 (Oreo) ke atas
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Peringatan Suhu Air"
            val descriptionText = "Notifikasi saat suhu air nutrisi melewati batas 26°C"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Fungsi ini yang akan dipanggil saat suhu panas
    fun showTemperatureAlert(temp: Float) {
        // Cek apakah user sudah memberi izin notifikasi (Wajib untuk Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return // Batalkan memunculkan notifikasi jika belum diizinkan
            }
        }

        // Merakit bentuk notifikasi
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert) // Ikon peringatan bawaan Android
            .setContentTitle("Peringatan Suhu! 🔥")
            .setContentText("Suhu air mencapai $temp°C. Pendingin otomatis diaktifkan.")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioritas tinggi agar muncul pop-up di atas layar
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000)) // Getaran panjang
            .setAutoCancel(true)

        // Menampilkan notifikasi ke layar HP
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }
}