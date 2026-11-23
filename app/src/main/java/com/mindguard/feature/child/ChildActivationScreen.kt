package com.mindguard.feature.child

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.mindguard.core.theme.DarkBlue
import com.mindguard.core.theme.MindShieldAiTheme
import com.mindguard.core.theme.Primary
import com.mindguard.core.theme.TextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildActivationScreen(
    onBack: () -> Unit = {},
    onActivationSuccess: () -> Unit = {}
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val context = LocalContext.current
        var selectedMode by remember { mutableIntStateOf(0) } // 0: QR Code, 1: Manual Code
        var manualCode by remember { mutableStateOf("") }
        var showSuccessDialog by remember { mutableStateOf(false) }

        var hasCameraPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        val cameraPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            hasCameraPermission = isGranted
        }

        val infiniteTransition = rememberInfiniteTransition(label = "scanner")
        val scanLineOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 180f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scanLine"
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape,
                                color = Primary.copy(alpha = 0.1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "الانضمام كطفل",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = DarkBlue
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = DarkBlue
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Info Banner
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ربط جهاز الطفل بحساب ولي الأمر",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "امسح رمز QR أو أدخل رمز الاقتران لتفعيل الحماية الذكية على هذا الجهاز.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Selector Tabs
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SegmentedButton(
                        selected = selectedMode == 0,
                        onClick = { selectedMode = 0 },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("مسح رمز QR", fontWeight = FontWeight.Bold)
                        }
                    }
                    SegmentedButton(
                        selected = selectedMode == 1,
                        onClick = { selectedMode = 1 },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("الرمز اليدوي", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (selectedMode == 0) {
                    if (!hasCameraPermission) {
                        // Permission Request Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    modifier = Modifier.size(64.dp),
                                    shape = RoundedCornerShape(22.dp),
                                    color = Primary.copy(alpha = 0.1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "إذن الكاميرا مطلوب لمسح الـ QR",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkBlue,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "يرجى منح إذن استخدام الكاميرا للبدء بمسح رمز QR الخاص بحساب ولي الأمر.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                                ) {
                                    Text("السماح باستخدام الكاميرا", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // Real CameraX Live Stream Viewport
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkBlue),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                // Live Camera Preview
                                CameraXPreview(
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Target Frame Overlay
                                Box(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .border(BorderStroke(3.dp, Primary), RoundedCornerShape(20.dp))
                                        .background(Color.Black.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    // Animated Laser Line
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp)
                                            .offset(y = scanLineOffset.dp)
                                            .background(Primary)
                                    )
                                }

                                Text(
                                    text = "وجّه الكاميرا نحو رمز QR على جهاز ولي الأمر",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .padding(bottom = 8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                showSuccessDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "محاكاة مسح الرمز والاتصال",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                } else {
                    // Manual PIN Code Entry
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                modifier = Modifier.size(56.dp),
                                shape = CircleShape,
                                color = Primary.copy(alpha = 0.1f)
                            ) {
                                Icon(Icons.Default.Key, contentDescription = null, tint = Primary, modifier = Modifier.padding(14.dp))
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "أدخل رمز الاقتران اليدوي",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = DarkBlue
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "احصل على رمز الاقتران المكون من 6 أرقام الظاهر في تطبيق ولي الأمر",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            OutlinedTextField(
                                value = manualCode,
                                onValueChange = { if (it.length <= 6) manualCode = it },
                                label = { Text("رمز الاقتران (مثل: 482731)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(22.dp),
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Primary)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { showSuccessDialog = true },
                                enabled = manualCode.length == 6,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                            ) {
                                Text(
                                    text = "تأكيد الاقتران والبدء",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Professional Instructions Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "تعليمات خطوة بخطوة",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkBlue
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        ActivationInstructionRow(
                            step = "١",
                            text = "افتح تطبيق ولي الأمر على جهاز الأب أو الأم."
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ActivationInstructionRow(
                            step = "٢",
                            text = "اختر العائلة ثم اضغط على زر ربط جهاز جديد لعرض رمز QR."
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ActivationInstructionRow(
                            step = "٣",
                            text = "وجه كاميرا هذا الهاتف نحو الرمز أو أدخل الرمز اليدوي."
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF43A047),
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text(
                        text = "تم الاقتران بـ MindGuard بنجاح!",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(
                        text = "تم ربط هذا الجهاز بحساب العائلة وتفعيل حماية الذكاء الاصطناعي بنجاح.",
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            onActivationSuccess()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                    ) {
                        Text("الانتقال للرئيسية")
                    }
                }
            )
        }
    }
}

@Composable
fun ActivationInstructionRow(step: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(28.dp),
            shape = CircleShape,
            color = Color(0xFFE0E7FF)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = step,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextDark
        )
    }
}

@Composable
fun CameraXPreview(
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        modifier = modifier,
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = CameraPreview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(previewView.context))
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ChildActivationScreenPreview() {
    MindShieldAiTheme {
        ChildActivationScreen()
    }
}
