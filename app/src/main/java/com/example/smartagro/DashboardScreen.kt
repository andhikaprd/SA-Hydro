package com.example.smartagro

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartagro.ui.theme.CreamPastel
import com.example.smartagro.ui.theme.DarkGreen
import com.example.smartagro.ui.theme.LightGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: SmartAgroViewModel,
    onNavigateToMonitor: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToNotification: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val currentTemp by viewModel.currentTemp.collectAsState()
    val tempStatus by viewModel.tempStatus.collectAsState()
    val lastUpdated by viewModel.lastUpdated.collectAsState()
    val isOnline by viewModel.isDeviceOnline.collectAsState()
    val alertCount by viewModel.alertCount.collectAsState()
    val tempHistory by viewModel.temperatureHistory.collectAsState()

    var lastWarningTime by remember { mutableStateOf(0L) }
    var recentWarningData by remember { mutableStateOf<WarningData?>(null) }

    if (tempStatus == "Panas" || tempStatus == "Bahaya") {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastWarningTime > 600000) {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            recentWarningData = WarningData(
                title = "Suhu Air Terlalu Tinggi!",
                desc = "Suhu air mencapai ${String.format(Locale.getDefault(), "%.1f", currentTemp)}°C melebihi batas aman...",
                time = sdf.format(Date(currentTime))
            )
            lastWarningTime = currentTime
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamPastel)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGreen)
        ) {
            DashboardHeader(
                onNotificationClick = onNavigateToNotification,
                onProfileClick = onNavigateToProfile
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // PERUBAHAN: Memasukkan nilai currentTemp asli (bukan String) agar bisa dicek logikanya
            CurrentStatusCard(
                currentTemp = currentTemp,
                status = tempStatus,
                updatedAt = lastUpdated
            )

            SmallStatsRow(
                alertCount = alertCount.toString(),
                onlineStatus = if (isOnline) "Online" else "Offline"
            )

            GraphCard(temperatureHistory = tempHistory)

            ActionButtonsRow(
                onMonitorClick = onNavigateToMonitor,
                onHistoryClick = onNavigateToHistory
            )

            if (recentWarningData != null) {
                RecentWarningsSection(warning = recentWarningData!!)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

data class WarningData(val title: String, val desc: String, val time: String)

@Composable
fun RealtimeLineChart(data: List<Float>, modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(
        color = Color(0xFF1B5E20),
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
    )

    val displayData = data.takeLast(12)

    Canvas(modifier = modifier) {
        if (displayData.size < 2) return@Canvas

        val width = size.width
        val height = size.height
        val minTemp = 20f
        val maxTemp = 35f
        val range = maxTemp - minTemp

        val points = displayData.mapIndexed { index, temp ->
            val x = index * (width / (displayData.size - 1))
            val normalizedTemp = temp.coerceIn(minTemp, maxTemp)
            val y = height - ((normalizedTemp - minTemp) / range) * height
            androidx.compose.ui.geometry.Offset(x, y)
        }

        val path = Path().apply {
            points.forEachIndexed { index, offset ->
                if (index == 0) moveTo(offset.x, offset.y)
                else lineTo(offset.x, offset.y)
            }
        }

        drawPath(
            path = path,
            color = Color(0xFF4CAF50),
            style = Stroke(width = 5f)
        )

        points.forEachIndexed { index, offset ->
            drawCircle(
                color = Color(0xFF2E7D32),
                radius = 8f,
                center = offset
            )

            val text = String.format(Locale.getDefault(), "%.1f", displayData[index])
            val textLayoutResult = textMeasurer.measure(text, labelStyle)

            drawText(
                textMeasurer = textMeasurer,
                text = text,
                style = labelStyle,
                topLeft = androidx.compose.ui.geometry.Offset(
                    x = offset.x - (textLayoutResult.size.width / 2),
                    y = offset.y - textLayoutResult.size.height - 10f
                )
            )
        }
    }
}

@Composable
fun DashboardHeader(
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = "Rabu, 18 Mar 2026",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
            Text(
                text = "Selamat Datang , Petani!",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "ESP32-Bak-01",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(onClick = onNotificationClick) {
                HeaderIconBox(icon = Icons.Outlined.Notifications)
            }
            IconButton(onClick = onProfileClick) {
                HeaderIconBox(icon = Icons.Default.Person)
            }
        }
    }
}

@Composable
fun HeaderIconBox(icon: ImageVector) {
    Surface(
        modifier = Modifier.size(40.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun CurrentStatusCard(currentTemp: Float, status: String, updatedAt: String) {
    // PERUBAHAN LOGIKA WARNA
    val isHot = currentTemp >= 26f
    val tempText = String.format(Locale.getDefault(), "%.1f", currentTemp)

    // Warna berubah jadi merah terang jika panas, kembali putih jika normal
    val cardBackgroundColor = if (isHot) Color(0xFFE53935) else Color.White
    val titleColor = if (isHot) Color.White.copy(alpha = 0.8f) else Color.Gray
    val tempColor = if (isHot) Color.White else Color(0xFF1B5E20)
    val statusBgColor = if (isHot) Color.White.copy(alpha = 0.2f) else LightGreen.copy(alpha = 0.15f)
    val statusTextColor = if (isHot) Color.White else LightGreen
    val timeColor = if (isHot) Color.White.copy(alpha = 0.7f) else Color.Gray
    val displayStatus = if (isHot) "Bahaya (Panas)" else status

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isHot) 6.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "STATUS SUHU AIR SAAT INI",
                color = titleColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "$tempText °C",
                color = tempColor,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = statusBgColor,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Kondisi $displayStatus",
                    color = statusTextColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Diperbarui $updatedAt",
                color = timeColor,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun SmallStatsRow(alertCount: String, onlineStatus: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SmallStatCard(
            modifier = Modifier.weight(1f),
            label = "Rata rata Hari ini",
            value = "24.2°C",
            icon = Icons.Default.Timeline,
            iconTint = Color(0xFF4CAF50)
        )
        SmallStatCard(
            modifier = Modifier.weight(1f),
            label = "Total Peringatan",
            value = alertCount,
            icon = Icons.Default.NotificationsActive,
            iconTint = Color(0xFFF44336)
        )
        SmallStatCard(
            modifier = Modifier.weight(1f),
            label = "Koneksi",
            value = onlineStatus,
            icon = Icons.Default.Wifi,
            iconTint = Color(0xFF4CAF50),
            valueColor = LightGreen
        )
    }
}

@Composable
fun SmallStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    valueColor: Color = Color.Black
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(8.dp),
                color = iconTint.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, fontSize = 10.sp, color = Color.Gray, lineHeight = 12.sp)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
fun GraphCard(temperatureHistory: List<Float>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Grafik Suhu 12 Jam Terakhir",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                RealtimeLineChart(
                    data = temperatureHistory,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Suhu Air", fontSize = 10.sp, color = Color.Gray)
                Text(text = "Batas Aman (26°C)", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ActionButtonsRow(
    onMonitorClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onMonitorClick,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Thermostat, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kendali Peltier", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        OutlinedButton(
            onClick = onHistoryClick,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Lihat Riwayat", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            }
        }
    }
}

@Composable
fun RecentWarningsSection(warning: WarningData) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Peringatan Terbaru", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = "Lihat semua", fontSize = 12.sp, color = LightGreen, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        WarningCard(
            title = warning.title,
            desc = warning.desc,
            time = warning.time
        )
    }
}

@Composable
fun WarningCard(title: String, desc: String, time: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color.Red.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                Text(text = desc, fontSize = 12.sp, color = Color.DarkGray, maxLines = 1)
            }

            Text(text = time, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}