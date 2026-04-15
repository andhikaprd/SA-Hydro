package com.example.smartagro

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartagro.ui.theme.CreamPastel
import com.example.smartagro.ui.theme.DarkGreen
import com.example.smartagro.ui.theme.LightGreen

@Composable
fun HelpGuideScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamPastel)
    ) {
        // Header Area Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(DarkGreen)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            HelpHeader(onBack = { navController.popBackStack() })

            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                PopularCategoriesSection()
                
                Spacer(modifier = Modifier.height(28.dp))
                
                Text(
                    text = "TOPIK BANTUAN",
                    color = LightGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ExpandableHelpCard(
                    title = "Perangkat Tidak Terhubung",
                    subtitle = "Langkah koneksi ESP32 via WiFi",
                    icon = Icons.Default.Build,
                    iconTint = Color(0xFF2196F3),
                    detailText = "1. Pastikan WiFi di lokasi tandon stabil.\n2. Cek apakah lampu indikator ESP32 menyala.\n3. Coba cabut colok adaptor ESP32 untuk merestart alat."
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                ExpandableHelpCard(
                    title = "Kalibrasi Sensor DS18B20",
                    subtitle = "Panduan akurasi sensor suhu air",
                    icon = Icons.Default.Thermostat,
                    iconTint = Color(0xFF4CAF50),
                    detailText = "Pastikan sensor DS18B20 terendam penuh dalam air. Jika suhu tidak wajar, bersihkan ujung sensor dari lumut."
                )

                Spacer(modifier = Modifier.height(12.dp))

                ExpandableHelpCard(
                    title = "Mengatur Threshold Suhu",
                    subtitle = "Setting batas aman air nutrisi",
                    icon = Icons.Default.NotificationsActive,
                    iconTint = Color(0xFFFF9800),
                    detailText = "Anda dapat mengatur batas atas dan bawah suhu di menu Pengaturan. Sistem akan otomatis menyalakan pendingin jika suhu melewati ambang batas tersebut."
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun HelpHeader(onBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = "Bantuan & Panduan",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Panduan & Dokumentasi",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = { Text("Cari bantuan...", color = Color.Gray) },
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            singleLine = true
        )
    }
}

@Composable
fun PopularCategoriesSection() {
    Column {
        Text(
            text = "KATEGORI POPULER",
            color = LightGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CategoryCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.QuestionMark,
                iconColor = Color(0xFF2196F3),
                title = "Masalah Umum"
            )
            CategoryCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Lightbulb,
                iconColor = Color(0xFF4CAF50),
                title = "Tips & Trik"
            )
            CategoryCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Build,
                iconColor = Color(0xFFF44336),
                title = "Pemecahan Masalah"
            )
        }
    }
}

@Composable
fun CategoryCard(modifier: Modifier = Modifier, icon: ImageVector, iconColor: Color, title: String) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun ExpandableHelpCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    detailText: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = iconTint.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
            
            AnimatedVisibility(visible = expanded) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 0.5.dp,
                        color = Color.LightGray.copy(alpha = 0.3f)
                    )
                    Text(
                        text = detailText,
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun HelpGuideScreenPreview() {
    HelpGuideScreen(rememberNavController())
}
