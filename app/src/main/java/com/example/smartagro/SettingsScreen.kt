package com.example.smartagro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartagro.ui.theme.CreamPastel
import com.example.smartagro.ui.theme.DarkGreen
import com.example.smartagro.ui.theme.LightGreen

@Composable
fun SettingsScreen(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToHelp: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamPastel)
            .verticalScroll(rememberScrollState())
    ) {
        SettingsHeader()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // MODE OPERASI Section
            Column {
                SectionLabel("MODE OPERASI")
                OperationModeCard()
            }

            // KEAMANAN IOT Section
            Column {
                SectionLabel("KEAMANAN IOT")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Schedule,
                            iconColor = Color(0xFFF57C00),
                            title = "Safety Timeout",
                            trailingText = "Terhubung"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        SettingsItem(
                            icon = Icons.Default.Timer,
                            iconColor = Color(0xFF4CAF50),
                            title = "Anti Spam Polaritas",
                            trailingText = "192.168.1.1"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        SettingsItem(
                            icon = Icons.Default.Info,
                            iconColor = Color(0xFF2196F3),
                            title = "Mengapa fitur ini penting?"
                        )
                    }
                }
            }

            // PENGATURAN MONITORING Section
            Column {
                SectionLabel("PENGATURAN MONITORING")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Thermostat,
                            iconColor = Color(0xFFE53935),
                            title = "Threshold Suhu",
                            trailingText = "28°C"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        SettingsItem(
                            icon = Icons.Default.AccessTime,
                            iconColor = Color(0xFF1E88E5),
                            title = "Interval Pembacaan",
                            trailingText = "2 detik"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        SettingsItem(
                            icon = Icons.Default.Notifications,
                            iconColor = Color(0xFFFBC02D),
                            title = "Mode Notifikasi"
                        )
                    }
                }
            }

            // STATUS PERANGKAT Section
            Column {
                SectionLabel("STATUS PERANGKAT")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Memory,
                            iconColor = Color(0xFF43A047),
                            title = "Status ESP32",
                            trailingText = "Terhubung"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        SettingsItem(
                            icon = Icons.Default.Public,
                            iconColor = Color(0xFF8E24AA),
                            title = "Alamat IP",
                            trailingText = "192.168.1.1"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        SettingsItem(
                            icon = Icons.Default.Wifi,
                            iconColor = Color(0xFF1E88E5),
                            title = "Sinyal WiFi",
                            trailingText = "-62 dBm"
                        )
                    }
                }
            }

            // PREFERENSI APLIKASI Section
            Column {
                SectionLabel("PREFERENSI APLIKASI")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.VolumeUp,
                            iconColor = Color(0xFFE53935),
                            title = "Suara Notifikasi"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        SettingsItem(
                            icon = Icons.Default.Vibration,
                            iconColor = Color(0xFFF57C00),
                            title = "Getaran"
                        )
                    }
                }
            }

            // LAINNYA Section
            Column {
                SectionLabel("LAINNYA")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Person,
                            iconColor = Color(0xFF43A047),
                            title = "Profil Pengguna",
                            onClick = onNavigateToProfile
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        SettingsItem(
                            icon = Icons.Default.Info,
                            iconColor = Color(0xFF1E88E5),
                            title = "Tentang Aplikasi",
                            onClick = onNavigateToAbout
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        SettingsItem(
                            icon = Icons.Default.MenuBook,
                            iconColor = Color(0xFFF57C00),
                            title = "Panduan & Bantuan",
                            onClick = onNavigateToHelp
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        SettingsItem(
                            icon = Icons.Default.Logout,
                            iconColor = Color(0xFFE53935),
                            title = "Keluar",
                            titleColor = Color(0xFFE53935),
                            onClick = onLogout
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SettingsHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkGreen)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = "Pengaturan",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Konfigurasi sistem Smart Agro",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        color = LightGreen,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun OperationModeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkGreen.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Mode Otomatis", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "Peltier dikontrol otomatis berdasarkan suhu", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "Batas bawah", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                        Text(text = "18°C", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    Column {
                        Text(text = "Batas atas", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                        Text(text = "18°C", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { /* Action */ }
                ) {
                    Text(
                        text = "Ubah",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    titleColor: Color = Color.Black,
    trailingText: String? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = iconColor.copy(alpha = 0.1f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            color = titleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        
        if (trailingText != null) {
            Text(
                text = trailingText,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}
