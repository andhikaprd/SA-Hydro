package com.example.smartagro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

data class NotificationItem(
    val id: Int,
    val title: String,
    val description: String,
    val type: NotificationType,
    val time: String,
    val value: String? = null,
    val isRead: Boolean = false
)

// 1. STATEFUL COMPOSABLE
@Composable
fun NotificationScreen(viewModel: SmartAgroViewModel) {
    val notifications by viewModel.notifications.collectAsState()

    NotificationScreenContent(
        notifications = notifications,
        onMarkAllAsRead = { viewModel.markAllNotificationsAsRead() },
        onMarkAsRead = { id -> viewModel.markNotificationAsRead(id) }
    )
}

// 2. STATELESS COMPOSABLE
@Composable
fun NotificationScreenContent(
    notifications: List<NotificationItem>,
    onMarkAllAsRead: () -> Unit = {},
    onMarkAsRead: (Int) -> Unit = {}
) {
    val unreadCount = notifications.count { !it.isRead }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamPastel)
    ) {
        NotificationHeader(
            unreadCount = unreadCount,
            onReadAllClick = onMarkAllAsRead
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Langsung melooping semua notifikasi tanpa filter
            items(notifications) { item ->
                NotificationCard(
                    item = item,
                    onReadClick = { onMarkAsRead(item.id) }
                )
            }
        }
    }
}

@Composable
fun NotificationHeader(
    unreadCount: Int,
    onReadAllClick: () -> Unit
) {
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
                text = "$unreadCount belum dibaca",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(20.dp))
                .clickable { onReadAllClick() },
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

// UNIFIED NOTIFICATION CARD
@Composable
fun NotificationCard(item: NotificationItem, onReadClick: () -> Unit) {
    // Menentukan ikon dan warna berdasarkan tipe notifikasi
    val (icon, tintColor) = when (item.type) {
        NotificationType.WARNING -> Pair(Icons.Default.NotificationsActive, Color.Red)
        NotificationType.INFO -> Pair(Icons.Default.Info, Color(0xFF2196F3))
        NotificationType.SYSTEM -> Pair(Icons.Default.CheckCircle, DarkGreen)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onReadClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), // Latar seragam putih
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(tintColor) // Garis warna sesuai tipe
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tintColor,
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
                    if (item.value != null) {
                        Text(text = item.value, color = tintColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Text(text = item.time, color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    if (!item.isRead) {
                        Text(
                            text = "Tandai Dibaca",
                            color = LightGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { onReadClick() }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun NotificationScreenPreview() {
    val dummyData = listOf(
        NotificationItem(
            id = 1,
            title = "Suhu Air Terlalu Tinggi !",
            description = "Suhu air mencapai 28.2°C melebihi batas aman. Peltier pendingin diaktifkan.",
            type = NotificationType.WARNING,
            time = "Senin, 14:05", // Dummy data sudah pakai Hari
            value = "28.2°C",
            isRead = false
        ),
        NotificationItem(
            id = 2,
            title = "Suhu Kembali Normal",
            description = "Suhu air telah stabil dan kembali ke kondisi optimal.",
            type = NotificationType.INFO,
            time = "Senin, 13:05",
            value = "24.5°C",
            isRead = false
        ),
        NotificationItem(
            id = 3,
            title = "Koneksi Stabil",
            description = "Sistem NodeMCU dan sensor suhu terhubung dengan baik.",
            type = NotificationType.SYSTEM,
            time = "Senin, 12:00",
            isRead = true
        )
    )

    NotificationScreenContent(notifications = dummyData)
}