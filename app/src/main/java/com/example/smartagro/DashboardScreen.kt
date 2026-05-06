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
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: SmartAgroViewModel,
    onNavigateToMonitor: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToNotification: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    // Collecting State from ViewModel
    val currentTemp by viewModel.currentTemp.collectAsState()
    val tempStatus by viewModel.tempStatus.collectAsState()
    val lastUpdated by viewModel.lastUpdated.collectAsState()
    val isOnline by viewModel.isDeviceOnline.collectAsState()
    val alertCount by viewModel.alertCount.collectAsState()
    val tempHistory by viewModel.temperatureHistory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamPastel)
    ) {
        // Sticky Header
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

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp), // Lega vertical padding
            verticalArrangement = Arrangement.spacedBy(24.dp) // Ruang napas konsisten antar elemen
        ) {
            CurrentStatusCard(
                temp = String.format(Locale.getDefault(), "%.1f", currentTemp),
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

            RecentWarningsSection()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun RealtimeLineChart(data: List<Float>, modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(
        color = Color(0xFF1B5E20),
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
    )

    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas

        val width = size.width
        val height = size.height
        val minTemp = 20f
        val maxTemp = 35f
        val range = maxTemp - minTemp

        val points = data.mapIndexed { index, temp ->
            val x = index * (width / (data.size - 1))
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

            val text = String.format(Locale.getDefault(), "%.1f", data[index])
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
fun CurrentStatusCard(temp: String, status: String, updatedAt: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally // Konten rata tengah persis
        ) {
            Text(
                text = "STATUS SUHU AIR SAAT INI",
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp)) // Ruang napas proporsional

            Text(
                text = "$temp °C",
                color = Color(0xFF1B5E20), 
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp)) // Ruang napas proporsional

            Surface(
                color = LightGreen.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Kondisi $status",
                    color = LightGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp)) // Ruang napas proporsional

            Text(
                text = "Diperbarui $updatedAt",
                color = Color.Gray,
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
                Text(text = "Batas Aman (28°C)", fontSize = 10.sp, color = Color.Gray)
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
fun RecentWarningsSection() {
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
            title = "Suhu Air Terlalu Tinggi!",
            desc = "Suhu air mencapai 28.2°C melebihi batas....",
            time = "14:05"
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        WarningCard(
            title = "Suhu Air Terlalu Tinggi!",
            desc = "Suhu air mencapai 28.2°C melebihi batas....",
            time = "14:05"
        )
    }
}

@Composable
fun WarningCard(title: String, desc: String, time: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)) // Pinkish background
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
