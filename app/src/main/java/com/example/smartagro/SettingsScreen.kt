package com.example.smartagro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.HelpOutline
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
    ) {
        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGreen)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp)
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
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // KONTEN PENGATURAN
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // 1. KEAMANAN IOT
            SettingsSectionTitle(title = "KEAMANAN IOT")
            SettingsCard {
                SettingsItem(icon = Icons.Default.Timer, title = "Safety Timeout", value = "Terhubung")
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(start = 56.dp))
                SettingsItem(icon = Icons.Default.Security, title = "Anti Spam Polaritas", value = "Aktif")
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(start = 56.dp))
                SettingsItem(icon = Icons.Default.Info, title = "Mengapa fitur ini penting?", iconTint = Color(0xFF2196F3))
            }

            // 2. PENGATURAN MONITORING
            SettingsSectionTitle(title = "PENGATURAN MONITORING")
            SettingsCard {
                SettingsItem(icon = Icons.Default.Thermostat, title = "Threshold Suhu", value = "26°C", iconTint = Color(0xFFF44336))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(start = 56.dp))
                SettingsItem(icon = Icons.Default.Schedule, title = "Interval Pembacaan", value = "5 menit", iconTint = Color(0xFF2196F3))
            }

            // 3. STATUS PERANGKAT
            SettingsSectionTitle(title = "STATUS PERANGKAT")
            SettingsCard {
                SettingsItem(icon = Icons.Default.CheckCircle, title = "Status ESP32", value = "Terhubung")
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(start = 56.dp))
                SettingsItem(icon = Icons.Default.Language, title = "Alamat IP", value = "192.168.1.12", iconTint = Color(0xFF9C27B0))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(start = 56.dp))
                SettingsItem(icon = Icons.Default.Wifi, title = "Sinyal WiFi", value = "-62 dBm", iconTint = Color(0xFF2196F3))
            }

            // 4. LAINNYA (Dapat di-klik / Navigasi)
            SettingsSectionTitle(title = "LAINNYA")
            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Profil Pengguna",
                    onClick = onNavigateToProfile
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(start = 56.dp))
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Tentang Aplikasi",
                    onClick = onNavigateToAbout
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(start = 56.dp))
                SettingsItem(
                    icon = Icons.Outlined.HelpOutline,
                    title = "Panduan & Bantuan",
                    iconTint = Color(0xFFFF9800),
                    onClick = onNavigateToHelp
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(start = 56.dp))
                SettingsItem(
                    icon = Icons.Default.Logout,
                    title = "Keluar",
                    titleColor = Color(0xFFF44336),
                    iconTint = Color(0xFFF44336),
                    onClick = onLogout
                )
            }

            Spacer(modifier = Modifier.height(40.dp)) // Jarak ekstra agar tidak tertutup Bottom Navigation
        }
    }
}

// --- Komponen Pendukung ---

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        color = LightGreen,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        content = content
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    value: String? = null,
    titleColor: Color = Color.Black,
    iconTint: Color = DarkGreen,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ikon
        Surface(
            shape = CircleShape,
            color = iconTint.copy(alpha = 0.1f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Judul Menu
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = if (onClick != null) FontWeight.Medium else FontWeight.Normal,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )

        // Nilai (Contoh: "5 menit", "Terhubung") jika ada
        if (value != null) {
            Text(
                text = value,
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        // Panah kanan hanya muncul jika item ini bisa di-klik pindah halaman
        if (onClick != null) {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}