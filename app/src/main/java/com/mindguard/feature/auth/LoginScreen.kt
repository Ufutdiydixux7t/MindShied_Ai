package com.mindguard.feature.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.mindguard.core.theme.MindShieldAiTheme

private val LoginNavy = Color(0xFF15345D)
private val LoginRoyalBlue = Color(0xFF3478C8)
private val LoginLightBlue = Color(0xFFD7E7FA)
private val LoginSoftBlue = Color(0xFFF3F8FE)
private val LoginMuted = Color(0xFF64758B)
private val LoginBorder = Color(0xFFD5E3F3)

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
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFF0F6FD), Color.White, Color(0xFFF5F9FE))
                    )
                )
        ) {
            LoginWaveDecoration(top = true, modifier = Modifier.align(Alignment.TopCenter))
            LoginWaveDecoration(top = false, modifier = Modifier.align(Alignment.BottomCenter))

            // Watermark (علامة مائية خفيفة في الخلفية)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_logo),
                    contentDescription = null,
                    modifier = Modifier.size(280.dp),
                    alpha = 0.025f
                )
            }

            // Language Switcher Icon Button at Top Corner (زر اختيار اللغة في الأعلى)
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 14.dp, end = 16.dp)
                    .size(46.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.82f),
                shadowElevation = 3.dp
            ) {
                IconButton(onClick = onLanguageClick, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "اللغة",
                        tint = LoginNavy,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(42.dp))

                // Logo Container
                Surface(
                    modifier = Modifier
                        .size(96.dp)
                        .border(1.dp, Color.White, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "MindGuard Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(22.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // App Title
                Text(
                    text = "MindGuard",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = LoginNavy,
                        fontSize = 34.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Subtitle
                Text(
                    text = "حماية ذكية ومتقدمة لعائلة آمنة",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LoginMuted,
                        fontSize = 14.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(34.dp))

                // Email / Username Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("البريد الإلكتروني", color = LoginMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = LoginRoyalBlue)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LoginRoyalBlue,
                        unfocusedBorderColor = LoginBorder,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = LoginRoyalBlue
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("كلمة المرور", color = LoginMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = LoginRoyalBlue)
                    },
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = if (passwordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور",
                                tint = LoginRoyalBlue
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LoginRoyalBlue,
                        unfocusedBorderColor = LoginBorder,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = LoginRoyalBlue
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
                                checkedColor = LoginRoyalBlue,
                                uncheckedColor = LoginMuted
                            )
                        )
                        Text(
                            text = "تذكرني",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = LoginNavy,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    TextButton(onClick = onForgotPasswordClick) {
                        Text(
                            text = "نسيت كلمة المرور؟",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = LoginRoyalBlue,
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
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LoginNavy),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp)
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
                        color = LoginBorder
                    )
                    Text(
                        text = "أو",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(color = LoginMuted)
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp),
                        color = LoginBorder
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
                        modifier = Modifier.size(56.dp).border(1.dp, LoginBorder, CircleShape),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp
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
                        modifier = Modifier.size(56.dp).border(1.dp, LoginBorder, CircleShape),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp
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
                        modifier = Modifier.size(56.dp).border(1.dp, LoginBorder, CircleShape),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp
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
                        text = "ليس لديك حساب؟",
                        style = MaterialTheme.typography.bodyMedium.copy(color = LoginMuted)
                    )
                    TextButton(onClick = onRegisterClick) {
                        Text(
                            text = "إنشاء حساب جديد",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = LoginRoyalBlue,
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
                            tint = LoginRoyalBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "الانضمام كطفل (عبر QR / الرمز)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = LoginNavy,
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
            .height(if (top) 130.dp else 160.dp)
    ) {
        val width = size.width
        val height = size.height
        fun wavePath(layer: Int): Path = Path().apply {
            val rightEdge = when (layer) {
                0 -> if (top) 0.43f else 0.52f
                1 -> if (top) 0.56f else 0.41f
                else -> if (top) 0.68f else 0.30f
            }
            val leftEdge = when (layer) {
                0 -> if (top) 0.56f else 0.62f
                1 -> if (top) 0.68f else 0.49f
                else -> if (top) 0.77f else 0.40f
            }
            val middle = when (layer) {
                0 -> if (top) 0.46f else 0.53f
                1 -> if (top) 0.58f else 0.44f
                else -> if (top) 0.70f else 0.33f
            }
            if (top) {
                moveTo(0f, 0f)
                lineTo(width, 0f)
                lineTo(width, height * rightEdge)
                cubicTo(
                    width * 0.82f, height * (rightEdge - 0.13f),
                    width * 0.66f, height * (middle - 0.16f),
                    width * 0.45f, height * middle
                )
                cubicTo(
                    width * 0.27f, height * (leftEdge + 0.08f),
                    width * 0.12f, height * (leftEdge - 0.13f),
                    0f, height * leftEdge
                )
                close()
            } else {
                moveTo(0f, height)
                lineTo(width, height)
                lineTo(width, height * rightEdge)
                cubicTo(
                    width * 0.82f, height * (rightEdge + 0.15f),
                    width * 0.66f, height * (middle - 0.13f),
                    width * 0.45f, height * middle
                )
                cubicTo(
                    width * 0.27f, height * (leftEdge + 0.10f),
                    width * 0.12f, height * (leftEdge - 0.13f),
                    0f, height * leftEdge
                )
                close()
            }
        }

        if (top) {
            drawPath(
                path = wavePath(0),
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFC5D9F2).copy(alpha = 0.86f), LoginSoftBlue.copy(alpha = 0.96f)),
                    startY = 0f,
                    endY = height
                )
            )
            drawPath(
                path = wavePath(1),
                brush = Brush.verticalGradient(
                    colors = listOf(LoginLightBlue.copy(alpha = 0.54f), Color.White.copy(alpha = 0.68f)),
                    startY = 0f,
                    endY = height
                )
            )
            drawPath(
                path = wavePath(2),
                brush = Brush.verticalGradient(
                    colors = listOf(LoginRoyalBlue.copy(alpha = 0.20f), LoginLightBlue.copy(alpha = 0.38f)),
                    startY = 0f,
                    endY = height
                )
            )
        } else {
            drawPath(
                path = wavePath(0),
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFDCE9F8).copy(alpha = 0.32f), Color(0xFFC5D9F4).copy(alpha = 0.94f)),
                    startY = 0f,
                    endY = height
                )
            )
            drawPath(
                path = wavePath(1),
                brush = Brush.verticalGradient(
                    colors = listOf(LoginLightBlue.copy(alpha = 0.42f), Color(0xFF91B6E7).copy(alpha = 0.60f)),
                    startY = 0f,
                    endY = height
                )
            )
            drawPath(
                path = wavePath(2),
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFB9D1F0).copy(alpha = 0.34f), LoginRoyalBlue.copy(alpha = 0.24f)),
                    startY = 0f,
                    endY = height
                )
            )
        }
    }
}
