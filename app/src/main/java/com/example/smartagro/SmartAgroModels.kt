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
 * Enum untuk menentukan tipe notifikasi dan warnanya di UI
 */
enum class NotificationType {
    WARNING, // Merah (Bahaya/Suhu Tinggi)
    INFO,    // Biru (Suhu Normal/Stabil)
    SYSTEM   // Hijau (Alat Terhubung/Berfungsi)
}

/**
 * Data class untuk menyimpan riwayat peringatan darurat (Alert) / Notifikasi.
 * Sudah di-upgrade untuk mendukung UI 3 Warna.
 */
@Keep
@IgnoreExtraProperties
data class AlertModel(
    val alertId: String = "", // Pakai String karena Firebase menggunakan Push Key (misal: -NO61xyz...)
    val timestamp: Long = 0L,
    val judul: String = "",
    val deskripsi: String = "",
    val type: String = "INFO", // Disimpan sebagai String di Firebase ("WARNING", "INFO", "SYSTEM")
    val value: String = "",    // Opsional: Untuk menampilkan angka, misal "28.5°C"
    val isRead: Boolean = false
)