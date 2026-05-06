package com.example.smartagro

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartagro.ui.theme.CreamPastel
import com.example.smartagro.ui.theme.DarkGreen
import com.example.smartagro.ui.theme.LightGreen

// --- IMPORT UNTUK GOOGLE LOGIN & FIREBASE ---
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

// --- IMPORT UNTUK GOOGLE RECAPTCHA ENTERPRISE ---
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaClient

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit = {}) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // SharedPreferences setup
    val sharedPreferences = remember {
        context.getSharedPreferences("SmartAgroPrefs", Context.MODE_PRIVATE)
    }

    // Inisialisasi Firebase Auth & Credential Manager
    val auth = remember { FirebaseAuth.getInstance() }
    val credentialManager = remember { CredentialManager.create(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    // --- STATE RECAPTCHA & LOADING ---
    var recaptchaClient by remember { mutableStateOf<RecaptchaClient?>(null) }
    var isLoginLoading by remember { mutableStateOf(false) }
    var isGoogleLoading by remember { mutableStateOf(false) }

    // --- LOGIKA AUTO-FILL & INISIALISASI RECAPTCHA SAAT SCREEN DIBUKA ---
    LaunchedEffect(Unit) {
        // Auto-fill dari SharedPreferences
        val isSaved = sharedPreferences.getBoolean("remember_me", false)
        if (isSaved) {
            email = sharedPreferences.getString("saved_email", "") ?: ""
            password = sharedPreferences.getString("saved_password", "") ?: ""
            rememberMe = true
        }

        // Inisialisasi Google reCAPTCHA Client dengan ID YANG BENAR
        try {
            val application = context.applicationContext as Application
            Recaptcha.getClient(application, "6LdrFNwsAAAAAIlMd1O4zN2-81apvHrW82a83F-m")
                .onSuccess { client ->
                    recaptchaClient = client
                    Log.d("RECAPTCHA", "reCAPTCHA berhasil diinisialisasi")
                }
                .onFailure { exception ->
                    // MEMUNCULKAN PESAN ERROR ASLI DARI GOOGLE
                    Log.e("RECAPTCHA_ERROR", "Gagal inisialisasi: ${exception.message}")
                    Toast.makeText(context, "Error reCAPTCHA: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Exception: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Validation logic for button enabled state
    val isEmailValid = email.contains("@") && email.contains(".")
    val isPasswordValid = password.length >= 6
    val isFormValid = isEmailValid && isPasswordValid

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamPastel)
    ) {
        // Background Top Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(
                    DarkGreen,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header Section
            Surface(
                modifier = Modifier.size(72.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Text(
                text = "SmartAgro",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = "Monitoring Suhu Air Nutrisi Tanaman",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Masuk ke Akun",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Text(
                        text = "Pantau suhu air nutrisi tanamanmu dimana saja",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 4.dp, bottom = 24.dp)
                    )

                    // Email Input
                    Text(
                        text = "Email",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        placeholder = { Text("email@contoh.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = DarkGreen) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                            focusedContainerColor = CreamPastel.copy(alpha = 0.2f),
                            unfocusedContainerColor = CreamPastel.copy(alpha = 0.2f)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Password Input
                    Text(
                        text = "Kata Sandi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        placeholder = { Text("Minimal 6 karakter") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = DarkGreen) },
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(icon, contentDescription = null, tint = Color.Gray)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                            focusedContainerColor = CreamPastel.copy(alpha = 0.2f),
                            unfocusedContainerColor = CreamPastel.copy(alpha = 0.2f)
                        )
                    )

                    // --- FITUR SIMPAN SANDI ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(checkedColor = LightGreen)
                        )
                        Text(
                            text = "Simpan Sandi",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = "Lupa kata sandi?",
                        color = DarkGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp)
                            .clickable { /* Handle forgot password */ }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- LOGIN BUTTON DENGAN VERIFIKASI RECAPTCHA ---
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isLoginLoading = true

                                if (recaptchaClient != null) {
                                    // Eksekusi pemeriksaan reCAPTCHA di belakang layar
                                    recaptchaClient!!.execute(RecaptchaAction.LOGIN)
                                        .onSuccess { token ->
                                            isLoginLoading = false

                                            // Lolos validasi reCAPTCHA, jalankan logika login asli
                                            if (email == "admin@gmail.com" && password == "admin123") {
                                                if (rememberMe) {
                                                    sharedPreferences.edit()
                                                        .putString("saved_email", email)
                                                        .putString("saved_password", password)
                                                        .putBoolean("remember_me", true)
                                                        .apply()
                                                } else {
                                                    sharedPreferences.edit().clear().apply()
                                                }
                                                onLoginSuccess()
                                            } else {
                                                Toast.makeText(context, "Email atau Kata Sandi salah", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        .onFailure { exception ->
                                            isLoginLoading = false
                                            Log.e("RECAPTCHA_EXECUTE", "Gagal eksekusi: ${exception.message}")
                                            Toast.makeText(context, "Akses ditolak: ${exception.message}", Toast.LENGTH_LONG).show()
                                        }
                                } else {
                                    isLoginLoading = false
                                    Toast.makeText(context, "Sistem keamanan belum siap, silakan coba beberapa saat lagi.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = isFormValid && !isLoginLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightGreen,
                            disabledContainerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isLoginLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Memverifikasi...", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.Login, contentDescription = null)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Masuk", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "atau masuk dengan",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Google Login Button
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                isGoogleLoading = true
                                try {
                                    val googleIdOption = GetGoogleIdOption.Builder()
                                        .setFilterByAuthorizedAccounts(false)
                                        .setServerClientId(context.getString(R.string.default_web_client_id))
                                        .setAutoSelectEnabled(true)
                                        .build()

                                    val request = GetCredentialRequest.Builder()
                                        .addCredentialOption(googleIdOption)
                                        .build()

                                    val result = credentialManager.getCredential(context, request)
                                    val credential = result.credential

                                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                        val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                                        auth.signInWithCredential(authCredential)
                                            .addOnCompleteListener { task ->
                                                isGoogleLoading = false
                                                if (task.isSuccessful) {
                                                    Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                                                    onLoginSuccess()
                                                } else {
                                                    Toast.makeText(context, "Gagal masuk: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    } else {
                                        isGoogleLoading = false
                                        Toast.makeText(context, "Terjadi kesalahan sistem", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    isGoogleLoading = false
                                    Toast.makeText(context, "Login dibatalkan", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = !isGoogleLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isGoogleLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = DarkGreen,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Memproses...", color = Color.Black, fontWeight = FontWeight.Medium)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = Color.Unspecified
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Masuk dengan Google", color = Color.Black, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(3f))

            // Footer
            val annotatedString = buildAnnotatedString {
                append("Belum punya akun? ")
                withStyle(style = SpanStyle(color = DarkGreen, fontWeight = FontWeight.Bold)) {
                    append("Daftar Sekarang")
                }
            }
            Text(
                text = annotatedString,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .clickable { /* Navigate to Sign Up */ }
            )
        }
    }
}