package com.mindguard.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindguard.R
import com.mindguard.core.theme.DarkBlue
import com.mindguard.core.theme.MindShieldAiTheme
import com.mindguard.core.theme.Primary
import com.mindguard.core.theme.Secondary

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun LoginScreenPreview() {
    MindShieldAiTheme {
        LoginScreen()
    }
}

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onGoogleLoginClick: () -> Unit = {},
    onMicrosoftLoginClick: () -> Unit = {},
    onFacebookLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onChildJoinClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {}
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        var rememberMe by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LoginWaveDecoration(top = true, modifier = Modifier.align(Alignment.TopCenter))
            LoginWaveDecoration(top = false, modifier = Modifier.align(Alignment.BottomCenter))

            // Watermark (علامة مائية خفيفة في الخلفية)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    modifier = Modifier.size(340.dp),
                    tint = DarkBlue.copy(alpha = 0.04f)
                )
            }

            // Language Switcher Icon Button at Top Corner (زر اختيار اللغة في الأعلى)
            IconButton(
                onClick = onLanguageClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "اللغة",
                    tint = DarkBlue,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                // Logo Container
                Surface(
                    modifier = Modifier.size(84.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Primary.copy(alpha = 0.15f),
                    shadowElevation = 0.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "MindGuard Logo",
                            modifier = Modifier.size(48.dp),
                            tint = DarkBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // App Title
                Text(
                    text = "MindGuard",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                        fontSize = 32.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Subtitle
                Text(
                    text = "حماية ذكية ومتقدمة لعائلة آمنة",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        fontSize = 14.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(68.dp))

                // Email / Username Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("البريد الإلكتروني", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    trailingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = Secondary)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkBlue,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("كلمة المرور", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(icon, contentDescription = null, tint = Color.Gray)
                            }
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Secondary, modifier = Modifier.padding(end = 8.dp))
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkBlue,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Remember Me & Forgot Password Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { rememberMe = !rememberMe }
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = DarkBlue,
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            text = "تذكرني",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = DarkBlue,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    TextButton(onClick = onForgotPasswordClick) {
                        Text(
                            text = "نسيت كلمة المرور؟",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = DarkBlue,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Login Button
                Button(
                    onClick = { onLoginClick(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                ) {
                    Text(
                        text = "تسجيل الدخول",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))


                // Divider with "أو"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp),
                        color = Color.LightGray
                    )
                    Text(
                        text = "أو",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp),
                        color = Color.LightGray
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Social Media Borderless Circular Buttons (Google, Microsoft, Facebook)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Google Button
                    Surface(
                        onClick = onGoogleLoginClick,
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = "Google",
                                modifier = Modifier.size(26.dp),
                                tint = Color.Unspecified
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // Microsoft Button
                    Surface(
                        onClick = onMicrosoftLoginClick,
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_microsoft),
                                contentDescription = "Microsoft",
                                modifier = Modifier.size(24.dp),
                                tint = Color.Unspecified
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // Facebook Button
                    Surface(
                        onClick = onFacebookLoginClick,
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_facebook),
                                contentDescription = "Facebook",
                                modifier = Modifier.size(26.dp),
                                tint = Color.Unspecified
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Register Prompt
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "\"ليس لديك حساب؟\"",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                    TextButton(onClick = onRegisterClick) {
                        Text(
                            text = "إنشاء حساب جديد",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = DarkBlue,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Child Join Option (QR / Code)
                TextButton(onClick = onChildJoinClick) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "الانضمام كطفل (عبر QR / الرمز)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Secondary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun LoginWaveDecoration(top: Boolean, modifier: Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(if (top) 105.dp else 125.dp)
    ) {
        val width = size.width
        val height = size.height
        fun wavePath(offset: Float, amplitude: Float): Path = Path().apply {
            if (top) {
                moveTo(0f, 0f)
                lineTo(width, 0f)
                lineTo(width, height * 0.55f + offset)
                cubicTo(
                    width * 0.78f, height * 0.38f + offset - amplitude,
                    width * 0.58f, height * 0.78f + offset + amplitude,
                    width * 0.36f, height * 0.58f + offset
                )
                cubicTo(
                    width * 0.20f, height * 0.42f + offset - amplitude,
                    width * 0.08f, height * 0.72f + offset + amplitude,
                    0f, height * 0.56f + offset
                )
                close()
            } else {
                moveTo(0f, height)
                lineTo(width, height)
                lineTo(width, height * 0.38f + offset)
                cubicTo(
                    width * 0.80f, height * 0.60f + offset + amplitude,
                    width * 0.60f, height * 0.22f + offset - amplitude,
                    width * 0.38f, height * 0.45f + offset
                )
                cubicTo(
                    width * 0.22f, height * 0.62f + offset + amplitude,
                    width * 0.08f, height * 0.30f + offset - amplitude,
                    0f, height * 0.48f + offset
                )
                close()
            }
        }

        if (top) {
            drawPath(wavePath(0f, height * 0.07f), Color(0xFFDCE9FA).copy(alpha = 0.72f))
            drawPath(wavePath(height * 0.08f, height * 0.05f), Color(0xFFC4D9F5).copy(alpha = 0.52f))
            drawPath(wavePath(height * 0.15f, height * 0.04f), Color(0xFFAFC9F1).copy(alpha = 0.34f))
        } else {
            drawPath(wavePath(0f, height * 0.07f), Color(0xFFDCE9FA).copy(alpha = 0.72f))
            drawPath(wavePath(-height * 0.08f, height * 0.05f), Color(0xFFC4D9F5).copy(alpha = 0.52f))
            drawPath(wavePath(-height * 0.15f, height * 0.04f), Color(0xFFAFC9F1).copy(alpha = 0.34f))
        }
    }
}
