package com.mindguard.feature.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindguard.core.theme.DarkBlue
import com.mindguard.core.theme.Primary
import com.mindguard.feature.family.CreateChildScreen
import com.mindguard.feature.family.LinkChildDeviceScreen

sealed class FamilyFlowStep {
    object List : FamilyFlowStep()
    object CreateChild : FamilyFlowStep()
    data class LinkDevice(val childName: String) : FamilyFlowStep()
}

@Composable
fun ParentHomeScreen(
    onAddDeviceClick: () -> Unit = {},
    onSubScreenVisibilityChange: (Boolean) -> Unit = {}
) {
    var currentStep by remember { mutableStateOf<FamilyFlowStep>(FamilyFlowStep.List) }

    LaunchedEffect(currentStep) {
        onSubScreenVisibilityChange(currentStep == FamilyFlowStep.List)
    }

    when (val step = currentStep) {
        is FamilyFlowStep.List -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 92.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(0.2f))

                // Family Illustration / Icon Container
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(
                            color = Primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FamilyRestroom,
                        contentDescription = "Family",
                        tint = Primary,
                        modifier = Modifier.size(80.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Heading
                Text(
                    text = "لم تتم إضافة أطفال بعد",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                        fontSize = 22.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Subtitle description
                Text(
                    text = "أنشئ حساب طفلك، واضبط الحماية المناسبة،\nثم اربط جهازه.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        fontSize = 14.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.weight(0.5f))

                // Add New Device Button
                Button(
                    onClick = {
                        currentStep = FamilyFlowStep.CreateChild
                        onAddDeviceClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ربط جهاز جديد",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        is FamilyFlowStep.CreateChild -> {
            CreateChildScreen(
                onBack = { currentStep = FamilyFlowStep.List },
                onNextToLink = { name -> currentStep = FamilyFlowStep.LinkDevice(name) }
            )
        }
        is FamilyFlowStep.LinkDevice -> {
            LinkChildDeviceScreen(
                childName = step.childName,
                onBack = { currentStep = FamilyFlowStep.CreateChild },
                onFinish = { currentStep = FamilyFlowStep.List }
            )
        }
    }
}
