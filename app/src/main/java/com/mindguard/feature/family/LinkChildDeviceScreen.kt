package com.mindguard.feature.family

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindguard.core.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkChildDeviceScreen(
    childName: String = "آدم",
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val context = LocalContext.current
        val pairingCode = "MG-482731"

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(28.dp),
                                shape = CircleShape,
                                color = Primary.copy(alpha = 0.1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MindGuard",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    actions = {
                        TextButton(onClick = onFinish) {
                            Text(
                                text = "إنهاء",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "ربط جهاز $childName",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "امسح رمز QR أدناه باستخدام تطبيق طفلك لربط الجهاز بالحساب فوراً",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Realistic QR Code Card Container (Compact)
                Card(
                    modifier = Modifier.size(190.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val qrColor = MaterialTheme.colorScheme.onSurface
                        // Realistic QR Code Canvas drawing
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val canvasWidth = size.width
                            val modulesCount = 25
                            val moduleSize = canvasWidth / modulesCount

                            val seed = 482731L
                            for (x in 0 until modulesCount) {
                                for (y in 0 until modulesCount) {
                                    val isCenterLogoArea = (x in 9..15 && y in 9..15)
                                    val isTopLeftFinder = (x in 0..6 && y in 0..6)
                                    val isTopRightFinder = (x in 18..24 && y in 0..6)
                                    val isBottomLeftFinder = (x in 0..6 && y in 18..24)

                                    if (!isCenterLogoArea && !isTopLeftFinder && !isTopRightFinder && !isBottomLeftFinder) {
                                        val hash = ((x * 31 + y * 17 + seed) % 3) == 0L
                                        if (hash) {
                                            drawRect(
                                                color = qrColor,
                                                topLeft = Offset(x * moduleSize, y * moduleSize),
                                                size = Size(moduleSize, moduleSize)
                                            )
                                        }
                                    }
                                }
                            }

                            fun drawFinderPattern(startX: Float, startY: Float) {
                                val outerSize = 7 * moduleSize
                                val innerSize = 5 * moduleSize
                                val coreSize = 3 * moduleSize

                                drawRect(
                                    color = qrColor,
                                    topLeft = Offset(startX, startY),
                                    size = Size(outerSize, outerSize)
                                )
                                drawRect(
                                    color = Color.White,
                                    topLeft = Offset(startX + moduleSize, startY + moduleSize),
                                    size = Size(innerSize, innerSize)
                                )
                                drawRect(
                                    color = qrColor,
                                    topLeft = Offset(startX + 2 * moduleSize, startY + 2 * moduleSize),
                                    size = Size(coreSize, coreSize)
                                )
                            }

                            drawFinderPattern(0f, 0f)
                            drawFinderPattern((modulesCount - 7) * moduleSize, 0f)
                            drawFinderPattern(0f, (modulesCount - 7) * moduleSize)
                        }

                        // Center Shield Badge in QR
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.5.dp, Primary),
                            shadowElevation = 3.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pairing Code Text & Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = pairingCode,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = Primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Copy & Share buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Pairing Code", pairingCode)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "تم نسخ الرمز بنجاح", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Outlined.ContentCopy, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("نسخ الرمز", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "مشاركة رمز الاقتران: $pairingCode", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Outlined.Share, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مشاركة", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Activation Instructions Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "تعليمات التفعيل السريع",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    InstructionStepItem(
                        stepNumber = "١",
                        text = "ثبّت تطبيق MindGuard على هاتف الطفل",
                        icon = Icons.Outlined.PhoneAndroid
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    InstructionStepItem(
                        stepNumber = "٢",
                        text = "اختر «الانضمام كطفل» من شاشة الترحيب",
                        icon = Icons.Outlined.Link
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    InstructionStepItem(
                        stepNumber = "٣",
                        text = "امسح رمز QR أعلاه أو أدخل رمز الاقتران",
                        icon = Icons.Outlined.QrCodeScanner
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun InstructionStepItem(
    stepNumber: String,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(30.dp),
                shape = CircleShape,
                color = Color(0xFFE0E7FF)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stepNumber,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
