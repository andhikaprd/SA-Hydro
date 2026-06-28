package com.example.smartagro

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SmartAgroViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationHelper = NotificationHelper(application)
    private var hasNotifiedForHighTemp = false

    // ==========================================
    // STATE UNTUK KEAMANAN (SINGLE DEVICE LOGIN)
    // ==========================================
    private val _forceLogout = MutableStateFlow(false)
    val forceLogout: StateFlow<Boolean> = _forceLogout.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _currentTemp = MutableStateFlow(25.1f)
    val currentTemp: StateFlow<Float> = _currentTemp.asStateFlow()

    private val _minTemp = MutableStateFlow<Float?>(22.1f)
    val minTemp: StateFlow<Float?> = _minTemp.asStateFlow()

    private val _maxTemp = MutableStateFlow<Float?>(27.3f)
    val maxTemp: StateFlow<Float?> = _maxTemp.asStateFlow()

    private var currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private val _temperatureHistory = MutableStateFlow<List<Float>>(emptyList())
    val temperatureHistory: StateFlow<List<Float>> = _temperatureHistory.asStateFlow()

    val tempStatus: StateFlow<String> = _currentTemp.map { temp ->
        when {
            temp < 18f -> "Terlalu Dingin"
            temp in 18f..<26f -> "Normal"
            temp >= 28f -> "Bahaya"
            temp >= 26f -> "Panas"
            else -> "Unknown"
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Normal")

    private val _lastUpdated = MutableStateFlow("Menunggu data...")
    val lastUpdated: StateFlow<String> = _lastUpdated.asStateFlow()

    private val _alertCount = MutableStateFlow(0)
    val alertCount: StateFlow<Int> = _alertCount.asStateFlow()

    private val _peltierStatus = MutableStateFlow(false)
    val peltierStatus: StateFlow<Boolean> = _peltierStatus.asStateFlow()

    private val _forceCoolingFlag = MutableStateFlow(false)
    val forceCoolingFlag: StateFlow<Boolean> = _forceCoolingFlag.asStateFlow()

    private val _autoModeEnabled = MutableStateFlow(true)
    val autoModeEnabled: StateFlow<Boolean> = _autoModeEnabled.asStateFlow()

    private val _lastSeen = MutableStateFlow(System.currentTimeMillis())
    private val _isDeviceOnline = MutableStateFlow(false)
    val isDeviceOnline: StateFlow<Boolean> = _isDeviceOnline.asStateFlow()

    // --- PROFILE DATA STATES ---
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
        startHeartbeatMonitor()
        fetchNotifications()
        fetchMonitoringData()
        fetchControlData()
    }

    // ==========================================
    // FUNGSI MATA-MATA (MONITOR DEVICE SESSION)
    // ==========================================
    fun monitorDeviceSession(userId: String, context: Context) {
        val currentDeviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val database = FirebaseDatabase.getInstance("https://smartagro-v1-default-rtdb.asia-southeast1.firebasedatabase.app").reference

        database.child("users").child(userId).child("device_id").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val firebaseDeviceId = snapshot.getValue(String::class.java)
                if (firebaseDeviceId != null && firebaseDeviceId != currentDeviceId) {
                    _forceLogout.value = true
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun resetLogoutState() {
        _forceLogout.value = false
    }

    // ==========================================
    // FORMAT HARI DAN JAM OTOMATIS
    // ==========================================
    private fun formatWaktuNotifikasi(waktuDariFirebase: String): String {
        // Jika kosong, ambil waktu saat ini
        if (waktuDariFirebase.isEmpty()) {
            val formatBaru = SimpleDateFormat("EEEE, HH:mm", Locale("id", "ID"))
            return formatBaru.format(Date())
        }

        // Jika dari firebase hanya mengirim jam (contoh: "14:05"), tambahkan hari ini
        if (waktuDariFirebase.length <= 5 && waktuDariFirebase.contains(":")) {
            val formatHari = SimpleDateFormat("EEEE", Locale("id", "ID"))
            val hariIni = formatHari.format(Date())
            return "$hariIni, $waktuDariFirebase"
        }

        // Coba parsing jika formatnya "yyyy-MM-dd HH:mm:ss"
        try {
            val formatAsli = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = formatAsli.parse(waktuDariFirebase)
            if (date != null) {
                val formatBaru = SimpleDateFormat("EEEE, HH:mm", Locale("id", "ID"))
                return formatBaru.format(date)
            }
        } catch (e: Exception) {
            // Biarkan lewat jika gagal parsing
        }

        // Jika tidak dikenali, kembalikan teks aslinya
        return waktuDariFirebase
    }

    private fun fetchNotifications() {
        val database = FirebaseDatabase.getInstance("https://smartagro-v1-default-rtdb.asia-southeast1.firebasedatabase.app").reference
        database.child("notifikasi").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notifList = mutableListOf<NotificationItem>()
                var idCounter = 1

                for (notifSnapshot in snapshot.children) {
                    val judul = notifSnapshot.child("judul").getValue(String::class.java) ?: ""
                    val pesan = notifSnapshot.child("pesan").getValue(String::class.java) ?: ""
                    val rawWaktu = notifSnapshot.child("waktu").getValue(String::class.java) ?: ""
                    val statusBaca = notifSnapshot.child("status_baca").getValue(Boolean::class.java) ?: false

                    // Gunakan fungsi format yang baru dibuat
                    val waktuFormatted = formatWaktuNotifikasi(rawWaktu)

                    val type = if (judul.contains("Tinggi", ignoreCase = true) || judul.contains("Peringatan", ignoreCase = true)) {
                        NotificationType.WARNING
                    } else {
                        NotificationType.INFO
                    }

                    notifList.add(
                        NotificationItem(
                            id = idCounter++,
                            title = judul,
                            description = pesan,
                            type = type,
                            time = waktuFormatted, // Waktu sudah berformat Hari, Jam
                            isRead = statusBaca
                        )
                    )
                }
                _notifications.value = notifList.reversed()
                _alertCount.value = notifList.count { !it.isRead }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun markNotificationAsRead(notificationId: Int) {
        viewModelScope.launch {
            val database = FirebaseDatabase.getInstance("https://smartagro-v1-default-rtdb.asia-southeast1.firebasedatabase.app").reference
            database.child("notifikasi").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var currentId = 1
                    for (notifSnapshot in snapshot.children) {
                        if (currentId == notificationId) {
                            notifSnapshot.ref.child("status_baca").setValue(true)
                            break
                        }
                        currentId++
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            val database = FirebaseDatabase.getInstance("https://smartagro-v1-default-rtdb.asia-southeast1.firebasedatabase.app").reference
            database.child("notifikasi").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (notifSnapshot in snapshot.children) {
                        notifSnapshot.ref.child("status_baca").setValue(true)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    private fun fetchMonitoringData() {
        val database = FirebaseDatabase.getInstance("https://smartagro-v1-default-rtdb.asia-southeast1.firebasedatabase.app").reference
        database.child("monitoring").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val suhuAir = snapshot.child("suhu_air").getValue(Float::class.java) ?: 0f
                val statusEsp = snapshot.child("status_esp32").getValue(String::class.java) ?: "Offline"
                val statusPeltierIoT = snapshot.child("status_peltier").getValue(Boolean::class.java) ?: false

                _currentTemp.value = suhuAir
                _peltierStatus.value = statusPeltierIoT

                if (statusEsp == "Online") {
                    _lastSeen.value = System.currentTimeMillis()
                    _isDeviceOnline.value = true
                }

                if (suhuAir >= 26f) {
                    if (!hasNotifiedForHighTemp) {
                        notificationHelper.showTemperatureAlert(suhuAir)
                        hasNotifiedForHighTemp = true
                    }
                } else if (suhuAir < 25f) {
                    hasNotifiedForHighTemp = false
                }

                val nowStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                if (nowStr != currentDate) {
                    currentDate = nowStr
                    _minTemp.value = suhuAir
                    _maxTemp.value = suhuAir
                } else {
                    _minTemp.value = if (_minTemp.value == null) suhuAir else minOf(_minTemp.value!!, suhuAir)
                    _maxTemp.value = if (_maxTemp.value == null) suhuAir else maxOf(_maxTemp.value!!, suhuAir)
                }

                val currentHistory = _temperatureHistory.value.toMutableList()
                currentHistory.add(suhuAir)
                if (currentHistory.size > 20) {
                    currentHistory.removeAt(0)
                }
                _temperatureHistory.value = currentHistory

                _lastUpdated.value = "Diperbarui ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}"
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchControlData() {
        val database = FirebaseDatabase.getInstance("https://smartagro-v1-default-rtdb.asia-southeast1.firebasedatabase.app").reference
        database.child("control").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isForceCooling = snapshot.child("force_cooling").getValue(Boolean::class.java) ?: false
                val isAutoMode = snapshot.child("auto_mode").getValue(Boolean::class.java) ?: true

                _forceCoolingFlag.value = isForceCooling
                _autoModeEnabled.value = isAutoMode
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun startHeartbeatMonitor() {
        viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val diff = now - _lastSeen.value
                _isDeviceOnline.value = diff < 15000
                delay(5000)
            }
        }
    }

    fun setForceCooling(active: Boolean) {
        viewModelScope.launch {
            _forceCoolingFlag.value = active
            val database = FirebaseDatabase.getInstance("https://smartagro-v1-default-rtdb.asia-southeast1.firebasedatabase.app").reference
            database.child("control").child("force_cooling").setValue(active)
        }
    }

    fun setAutoMode(active: Boolean) {
        viewModelScope.launch {
            _autoModeEnabled.value = active
            val database = FirebaseDatabase.getInstance("https://smartagro-v1-default-rtdb.asia-southeast1.firebasedatabase.app").reference
            database.child("control").child("auto_mode").setValue(active)
        }
    }

    fun updateUserProfile(name: String, phone: String, email: String, location: String, garden: String) {
        _userName.value = name
        _userPhone.value = phone
        _userEmail.value = email
        _userLocation.value = location
        _userGardenName.value = garden
    }

    fun updateProfileImage(uri: Uri?) {
        _profileImageUri.value = uri
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