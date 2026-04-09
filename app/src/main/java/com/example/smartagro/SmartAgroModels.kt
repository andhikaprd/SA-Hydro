package com.example.smartagro

import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties

/**
 * Data class untuk profil pengguna SmartAgro.
 * @Keep digunakan agar class tidak di-obfuscate oleh ProGuard sehingga Firebase dapat memetakan datanya.
 */
@Keep
@IgnoreExtraProperties
data class UserModel(
    val uid: String = "",
    val namaLengkap: String = "",
    val email: String = "",
    val nomorHp: String = "",
    val lokasi: String = "",
    val namaKebun: String = ""
)

/**
 * Data class untuk riwayat log suhu air nutrisi.
 */
@Keep
@IgnoreExtraProperties
data class TemperatureLog(
    val logId: String = "",
    val timestamp: Long = 0L,
    val suhuAir: Float = 0f,
    val kondisi: String = "" // Contoh: "Aman", "Waspada", "Bahaya"
)

/**
 * Data class untuk menyimpan riwayat peringatan darurat (Alert).
 */
@Keep
@IgnoreExtraProperties
data class AlertModel(
    val alertId: String = "",
    val timestamp: Long = 0L,
    val judul: String = "",
    val deskripsi: String = "",
    val isRead: Boolean = false
)
