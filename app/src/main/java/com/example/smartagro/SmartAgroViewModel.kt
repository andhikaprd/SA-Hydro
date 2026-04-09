package com.example.smartagro

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.ViewModel
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

class SmartAgroViewModel : ViewModel() {

    private val _currentTemp = MutableStateFlow(25.1f)
    val currentTemp: StateFlow<Float> = _currentTemp.asStateFlow()

    // Logika Pelacakan Suhu Min dan Maks
    private val _minTemp = MutableStateFlow<Float?>(null)
    val minTemp: StateFlow<Float?> = _minTemp.asStateFlow()

    private val _maxTemp = MutableStateFlow<Float?>(null)
    val maxTemp: StateFlow<Float?> = _maxTemp.asStateFlow()

    private var currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // temperatureHistory untuk Sliding Window Grafik Real-time
    private val _temperatureHistory = MutableStateFlow<List<Float>>(emptyList())
    val temperatureHistory: StateFlow<List<Float>> = _temperatureHistory.asStateFlow()

    val tempStatus: StateFlow<String> = _currentTemp.map { temp ->
        when {
            temp < 18f -> "Terlalu Dingin"
            temp in 18f..24f -> "Normal"
            temp > 30f -> "Bahaya"
            temp > 24f -> "Panas"
            else -> "Unknown"
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Normal")

    private val _lastUpdated = MutableStateFlow("Baru saja")
    val lastUpdated: StateFlow<String> = _lastUpdated.asStateFlow()

    private val _alertCount = MutableStateFlow(2)
    val alertCount: StateFlow<Int> = _alertCount.asStateFlow()

    // Status riil hardware dari ESP32
    private val _peltierStatus = MutableStateFlow(false)
    val peltierStatus: StateFlow<Boolean> = _peltierStatus.asStateFlow()

    // Perintah saklar manual dari Android
    private val _forceCoolingFlag = MutableStateFlow(false)
    val forceCoolingFlag: StateFlow<Boolean> = _forceCoolingFlag.asStateFlow()

    // HEARTBEAT LOGIC
    private val _lastSeen = MutableStateFlow(System.currentTimeMillis())
    
    private val _isDeviceOnline = MutableStateFlow(true)
    val isDeviceOnline: StateFlow<Boolean> = _isDeviceOnline.asStateFlow()

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
            combine(_currentTemp, _forceCoolingFlag) { temp, manual ->
                temp > 28f || manual
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
                delay(3000) 
                val delta = if (_peltierStatus.value) -0.3f else 0.15f
                val nextTemp = (_currentTemp.value + delta + (Random.nextFloat() * 0.2f - 0.1f)).coerceIn(20f, 32f)
                
                _currentTemp.value = nextTemp
                
                // UPDATE MIN/MAX TEMP LOGIC
                val nowStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                if (nowStr != currentDate) {
                    currentDate = nowStr
                    _minTemp.value = nextTemp
                    _maxTemp.value = nextTemp
                } else {
                    _minTemp.value = if (_minTemp.value == null) nextTemp else minOf(_minTemp.value!!, nextTemp)
                    _maxTemp.value = if (_maxTemp.value == null) nextTemp else maxOf(_maxTemp.value!!, nextTemp)
                }

                // UPDATE HISTORY (SLIDING WINDOW)
                val currentHistory = _temperatureHistory.value.toMutableList()
                currentHistory.add(nextTemp)
                if (currentHistory.size > 20) {
                    currentHistory.removeAt(0)
                }
                _temperatureHistory.value = currentHistory
                
                if (nextTemp > 30f) {
                    _alertCount.value += 1
                }
                _lastUpdated.value = "Diperbarui 1 detik lalu"
            }
        }
    }

    // Fungsi Ekspor ke CSV
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
