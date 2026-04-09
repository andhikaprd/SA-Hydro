package com.example.smartagro

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Segment
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartagro.ui.theme.CreamPastel
import com.example.smartagro.ui.theme.DarkGreen
import com.example.smartagro.ui.theme.LightGreen
import java.util.Locale

@Composable
fun MonitorScreen(viewModel: SmartAgroViewModel) {
    var selectedTab by remember { mutableStateOf("Monitor Live") }
    val currentTemp by viewModel.currentTemp.collectAsState()
    val tempStatus by viewModel.tempStatus.collectAsState()
    val peltierStatus by viewModel.peltierStatus.collectAsState()
    val forceCoolingFlag by viewModel.forceCoolingFlag.collectAsState()
    val isDeviceOnline by viewModel.isDeviceOnline.collectAsState()
    val temperatureHistory by viewModel.temperatureHistory.collectAsState()
    
    // Collect Min & Max Temp from ViewModel
    val minTemp by viewModel.minTemp.collectAsState()
    val maxTemp by viewModel.maxTemp.collectAsState()

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
            MonitorHeaderWithTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                isOnline = isDeviceOnline
            )
        }

        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            if (selectedTab == "Monitor Live") {
                MonitorLiveTab(
                    currentTemp = currentTemp,
                    tempStatus = tempStatus,
                    peltierStatus = peltierStatus,
                    forceCoolingFlag = forceCoolingFlag,
                    onForceCoolingChange = { viewModel.setForceCooling(it) },
                    isOnline = isDeviceOnline,
                    temperatureHistory = temperatureHistory,
                    minTemp = minTemp,
                    maxTemp = maxTemp
                )
            } else {
                RiwayatGrafikTab()
            }
        }
    }
}

@Composable
fun MonitorHeaderWithTabs(selectedTab: String, onTabSelected: (String) -> Unit, isOnline: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Monitor & Riwayat",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Sensor DS18B20 - ESP32-Bak-01",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
            
            Surface(
                color = if (isOnline) Color.White.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(if (isOnline) Color(0xFF4CAF50) else Color.Red, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isOnline) "🟢 Online" else "🔴 Offline", 
                        color = Color.White, 
                        fontSize = 11.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tab Bar / Segmented Control
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(Color.Black.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            val tabs = listOf("Monitor Live", "Riwayat & Grafik")
            tabs.forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (tab == "Monitor Live") Icons.Default.Timeline else Icons.Default.BarChart,
                            contentDescription = null,
                            tint = if (isSelected) DarkGreen else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tab,
                            color = if (isSelected) DarkGreen else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonitorLiveTab(
    currentTemp: Float, 
    tempStatus: String,
    peltierStatus: Boolean,
    forceCoolingFlag: Boolean,
    onForceCoolingChange: (Boolean) -> Unit,
    isOnline: Boolean,
    temperatureHistory: List<Float>,
    minTemp: Float?,
    maxTemp: Float?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // 1. Kartu Hijau LIVE MONITORING
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (isOnline) 1f else 0.6f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Color.White, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "LIVE MONITORING", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                
                Text(
                    text = String.format(Locale.getDefault(), "%.1f°C", currentTemp),
                    color = Color.White,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.alpha(if (isOnline) 1f else 0.5f)
                )
                
                if (!isOnline) {
                    Text(
                        text = "Data Terakhir Tersimpan (Offline)",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp)) {
                        Text(text = tempStatus, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
                    }
                    Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (peltierStatus) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward, 
                                contentDescription = null, 
                                tint = Color.White, 
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (peltierStatus) "Turun" else "Naik", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Kartu 10 Pembacaan Terakhir
        Card(
            modifier = Modifier.fillMaxWidth().alpha(if (isOnline) 1f else 0.7f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Visualisasi Real-time", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).background(if (isOnline) LightGreen else Color.Gray, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isOnline) "Live" else "Paused", color = if (isOnline) LightGreen else Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                    RealtimeLineChart(temperatureHistory)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Kendali Peltier Area
        Column(modifier = Modifier.fillMaxWidth().alpha(if (isOnline) 1f else 0.5f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Kendali Peltier", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                
                val statusText = if (peltierStatus) {
                    if (forceCoolingFlag) "Unit Chiller ON (Mode: Manual)" else "Unit Chiller ON (Mode: Otomatis)"
                } else {
                    "Unit Chiller OFF"
                }
                
                val statusColor = if (peltierStatus) Color(0xFF2196F3) else Color.Gray

                Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (peltierStatus) Icons.Default.AcUnit else Icons.Default.PowerSettingsNew, 
                            contentDescription = null, 
                            tint = statusColor, 
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = statusText, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            PeltierSwitchCard(
                modifier = Modifier.fillMaxWidth(), 
                title = "Force Cooling", 
                desc = if (isOnline) "Paksa mendinginkan air nutrisi" else "Kontrol tidak tersedia (Offline)",
                checked = forceCoolingFlag,
                onCheckedChange = if (isOnline) onForceCoolingChange else { _ -> },
                enabled = isOnline
            )
            
            Text(
                text = "Mode manual aktif maks 15 menit - Jeda beralih 30 detik",
                color = Color.Gray,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Grid Kotak Status (Now with Real-time Min/Max)
        StatsGrid(minTemp = minTemp, maxTemp = maxTemp)

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Pembacaan Terkini
        Text(text = "Pembacaan Terkini", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        RecentReadingsList()

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun RealtimeLineChart(data: List<Float>) {
    if (data.size < 2) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Mendapatkan data...", color = Color.Gray, fontSize = 12.sp)
        }
        return
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val spacing = width / (data.size - 1)
        
        // Adaptive Scaling
        val maxData = (data.maxOrNull() ?: 30f) + 0.5f
        val minData = (data.minOrNull() ?: 20f) - 0.5f
        val range = (maxData - minData).coerceAtLeast(1f)

        val points = data.mapIndexed { index, value ->
            val x = index * spacing
            val y = height - ((value - minData) / range) * height
            androidx.compose.ui.geometry.Offset(x, y)
        }

        // 1. Draw Gradient Fill
        val fillPath = Path().apply {
            moveTo(0f, height)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(width, height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(LightGreen.copy(alpha = 0.3f), Color.Transparent)
            )
        )

        // 2. Draw Smooth Line
        val linePath = Path().apply {
            points.forEachIndexed { index, point ->
                if (index == 0) moveTo(point.x, point.y) else lineTo(point.x, point.y)
            }
        }
        drawPath(
            path = linePath,
            color = LightGreen,
            style = Stroke(width = 3.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )

        // 3. Draw Latest Data Dot
        val lastPoint = points.last()
        drawCircle(
            color = LightGreen,
            radius = 5.dp.toPx(),
            center = lastPoint
        )
        drawCircle(
            color = Color.White,
            radius = 2.5.dp.toPx(),
            center = lastPoint
        )
    }
}

@Composable
fun RiwayatGrafikTab() {
    var selectedInnerTab by remember { mutableStateOf("Grafik") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Riwayat & Grafik internal
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, RoundedCornerShape(25.dp))
                .padding(4.dp)
        ) {
            listOf("Grafik", "Daftar").forEach { tab ->
                val isSelected = selectedInnerTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(25.dp))
                        .background(if (isSelected) DarkGreen else Color.Transparent)
                        .clickable { selectedInnerTab = tab }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) Color.White else Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            HistorySummaryCardsRow()
            
            Spacer(modifier = Modifier.height(20.dp))
            
            if (selectedInnerTab == "Grafik") {
                GrafikTabContent()
            } else {
                DaftarTabContent()
            }
        }
    }
}

@Composable
private fun HistorySummaryCardsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Storage,
            iconTint = Color(0xFF2196F3),
            value = "12",
            label = "Total Data"
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.AutoMirrored.Filled.Segment,
            iconTint = Color(0xFF4CAF50),
            value = "25.4°C",
            label = "Rata - rata"
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.NotificationsActive,
            iconTint = Color(0xFFF44336),
            value = "4",
            label = "Insiden"
        )
    }
}

@Composable
private fun SummaryCard(modifier: Modifier = Modifier, icon: ImageVector, iconTint: Color, value: String, label: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = iconTint.copy(alpha = 0.1f),
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.padding(6.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = label, fontSize = 10.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
private fun GrafikTabContent() {
    val timeFilters = listOf("Harian", "Mingguan", "Bulanan")
    var selectedTimeFilter by remember { mutableStateOf("Mingguan") }
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isExpanded = true }
                        ) {
                            Text(
                                text = "Grafik $selectedTimeFilter",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                            timeFilters.forEach { filter ->
                                DropdownMenuItem(
                                    text = { Text(filter) },
                                    onClick = {
                                        selectedTimeFilter = filter
                                        isExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(LightGreen, RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Rata-rata", fontSize = 10.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFFF44336), RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Maks", fontSize = 10.sp, color = Color.Gray)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Grafik Line Chart
                Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    val dummyData = listOf(24.2f, 25.4f, 28.2f, 26.1f, 27.5f, 25.0f, 24.8f)
                    LineChartHistoryInternal(data = dummyData)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Warning Box
                Surface(
                    color = Color(0xFFFFF5F5),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Batas aman suhu: 28°C. Hari dengan suhu melebihi batas ditandai merah.",
                            color = Color.Red,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Kotak Ringkasan
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatBox(label = "Suhu Tertinggi", value = "29.1°C", color = Color(0xFFF44336), modifier = Modifier.weight(1f))
            StatBox(label = "Suhu Terendah", value = "22.9°C", color = Color(0xFF2196F3), modifier = Modifier.weight(1f))
            StatBox(label = "Rata-rata", value = "25.4°C", color = LightGreen, modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun StatBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 10.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun DaftarTabContent() {
    val context = LocalContext.current
    val timeFilters = listOf("Harian", "Mingguan", "Bulanan")
    var selectedTimeFilter by remember { mutableStateOf("Harian") }
    var isExpanded by remember { mutableStateOf(false) }

    val historyData = listOf(
        MonitorHistoryItemData("18 Mar 2026 19:42", "24.5°C", "Normal"),
        MonitorHistoryItemData("18 Mar 2026 14:05", "28.2°C", "Bahaya"),
        MonitorHistoryItemData("18 Mar 2026 13:00", "27.4°C", "Waspada"),
        MonitorHistoryItemData("18 Mar 2026 12:00", "26.1°C", "Normal"),
        MonitorHistoryItemData("17 Mar 2026 20:00", "23.8°C", "Normal"),
        MonitorHistoryItemData("17 Mar 2026 08:00", "22.9°C", "Normal"),
        MonitorHistoryItemData("16 Mar 2026 14:30", "29.1°C", "Bahaya"),
        MonitorHistoryItemData("16 Mar 2026 10:00", "24.2°C", "Normal"),
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.clickable { isExpanded = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = selectedTimeFilter, fontSize = 14.sp)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
                DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                    timeFilters.forEach { filter ->
                        DropdownMenuItem(
                            text = { Text(filter) },
                            onClick = {
                                selectedTimeFilter = filter
                                isExpanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = { Toast.makeText(context, "Mendownload Data CSV...", Toast.LENGTH_SHORT).show() },
                colors = ButtonDefaults.buttonColors(containerColor = LightGreen),
                contentPadding = PaddingValues(horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Unduh Data (CSV)", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        
        Text(text = "Riwayat Lengkap", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))

        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Text(text = "Waktu", fontSize = 12.sp, color = LightGreen, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                    Text(text = "Suhu", fontSize = 12.sp, color = LightGreen, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text(text = "Status", fontSize = 12.sp, color = LightGreen, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                }
                
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(historyData) { data ->
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = data.time, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1.5f))
                            Text(text = data.temp, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                                InternalHistoryStatusBadge(status = data.status)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(top = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

@Composable
private fun InternalHistoryStatusBadge(status: String) {
    val (color, bgColor) = when (status) {
        "Bahaya" -> Color(0xFFF44336) to Color(0xFFFFF5F5)
        "Waspada" -> Color(0xFFFBC02D) to Color(0xFFFFFDE7)
        else -> LightGreen to Color(0xFFF1F8EC)
    }
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = status,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun LineChartHistoryInternal(data: List<Float>) {
    val chartColor = LightGreen
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val spacing = width / (data.size - 1)
        val maxData = data.maxOrNull() ?: 1f
        val minData = data.minOrNull() ?: 0f
        val range = (maxData - minData).coerceAtLeast(1f)
        
        val points = data.mapIndexed { index, value ->
            val x = index * spacing
            val y = height - ((value - minData) / range) * height
            Pair(x, y)
        }

        val fillPath = Path().apply {
            moveTo(0f, height)
            points.forEach { (x, y) -> lineTo(x, y) }
            lineTo(width, height)
            close()
        }
        drawPath(path = fillPath, brush = Brush.verticalGradient(colors = listOf(chartColor.copy(alpha = 0.3f), Color.Transparent)))

        val linePath = Path().apply {
            points.forEachIndexed { index, point ->
                if (index == 0) moveTo(point.first, point.second)
                else lineTo(point.first, point.second)
            }
        }
        drawPath(path = linePath, color = chartColor, style = Stroke(width = 3.dp.toPx()))

        points.forEach { (x, y) ->
            val dotColor = if (data[points.indexOf(Pair(x, y))] >= 28f) Color(0xFFF44336) else chartColor
            drawCircle(color = Color.White, radius = 4.dp.toPx(), center = androidx.compose.ui.geometry.Offset(x, y))
            drawCircle(color = dotColor, radius = 4.dp.toPx(), center = androidx.compose.ui.geometry.Offset(x, y), style = Stroke(width = 2.dp.toPx()))
        }
    }
}

private data class MonitorHistoryItemData(val time: String, val temp: String, val status: String)

@Composable
fun PeltierSwitchCard(
    modifier: Modifier = Modifier, 
    title: String, 
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape, 
                color = if (enabled) Color(0xFFE3F2FD) else Color.LightGray.copy(alpha = 0.2f), 
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AcUnit, 
                        contentDescription = null, 
                        tint = if (enabled) Color(0xFF2196F3) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (enabled) Color.Black else Color.Gray)
            Text(text = desc, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF4CAF50),
                    disabledCheckedTrackColor = Color(0xFF4CAF50).copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun LineChartMini(data: List<Float>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val spacing = width / (data.size - 1)
        val maxData = data.maxOrNull() ?: 1f
        val minData = data.minOrNull() ?: 0f
        val range = (maxData - minData).coerceAtLeast(0.1f)
        
        val points = data.mapIndexed { index, value ->
            Pair(index * spacing, height - ((value - minData) / range) * height)
        }

        val fillPath = Path().apply {
            moveTo(0f, height)
            points.forEach { lineTo(it.first, it.second) }
            lineTo(width, height)
            close()
        }
        drawPath(fillPath, brush = Brush.verticalGradient(listOf(LightGreen.copy(alpha = 0.2f), Color.Transparent)))

        val path = Path().apply {
            points.forEachIndexed { index, pair ->
                if (index == 0) moveTo(pair.first, pair.second) else lineTo(pair.first, pair.second)
            }
        }
        drawPath(path, color = LightGreen, style = Stroke(width = 2.dp.toPx()))
        
        points.forEach { drawCircle(color = LightGreen, radius = 3.dp.toPx(), center = androidx.compose.ui.geometry.Offset(it.first, it.second)) }
    }
}

@Composable
private fun StatsGrid(minTemp: Float?, maxTemp: Float?) {
    val minTempStr = minTemp?.let { String.format(Locale.getDefault(), "%.1f°C", it) } ?: "--.-°C"
    val maxTempStr = maxTemp?.let { String.format(Locale.getDefault(), "%.1f°C", it) } ?: "--.-°C"

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SmallStatCard(
                modifier = Modifier.weight(1f), 
                icon = Icons.Default.Thermostat, 
                iconTint = Color(0xFF2196F3), 
                title = "Suhu Min Hari Ini", 
                value = minTempStr
            )
            SmallStatCard(
                modifier = Modifier.weight(1f), 
                icon = Icons.Default.Thermostat, 
                iconTint = Color(0xFFF44336), 
                title = "Suhu Maks Hari Ini", 
                value = maxTempStr
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SmallStatCard(modifier = Modifier.weight(1f), icon = Icons.Default.CheckCircle, iconTint = Color(0xFF4CAF50), title = "Akurasi Sensor", value = "99.8%")
            SmallStatCard(modifier = Modifier.weight(1f), icon = Icons.Default.Wifi, iconTint = Color(0xFF9C27B0), title = "Kekuatan Sinyal", value = "-62 dBm")
        }
    }
}

@Composable
private fun SmallStatCard(modifier: Modifier = Modifier, icon: ImageVector, iconTint: Color, title: String, value: String) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(8.dp), color = iconTint.copy(alpha = 0.1f), modifier = Modifier.size(32.dp)) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.padding(6.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontSize = 10.sp, color = Color.Gray)
                Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RecentReadingsList() {
    val readings = listOf(
        MonitorReading("24.5°C", "11:00:37", "Normal"),
        MonitorReading("24.6°C", "11:00:35", "Normal"),
        MonitorReading("24.6°C", "11:00:33", "Normal"),
        MonitorReading("24.7°C", "11:00:31", "Normal"),
        MonitorReading("24.7°C", "11:00:29", "Normal")
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            readings.forEachIndexed { index, reading ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.size(40.dp).background(LightGreen.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = LightGreen, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = reading.temp, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = reading.time, fontSize = 11.sp, color = Color.Gray)
                    }
                    Surface(color = LightGreen.copy(alpha = 0.1f), shape = RoundedCornerShape(20.dp)) {
                        Text(text = reading.status, color = LightGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                    }
                }
                if (index < readings.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                }
            }
        }
    }
}

data class MonitorReading(val temp: String, val time: String, val status: String)

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun MonitorScreenPreview() {
    Text("Preview Monitor Screen")
}
