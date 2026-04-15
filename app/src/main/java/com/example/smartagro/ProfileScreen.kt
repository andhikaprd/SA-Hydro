package com.example.smartagro

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartagro.ui.theme.CreamPastel
import com.example.smartagro.ui.theme.DarkGreen
import com.example.smartagro.ui.theme.LightGreen

@Composable
fun ProfileScreen(onBack: () -> Unit = {}) {
    var isEditMode by remember { mutableStateOf(false) }
    
    // 1. Siapkan State untuk menyimpan URI Gambar
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    // 2. Siapkan Image Picker Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // User data states
    var name by remember { mutableStateOf("Bejo Morena") }
    var phone by remember { mutableStateOf("+62 812 3456 7890") }
    var email by remember { mutableStateOf("bejo.morena@gmail.com") }
    var location by remember { mutableStateOf("Pelaihari , Kalimantan Selatan") }
    var gardenName by remember { mutableStateOf("Kebun Selada Sejahtera") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamPastel)
    ) {
        // Header Area Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(DarkGreen)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(
                onBack = onBack, 
                isEditMode = isEditMode,
                onEditToggle = { isEditMode = !isEditMode },
                name = name,
                gardenName = gardenName,
                imageUri = imageUri,
                onImageClick = { launcher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                ProfileSummaryCardsRow()

                Spacer(modifier = Modifier.height(24.dp))

                PersonalInfoSection(
                    isEditMode = isEditMode,
                    name = name,
                    onNameChange = { name = it },
                    phone = phone,
                    onPhoneChange = { phone = it },
                    email = email,
                    onEmailChange = { email = it },
                    location = location,
                    onLocationChange = { location = it },
                    gardenName = gardenName,
                    onGardenNameChange = { gardenName = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                RegisteredDeviceSection()

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ProfileHeader(
    onBack: () -> Unit, 
    isEditMode: Boolean, 
    onEditToggle: () -> Unit,
    name: String,
    gardenName: String,
    imageUri: Uri?,
    onImageClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp).clickable { onBack() }
            )
            Text(
                text = "Profil Pengguna",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Surface(
                color = if (isEditMode) LightGreen else Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.clickable { onEditToggle() }
            ) {
                Text(
                    text = if (isEditMode) "Simpan" else "Ubah",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // UI FOTO PROFIL (Lingkaran)
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
                .border(2.dp, Color.White, CircleShape)
                .clickable { onImageClick() },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default Profile",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = name,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = gardenName,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ProfileSummaryCardsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-10).dp), 
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryStatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.CalendarToday,
            iconColor = Color(0xFF4CAF50),
            value = "128",
            label = "Hari Aktif"
        )
        SummaryStatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Notifications,
            iconColor = Color(0xFFE57373),
            value = "24",
            label = "Total Alert"
        )
        SummaryStatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Timeline,
            iconColor = Color(0xFFFFA726),
            value = "24.2° C",
            label = "Rata-rata Suhu"
        )
    }
}

@Composable
fun SummaryStatCard(modifier: Modifier = Modifier, icon: ImageVector, iconColor: Color, value: String, label: String) {
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
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.1f),
                modifier = Modifier.size(32.dp)
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = label, fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun PersonalInfoSection(
    isEditMode: Boolean,
    name: String, onNameChange: (String) -> Unit,
    phone: String, onPhoneChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    location: String, onLocationChange: (String) -> Unit,
    gardenName: String, onGardenNameChange: (String) -> Unit
) {
    Column {
        Text(
            text = "INFORMASI PRIBADI",
            color = LightGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                EditableInfoItem(
                    isEditMode = isEditMode,
                    icon = Icons.Default.Person,
                    iconColor = Color(0xFF4CAF50),
                    label = "Nama Lengkap",
                    value = name,
                    onValueChange = onNameChange
                )
                InfoDivider()
                EditableInfoItem(
                    isEditMode = isEditMode,
                    icon = Icons.Default.Phone,
                    iconColor = Color(0xFF42A5F5),
                    label = "Nomor HP",
                    value = phone,
                    onValueChange = onPhoneChange
                )
                InfoDivider()
                EditableInfoItem(
                    isEditMode = isEditMode,
                    icon = Icons.Default.Email,
                    iconColor = Color(0xFFFFA726),
                    label = "Email",
                    value = email,
                    onValueChange = onEmailChange
                )
                InfoDivider()
                EditableInfoItem(
                    isEditMode = isEditMode,
                    icon = Icons.Default.LocationOn,
                    iconColor = Color(0xFFEF5350),
                    label = "Lokasi",
                    value = location,
                    onValueChange = onLocationChange
                )
                InfoDivider()
                EditableInfoItem(
                    isEditMode = isEditMode,
                    icon = Icons.Default.Eco,
                    iconColor = Color(0xFF2E7D32),
                    label = "Nama Kebun",
                    value = gardenName,
                    onValueChange = onGardenNameChange
                )
            }
        }
    }
}

@Composable
fun EditableInfoItem(
    isEditMode: Boolean,
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = iconColor.copy(alpha = 0.1f),
            modifier = Modifier.size(40.dp)
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
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = Color.Gray, fontSize = 11.sp)
            if (isEditMode) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LightGreen,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            } else {
                Text(text = value, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun InfoDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp),
        thickness = 0.5.dp,
        color = Color.LightGray.copy(alpha = 0.3f)
    )
}

@Composable
fun RegisteredDeviceSection() {
    Column {
        Text(
            text = "PERANGKAT TERDAFTAR",
            color = LightGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
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
                    shape = RoundedCornerShape(12.dp),
                    color = LightGreen.copy(alpha = 0.1f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = LightGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "ESP32-Bak-01", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "Sensor DS18B20", color = Color.Gray, fontSize = 12.sp)
                    Text(text = "•Online", color = LightGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
