package com.example.smartagro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartagro.ui.theme.CreamPastel
import com.example.smartagro.ui.theme.DarkGreen
import com.example.smartagro.ui.theme.LightGreen

@Composable
fun AboutScreen(onBack: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamPastel)
    ) {
        // HEADER HIJAU UTUH (Latar belakang solid, tidak nabrak card bawah)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    DarkGreen,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .statusBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            // Top Bar: Panah Kiri, Judul Tengah Presisi
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterStart) // Panah stay di kiri
                        .size(24.dp)
                        .clickable { onBack() }
                )

                Text(
                    text = "Tentang Aplikasi",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center) // Teks persis di tengah
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logo dan Judul Aplikasi
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SmartAgro",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Monitoring Suhu Air Nutrisi Tanaman",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }
        }

        // KONTEN BAWAH (Scrollable tanpa merusak Header)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Card Deskripsi
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Tentang Aplikasi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "Smart Agro adalah sistem IoT untuk petani hidroponik yang memantau suhu air nutrisi selada secara real-time. Dilengkapi kendali otomatis berbasis threshold dan keamanan akses.",
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "FITUR UTAMA",
                color = LightGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
            )

            // Card Fitur Utama
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    FeatureItem(
                        icon = Icons.Default.Thermostat,
                        iconColor = Color(0xFF4CAF50),
                        title = "Monitoring Real-Time",
                        desc = "Pantau suhu air nutrisi selada langsung via sensor DS18B20"
                    )
                    DividerLight()
                    FeatureItem(
                        icon = Icons.Default.NotificationsActive,
                        iconColor = Color(0xFFF44336),
                        title = "Notifikasi Cerdas",
                        desc = "Peringatan otomatis saat suhu mencapai titik kritis"
                    )
                    DividerLight()
                    FeatureItem(
                        icon = Icons.Default.BarChart,
                        iconColor = Color(0xFF2196F3),
                        title = "Analitik Data",
                        desc = "Grafik riwayat suhu untuk analisis tren pola air"
                    )
                    DividerLight()
                    FeatureItem(
                        icon = Icons.Default.Memory,
                        iconColor = Color(0xFFFF9800),
                        title = "IoT Terintegrasi",
                        desc = "Terhubung ESP32 via WiFi & cloud secara real-time"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Dikembangkan oleh", fontSize = 12.sp, color = Color.Gray)
                Text(text = "Kelompok 1 - PBL IT Proyek 2", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkGreen, modifier = Modifier.padding(top = 4.dp))
                Text(text = "Politeknik Negeri Tanah Laut © 2026", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun FeatureItem(icon: ImageVector, iconColor: Color, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = iconColor.copy(alpha = 0.1f),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = desc, fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun DividerLight() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 60.dp),
        thickness = 0.5.dp,
        color = Color.LightGray.copy(alpha = 0.4f)
    )
}