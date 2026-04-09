package com.example.smartagro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartagro.ui.theme.CreamPastel
import com.example.smartagro.ui.theme.DarkGreen
import com.example.smartagro.ui.theme.LightGreen

enum class NotificationType {
    WARNING, INFO, SYSTEM
}

data class NotificationItem(
    val id: Int,
    val title: String,
    val description: String,
    val type: NotificationType,
    val time: String,
    val value: String? = null,
    val isRead: Boolean = false
)

@Composable
fun NotificationScreen() {
    val notifications = remember {
        listOf(
            NotificationItem(
                1, "Suhu Air Terlalu Tinggi !",
                "Suhu air mencapai 28.2°C melebihi batas aman. Segera ambil tindakan.",
                NotificationType.WARNING, "14:05", "28.2°C", false
            ),
            NotificationItem(
                2, "Peringatan Suhu Mendekati Batas",
                "Suhu air 27.4°C mendekati batas waspada 28°C. Pantau terus.",
                NotificationType.WARNING, "13:40", "27.4°C", false
            ),
            NotificationItem(
                3, "Suhu Kembali Normal",
                "Suhu air telah kembali ke kondisi normal 24.5°C . Setelah dilakukan pendinginan",
                NotificationType.INFO, "13:05", "24.5°C", true
            ),
            NotificationItem(
                4, "Perangkat ESP32 Terhubung",
                "Sensor ESP32-Bak-01 berhasil terhubung ke jaringan WiFi.",
                NotificationType.SYSTEM, "06:04", null, true
            ),
            NotificationItem(
                5, "Laporan Harian",
                "Suhu rata rata hari ini : 24.2°C . Kondisi optimal untuk pertumbuhan selada",
                NotificationType.INFO, "Kemarin 21:40", "24.2°C", true
            )
        )
    }

    // State untuk filter yang dipilih
    var selectedFilter by remember { mutableStateOf("Semua") }

    // Logika Filtering: Filter list berdasarkan tipe yang dipilih
    val filteredNotifications = remember(selectedFilter, notifications) {
        when (selectedFilter) {
            "Peringatan" -> notifications.filter { it.type == NotificationType.WARNING }
            "Info" -> notifications.filter { it.type == NotificationType.INFO }
            "Sistem" -> notifications.filter { it.type == NotificationType.SYSTEM }
            else -> notifications
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamPastel)
    ) {
        NotificationHeader()

        // Mengirim state dan callback ke FilterTabRow
        FilterTabRow(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it },
            notifications = notifications
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredNotifications) { item ->
                when (item.type) {
                    NotificationType.WARNING -> WarningNotificationCard(item)
                    NotificationType.INFO -> InfoNotificationCard(item)
                    NotificationType.SYSTEM -> SystemNotificationCard(item)
                }
            }
        }
    }
}

@Composable
fun NotificationHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkGreen)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = "Notifikasi",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "2 belum dibaca",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable { /* Action */ },
            color = Color.White.copy(alpha = 0.2f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "Baca Semua",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun FilterTabRow(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    notifications: List<NotificationItem>
) {
    val filterOptions = listOf("Semua", "Peringatan", "Info", "Sistem")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        LazyRow(
            modifier = Modifier.padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterOptions) { filter ->
                val isSelected = filter == selectedFilter

                // Menghitung jumlah notifikasi per kategori secara dinamis
                val count = when (filter) {
                    "Semua" -> notifications.size
                    "Peringatan" -> notifications.count { it.type == NotificationType.WARNING }
                    "Info" -> notifications.count { it.type == NotificationType.INFO }
                    "Sistem" -> notifications.count { it.type == NotificationType.SYSTEM }
                    else -> 0
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) DarkGreen else Color.Transparent)
                        .clickable { onFilterSelected(filter) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "$filter ($count)",
                        color = if (isSelected) Color.White else Color.Gray,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

// ... Sisanya (WarningNotificationCard, InfoNotificationCard, SystemNotificationCard) tetap sama ...

@Composable
fun WarningNotificationCard(item: NotificationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(Color.Red)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (!item.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color.Red, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.description,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = item.value ?: "", color = LightGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = item.time, color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Tandai Dibaca",
                        color = LightGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { /* Action */ }
                    )
                }
            }
        }
    }
}

@Composable
fun InfoNotificationCard(item: NotificationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(Color(0xFF2196F3))
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.description,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.value != null) {
                        Text(text = item.value, color = LightGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Text(text = item.time, color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SystemNotificationCard(item: NotificationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(Color(0xFF9C27B0))
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = Color(0xFF9C27B0),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.description,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = item.time, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun NotificationScreenPreview() {
    NotificationScreen()
}