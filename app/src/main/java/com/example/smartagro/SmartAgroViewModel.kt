package com.example.smartagro

import android.app.Application // Perlu untuk Context Notifikasi
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel // Diubah dari ViewModel ke AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// PERUBAHAN: Class sekarang menggunakan AndroidViewModel agar bisa membaca Context untuk notifikasi
class SmartAgroViewModel(application: Application) : AndroidViewModel(application) {

    // ==========================================
    // DEKLARASI ALAT NOTIFIKASI
    // ==========================================
    private val notificationHelper = NotificationHelper(application)

    // Variabel pintar agar notifikasi tidak spam berkali-kali
    private var hasNotifiedForHighTemp = false
    // ==========================================

    private val _currentTemp = MutableStateFlow(25.1f)
    val currentTemp: StateFlow<Float> = _currentTemp.asStateFlow()

    // Logika Pelacakan Suhu Min dan Maks
    private val _minTemp = MutableStateFlow<Float?>(22.1f)
    val minTemp: StateFlow<Float?> = _minTemp.asStateFlow()

    private val _maxTemp = MutableStateFlow<Float?>(27.3f)
    val maxTemp: StateFlow<Float?> = _maxTemp.asStateFlow()

    private var currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // temperatureHistory untuk Sliding Window Grafik Real-time
    private val _temperatureHistory = MutableStateFlow<List<Float>>(
        listOf(24.5f, 25.0f, 25.2f, 24.8f, 25.1f, 25.5f, 25.3f, 25.6f, 25.4f, 25.2f)
    )
    val temperatureHistory: StateFlow<List<Float>> = _temperatureHistory.asStateFlow()

    // PERUBAHAN: Status suhu disesuaikan dengan threshold alat baru yaitu 26 Derajat
    val tempStatus: StateFlow<String> = _currentTemp.map { temp ->
        when {
            temp < 18f -> "Terlalu Dingin"
            temp in 18f..<26f -> "Normal"
            temp >= 28f -> "Bahaya"
            temp >= 26f -> "Panas"
            else -> "Unknown"
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Normal")

    private val _lastUpdated = MutableStateFlow("Baru saja")
    val lastUpdated: StateFlow<String> = _lastUpdated.asStateFlow()

    private val _alertCount = MutableStateFlow(2)
    val alertCount: StateFlow<Int> = _alertCount.asStateFlow()

    // Status riil hardware dari ESP8266
    private val _peltierStatus = MutableStateFlow(false)
    val peltierStatus: StateFlow<Boolean> = _peltierStatus.asStateFlow()

    // Perintah saklar manual dari Android
    private val _forceCoolingFlag = MutableStateFlow(false)
    val forceCoolingFlag: StateFlow<Boolean> = _forceCoolingFlag.asStateFlow()

    // Mode Otomatis
    private val _autoModeEnabled = MutableStateFlow(true)
    val autoModeEnabled: StateFlow<Boolean> = _autoModeEnabled.asStateFlow()

    // HEARTBEAT LOGIC
    private val _lastSeen = MutableStateFlow(System.currentTimeMillis())

    private val _isDeviceOnline = MutableStateFlow(true)
    val isDeviceOnline: StateFlow<Boolean> = _isDeviceOnline.asStateFlow()

    // --- PROFILE DATA STATES (Persistent during App Lifecycle) ---
    private val _userName = MutableStateFlow("Bejo Morena")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userPhone = MutableStateFlow("+62 812 3456 7890")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _userEmail = MutableStateFlow("bejo.morena@gmail.com")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userLocation = MutableStateFlow("Pelaihari , Kalimantan Selatan")
    val userLocation: StateFlow<String> = _userLocation.asStateFlow()

    private val _userGardenName = MutableStateFlow("Kebun Selada Sejahtera")
    val userGardenName: StateFlow<String> = _userGardenName.asStateFlow()

    private val _profileImageUri = MutableStateFlow<Uri?>(null)
    val profileImageUri: StateFlow<Uri?> = _profileImageUri.asStateFlow()

    init {
        simulateTemperatureChange()
        observePeltierLogic()
        startHeartbeatMonitor()
        simulateHeartbeat()
    }

    private fun startHeartbeatMonitor() {
        viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val diff = now - _lastSeen.value
                _isDeviceOnline.value = diff < 30000
                delay(5000)
            }
        }
    }

    private fun observePeltierLogic() {
        viewModelScope.launch {
            combine(_currentTemp, _forceCoolingFlag, _autoModeEnabled) { temp, manual, auto ->
                // PERUBAHAN: Menyesuaikan suhu pendingin menyala di 26 Derajat
                (auto && temp >= 26f) || manual
            }.collect { shouldBeOn ->
                _peltierStatus.value = shouldBeOn
            }
        }
    }

    fun setForceCooling(active: Boolean) {
        viewModelScope.launch {
            _forceCoolingFlag.value = active
        }
    }

    fun setAutoMode(active: Boolean) {
        viewModelScope.launch {
            _autoModeEnabled.value = active
        }
    }

    // --- PROFILE UPDATES ---
    fun updateUserProfile(
        name: String,
        phone: String,
        email: String,
        location: String,
        garden: String
    ) {
        _userName.value = name
        _userPhone.value = phone
        _userEmail.value = email
        _userLocation.value = location
        _userGardenName.value = garden
    }

    fun updateProfileImage(uri: Uri?) {
        _profileImageUri.value = uri
    }

    private fun simulateHeartbeat() {
        viewModelScope.launch {
            while (true) {
                delay(10000)
                _lastSeen.value = System.currentTimeMillis()
            }
        }
    }

    private fun simulateTemperatureChange() {
        viewModelScope.launch {
            while (true) {
                // Interval simulasi sementara dipercepat agar kita bisa melihat notifikasi lebih cepat saat presentasi
                delay(5000L)

                val delta = if (_peltierStatus.value) -0.3f else 0.5f // Dipercepat naiknya agar kelihatan
                val nextTemp = (_currentTemp.value + delta + (Random.nextFloat() * 0.2f - 0.1f)).coerceIn(20f, 32f)

                _currentTemp.value = nextTemp

                // ==========================================
                // LOGIKA PEMICU NOTIFIKASI
                // ==========================================
                if (nextTemp >= 26f) { // Jika suhu 26 derajat atau lebih
                    if (!hasNotifiedForHighTemp) { // Dan jika belum dikirimi peringatan sebelumnya
                        notificationHelper.showTemperatureAlert(nextTemp) // 1. Kirim notifikasi
                        hasNotifiedForHighTemp = true                     // 2. Kunci (agar tidak spam berulang kali)
                    }
                } else if (nextTemp < 25f) {
                    // Jika suhu berhasil didinginkan turun ke bawah 25 derajat,
                    // buka kuncinya agar notifikasi bisa bunyi lagi jika suhu memanas di masa depan.
                    hasNotifiedForHighTemp = false
                }
                // ==========================================

                val nowStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                if (nowStr != currentDate) {
                    currentDate = nowStr
                    _minTemp.value = nextTemp
                    _maxTemp.value = nextTemp
                } else {
                    _minTemp.value = if (_minTemp.value == null) nextTemp else minOf(_minTemp.value!!, nextTemp)
                    _maxTemp.value = if (_maxTemp.value == null) nextTemp else maxOf(_maxTemp.value!!, nextTemp)
                }

                val currentHistory = _temperatureHistory.value.toMutableList()
                currentHistory.add(nextTemp)
                if (currentHistory.size > 20) {
                    currentHistory.removeAt(0)
                }
                _temperatureHistory.value = currentHistory

                if (nextTemp > 28f) { // Jika menembus 28 berarti masuk kategori insiden bahaya
                    _alertCount.value += 1
                }
                _lastUpdated.value = "Diperbarui ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())}"
            }
        }
    }

    fun exportHistoryToCSV(context: Context) {
        val history = _temperatureHistory.value
        if (history.isEmpty()) {
            Toast.makeText(context, "Tidak ada data untuk diekspor", Toast.LENGTH_SHORT).show()
            return
        }
        val csvHeader = "Waktu,Suhu Air (°C)\n"
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        var csvContent = csvHeader
        history.forEach { temp ->
            val timestamp = sdf.format(Date())
            csvContent += "$timestamp,${String.format(Locale.getDefault(), "%.1f", temp)}\n"
        }
        try {
            val fileName = "SmartAgro_Riwayat_Suhu.csv"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            FileOutputStream(file).use { fos ->
                fos.write(csvContent.toByteArray())
            }
            Toast.makeText(context, "File CSV berhasil disimpan di folder Downloads", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal mengekspor data: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}