package com.example.smartagro

import android.content.Context
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartagro.ui.theme.CreamPastel
import com.example.smartagro.ui.theme.DarkGreen
import com.example.smartagro.ui.theme.LightGreen

@Composable
fun ProfileScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE) }

    var isEditMode by remember { mutableStateOf(false) }

    // Mengambil data dari SharedPreferences agar tidak reset saat refresh
    var name by remember { mutableStateOf(sharedPreferences.getString("name", "Bejo Morena") ?: "Bejo Morena") }
    var phone by remember { mutableStateOf(sharedPreferences.getString("phone", "+62 812 3456 7890") ?: "+62 812 3456 7890") }
    var email by remember { mutableStateOf(sharedPreferences.getString("email", "bejo.morena@gmail.com") ?: "bejo.morena@gmail.com") }
    var location by remember { mutableStateOf(sharedPreferences.getString("location", "Pelaihari , Kalimantan Selatan") ?: "Pelaihari , Kalimantan Selatan") }
    var gardenName by remember { mutableStateOf(sharedPreferences.getString("gardenName", "Kebun Selada Sejahtera") ?: "Kebun Selada Sejahtera") }

    val savedImageUri = sharedPreferences.getString("imageUri", null)
    var imageUri by remember { mutableStateOf<Uri?>(savedImageUri?.let { Uri.parse(it) }) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            sharedPreferences.edit().putString("imageUri", it.toString()).apply()
        }
    }

    // Fungsi untuk menyimpan data secara permanen saat tombol "Simpan" diklik
    val onEditToggle = {
        if (isEditMode) {
            sharedPreferences.edit().apply {
                putString("name", name)
                putString("phone", phone)
                putString("email", email)
                putString("location", location)
                putString("gardenName", gardenName)
                apply()
            }
        }
        isEditMode = !isEditMode
    }

    // Struktur Layout Utama
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamPastel)
    ) {
        // HEADER (Statis, tidak bisa di-scroll, background hijau tidak akan ketarik)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGreen)
        ) {
            ProfileHeader(
                onBack = onBack,
                isEditMode = isEditMode,
                onEditToggle = onEditToggle,
                name = name,
                gardenName = gardenName,
                imageUri = imageUri,
                onImageClick = { launcher.launch("image/*") }
            )
        }

        // KONTEN BAWAH (Bisa di-scroll)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Mengambil sisa ruang layar
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
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
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
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