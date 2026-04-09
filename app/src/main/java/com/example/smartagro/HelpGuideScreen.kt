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
import androidx.compose.runtime.*
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
fun HelpGuideScreen() {
    // Scaffold without bottomBar as it's handled in AppNavigation
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
            HelpHeader()

            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                PopularCategoriesSection()
                
                Spacer(modifier = Modifier.height(28.dp))
                HelpTopicsSection()
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun HelpHeader() {
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
            IconButton(onClick = { /* Handle Back */ }) {
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
fun HelpTopicsSection() {
    Column {
        Text(
            text = "Topik Bantuan",
            color = LightGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        HelpTopicItem(
            icon = Icons.Default.Build,
            iconBg = Color(0xFFE3F2FD),
            iconColor = Color(0xFF2196F3),
            title = "Perangkat Tidak Terhubung",
            description = "Langkah koneksi ESP32 via WiFi",
            buttonColor = Color(0xFF2196F3)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        HelpTopicItem(
            icon = Icons.Default.Thermostat,
            iconBg = Color(0xFFE8F5E9),
            iconColor = Color(0xFF4CAF50),
            title = "Kalibrasi Sensor DS18B20",
            description = "Panduan akurasi sensor suhu air",
            buttonColor = Color(0xFF4CAF50)
        )

        Spacer(modifier = Modifier.height(12.dp))

        HelpTopicItem(
            icon = Icons.Default.NotificationsActive,
            iconBg = Color(0xFFFFF3E0),
            iconColor = Color(0xFFFF9800),
            title = "Mengatur Threshold Suhu",
            description = "Setting batas aman air nutrisi",
            buttonColor = Color(0xFFFF9800)
        )
    }
}

@Composable
fun HelpTopicItem(
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    title: String,
    description: String,
    buttonColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = iconBg
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
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
                    text = description,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
            
            Surface(
                modifier = Modifier.clickable { /* Action */ },
                shape = RoundedCornerShape(8.dp),
                color = buttonColor
            ) {
                Text(
                    text = "Buka",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun HelpGuideScreenPreview() {
    HelpGuideScreen()
}
