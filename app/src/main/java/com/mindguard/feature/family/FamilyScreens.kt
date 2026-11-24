package com.mindguard.feature.family

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindguard.core.theme.Primary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FamilySubScreenHeader(title: String, onBack: () -> Unit, stepLabel: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(42.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = MaterialTheme.colorScheme.onSurface)
        }
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (stepLabel != null) {
            Surface(shape = RoundedCornerShape(8.dp), color = Primary.copy(alpha = 0.1f)) {
                Text(stepLabel, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FamilyHomeScreen(
    state: FamilyUiState,
    onAddChild: () -> Unit,
    onOpenChild: (String) -> Unit,
    onOpenRequests: () -> Unit,
    onOpenRewards: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .padding(bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("الأبناء", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedButton(onClick = onAddChild, shape = RoundedCornerShape(18.dp)) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(6.dp))
                Text("إضافة طفل", fontWeight = FontWeight.Bold)
            }
        }

        if (state.children.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(modifier = Modifier.size(56.dp), shape = CircleShape, color = Primary.copy(alpha = 0.1f)) {
                        Icon(Icons.Default.FamilyRestroom, contentDescription = null, tint = Primary, modifier = Modifier.padding(14.dp))
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("لم تتم إضافة أطفال بعد", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "أضف ملفاً محلياً لطفلك. ربط الجهاز ومزامنة البيانات غير متاحين في هذا الإصدار.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onAddChild, shape = RoundedCornerShape(22.dp)) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.size(6.dp))
                        Text("إضافة طفل")
                    }
                }
            }
        } else {
            state.children.forEach { child ->
                FamilyChildCard(child = child, onClick = { onOpenChild(child.id) })
            }
        }

        Text("الإدارة العامة", modifier = Modifier.padding(top = 4.dp), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        FamilyActionCard(
            title = "طلبات الاستثناء",
            subtitle = "مراجعة طلبات وقت إضافي عند توفر جهاز مرتبط",
            icon = Icons.Default.Inbox,
            onClick = onOpenRequests
        )
        FamilyActionCard(
            title = "إدارة المكافآت",
            subtitle = "إضافة المكافآت وتسجيلها محلياً",
            icon = Icons.Default.CardGiftcard,
            onClick = onOpenRewards
        )
    }
}

@Composable
private fun FamilyChildCard(child: FamilyChildProfile, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(14.dp), color = Primary.copy(alpha = 0.1f)) {
                Icon(Icons.Default.Person, contentDescription = child.name, tint = Primary, modifier = Modifier.padding(10.dp))
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(child.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                Text("${child.age} سنوات · ${child.email}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusChip("غير مرتبط", Color(0xFFFFF1D6), Color(0xFF9A6500))
                    StatusChip("إعداد محلي", Color(0xFFEAF1FF), Primary)
                }
                Text("وقت الاستخدام: غير متاح", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun FamilyActionCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(12.dp), color = Color(0xFFEEF4FF)) {
                Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.padding(10.dp))
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun StatusChip(label: String, background: Color, foreground: Color) {
    Surface(shape = RoundedCornerShape(8.dp), color = background) {
        Text(label, modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp), color = foreground, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FamilySavedScreen(child: FamilyChildProfile, onContinue: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(modifier = Modifier.size(72.dp), shape = CircleShape, color = Color(0xFFE8F5E9)) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.padding(16.dp))
        }
        Spacer(Modifier.height(14.dp))
        Text("تم حفظ ملف ${child.name}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text("حُفظت بيانات الطفل وقواعده على جهاز ولي الأمر.", color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(Modifier.height(14.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF6E5)), shape = RoundedCornerShape(16.dp)) {
            Text(
                "حالة الجهاز: غير مرتبط. لم يتم إرسال البيانات أو تطبيق الحماية على جهاز الطفل.",
                modifier = Modifier.padding(14.dp),
                color = Color(0xFF8A5B00),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(22.dp))
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(24.dp)) {
            Text("العودة إلى قائمة الأبناء", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FamilyUnavailableScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        FamilySubScreenHeader("الملف غير متاح", onBack)
        Text(
            "تعذر العثور على ملف الطفل المحفوظ على هذا الجهاز.",
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FamilyChildDetailsScreen(
    child: FamilyChildProfile,
    rewards: List<FamilyReward>,
    onBack: () -> Unit,
    onUpdateChild: (FamilyChildProfile) -> Unit,
    onManageRewards: () -> Unit
) {
    val settings = child.protectionSettings
    Column(modifier = Modifier.fillMaxSize()) {
        FamilySubScreenHeader(child.name, onBack)
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp).padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(52.dp), shape = RoundedCornerShape(16.dp), color = Primary.copy(alpha = 0.1f)) {
                        Icon(Icons.Default.Person, contentDescription = child.name, tint = Primary, modifier = Modifier.padding(13.dp))
                    }
                    Spacer(Modifier.size(12.dp))
                    Column {
                        Text(child.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("${child.age} سنوات", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(child.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            FamilyStatusCard(
                title = "حالة الجهاز والحماية",
                icon = Icons.Default.Devices,
                body = "الجهاز غير مرتبط. قواعد الحماية محفوظة محلياً فقط ولا تُطبّق على جهاز الطفل."
            )
            FamilyStatusCard(
                title = "وقت استخدام الهاتف",
                icon = Icons.Default.AccessTime,
                body = "غير متاح محلياً؛ لا توجد بيانات استخدام من جهاز الطفل."
            )

            Text("قواعد الحماية المحفوظة", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            Text(
                "يمكنك تعديل التفضيلات المحلية. لن تُفعّل على جهاز الطفل حتى يتوفر ربط حقيقي.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            RuleToggleCard(
                title = "حماية المحتوى غير المناسب",
                subtitle = "تفضيل محلي غير مطبق على الجهاز",
                icon = Icons.Default.Shield,
                checked = settings.contentProtectionEnabled,
                onCheckedChange = { onUpdateChild(child.copy(protectionSettings = settings.copy(contentProtectionEnabled = it))) }
            )
            RuleToggleCard(
                title = "تحديد وقت الاستخدام",
                subtitle = "تفضيل محلي؛ لا توجد قراءة أو فرض عن بُعد",
                icon = Icons.Default.AccessTime,
                checked = settings.dailyUsageLimitEnabled,
                onCheckedChange = { onUpdateChild(child.copy(protectionSettings = settings.copy(dailyUsageLimitEnabled = it))) }
            )
            RuleToggleCard(
                title = "الفيديوهات القصيرة",
                subtitle = "تفضيل محلي للتحكم بالمقاطع القصيرة",
                icon = Icons.Default.Star,
                checked = settings.shortVideosLimitEnabled,
                onCheckedChange = { onUpdateChild(child.copy(protectionSettings = settings.copy(shortVideosLimitEnabled = it))) }
            )
            RuleToggleCard(
                title = "تطبيقات محمية",
                subtitle = "تفضيل محفوظ محلياً",
                icon = Icons.Default.Shield,
                checked = settings.protectedAppsEnabled,
                onCheckedChange = { onUpdateChild(child.copy(protectionSettings = settings.copy(protectedAppsEnabled = it))) }
            )
            RuleToggleCard(
                title = "وقت الراحة والنوم",
                subtitle = "تفضيل محفوظ محلياً",
                icon = Icons.Default.AccessTime,
                checked = settings.sleepTimeEnabled,
                onCheckedChange = { onUpdateChild(child.copy(protectionSettings = settings.copy(sleepTimeEnabled = it))) }
            )
            RuleToggleCard(
                title = "تقييد استخدام الجهاز",
                subtitle = "تفضيل محلي؛ لا يوقف الجهاز عن بُعد",
                icon = Icons.Default.Devices,
                checked = settings.deviceRestrictionEnabled,
                onCheckedChange = { onUpdateChild(child.copy(protectionSettings = settings.copy(deviceRestrictionEnabled = it))) }
            )
            RuleToggleCard(
                title = "قفل تغييرات القواعد",
                subtitle = "تفضيل محفوظ محلياً",
                icon = Icons.Default.Shield,
                checked = settings.lockRulesEnabled,
                onCheckedChange = { onUpdateChild(child.copy(protectionSettings = settings.copy(lockRulesEnabled = it))) }
            )
            RuleToggleCard(
                title = "السماح بطلب وقت إضافي",
                subtitle = "لا تصل طلبات من أجهزة أخرى في الوضع المحلي",
                icon = Icons.Default.AccessTime,
                checked = settings.extraTimeEnabled,
                onCheckedChange = { onUpdateChild(child.copy(protectionSettings = settings.copy(extraTimeEnabled = it))) }
            )

            Text("المكافآت", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            if (rewards.none { it.completedAtMillis == null }) {
                Text("لا توجد مكافآت حالية لهذا الطفل.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                rewards.filter { it.completedAtMillis == null }.take(3).forEach { reward ->
                    Text("• ${reward.title} — ${reward.type}", style = MaterialTheme.typography.bodySmall)
                }
            }
            OutlinedButton(onClick = onManageRewards, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                Icon(Icons.Default.CardGiftcard, contentDescription = null)
                Spacer(Modifier.size(6.dp))
                Text("إدارة مكافآت الطفل")
            }
        }
    }
}

@Composable
private fun FamilyStatusCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, body: String) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.size(10.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun FamilyRequestsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        FamilySubScreenHeader("طلبات الاستثناء", onBack)
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(modifier = Modifier.size(58.dp), shape = CircleShape, color = Primary.copy(alpha = 0.1f)) {
                Icon(Icons.Default.Inbox, contentDescription = null, tint = Primary, modifier = Modifier.padding(15.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text("لا توجد طلبات استثناء", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(
                "لا يمكن استقبال الطلبات قبل ربط جهاز الطفل. لم تُنشأ طلبات تجريبية.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FamilyRewardsScreen(
    state: FamilyUiState,
    initialChildId: String?,
    onBack: () -> Unit,
    onAddReward: (childId: String, title: String, type: String, durationMinutes: Int) -> Unit,
    onCompleteReward: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val active = state.rewards.filter { it.completedAtMillis == null && (initialChildId == null || it.childId == initialChildId) }
    val history = state.rewards.filter { it.completedAtMillis != null && (initialChildId == null || it.childId == initialChildId) }
    val childNames = state.children.associate { it.id to it.name }

    Column(modifier = Modifier.fillMaxSize()) {
        FamilySubScreenHeader("إدارة المكافآت", onBack)
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("المكافآت الحالية", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Button(onClick = { showAddDialog = true }, enabled = state.children.isNotEmpty(), shape = RoundedCornerShape(18.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(5.dp))
                    Text("إضافة")
                }
            }
            Text(
                "تُحفظ المكافآت وسجلها على هذا الجهاز فقط؛ لا يتم إرسالها أو منحها تلقائياً للطفل.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (active.isEmpty()) {
                EmptyRewardsCard(if (state.children.isEmpty()) "أضف ملف طفل قبل إنشاء مكافأة." else "لا توجد مكافآت حالية.")
            } else {
                active.forEach { reward ->
                    RewardCard(reward, childNames[reward.childId] ?: "طفل", onComplete = { onCompleteReward(reward.id) })
                }
            }

            Text("سجل المكافآت السابقة", modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            if (history.isEmpty()) {
                Text("لا يوجد سجل مكافآت بعد.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                history.sortedByDescending { it.completedAtMillis }.forEach { reward ->
                    RewardHistoryCard(reward, childNames[reward.childId] ?: "طفل")
                }
            }
        }
    }

    if (showAddDialog) {
        AddRewardDialog(
            children = state.children,
            initialChildId = initialChildId,
            onDismiss = { showAddDialog = false },
            onSave = { childId, title, type, duration ->
                onAddReward(childId, title, type, duration)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun EmptyRewardsCard(message: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Text(message, modifier = Modifier.fillMaxWidth().padding(18.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun RewardCard(reward: FamilyReward, childName: String, onComplete: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Primary)
                Spacer(Modifier.size(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(reward.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("$childName · ${reward.type} · ${reward.durationMinutes} دقيقة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            TextButton(onClick = onComplete, modifier = Modifier.align(Alignment.End)) {
                Text("نقل إلى السجل المحلي", color = Primary)
            }
        }
    }
}

@Composable
private fun RewardHistoryCard(reward: FamilyReward, childName: String) {
    val completedDate = reward.completedAtMillis?.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
    } ?: ""
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(reward.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("$childName · ${reward.type} · ${reward.durationMinutes} دقيقة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(completedDate, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AddRewardDialog(
    children: List<FamilyChildProfile>,
    initialChildId: String?,
    onDismiss: () -> Unit,
    onSave: (childId: String, title: String, type: String, duration: Int) -> Unit
) {
    val types = listOf("وقت إضافي للهاتف", "وقت ألعاب", "نشاط عائلي", "مكافأة مخصصة")
    var title by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("") }
    var selectedChildId by remember(children, initialChildId) {
        mutableStateOf(initialChildId?.takeIf { id -> children.any { it.id == id } } ?: children.firstOrNull()?.id.orEmpty())
    }
    var selectedType by remember { mutableStateOf(types.first()) }
    var childMenuExpanded by remember { mutableStateOf(false) }
    var typeMenuExpanded by remember { mutableStateOf(false) }
    val selectedChildName = children.firstOrNull { it.id == selectedChildId }?.name.orEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة مكافأة", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("اسم المكافأة") },
                    shape = RoundedCornerShape(16.dp)
                )
                Box {
                    OutlinedButton(onClick = { childMenuExpanded = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Text(if (selectedChildName.isBlank()) "اختر الطفل" else selectedChildName, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                    }
                    DropdownMenu(expanded = childMenuExpanded, onDismissRequest = { childMenuExpanded = false }) {
                        children.forEach { child ->
                            DropdownMenuItem(
                                text = { Text(child.name) },
                                onClick = { selectedChildId = child.id; childMenuExpanded = false }
                            )
                        }
                    }
                }
                Box {
                    OutlinedButton(onClick = { typeMenuExpanded = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Text(selectedType, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                    }
                    DropdownMenu(expanded = typeMenuExpanded, onDismissRequest = { typeMenuExpanded = false }) {
                        types.forEach { type ->
                            DropdownMenuItem(text = { Text(type) }, onClick = { selectedType = type; typeMenuExpanded = false })
                        }
                    }
                }
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it.filter(Char::isDigit) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("المدة بالدقائق") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        confirmButton = {
            val duration = durationText.toIntOrNull()
            TextButton(
                onClick = { if (selectedChildId.isNotBlank() && title.isNotBlank() && duration != null && duration > 0) onSave(selectedChildId, title.trim(), selectedType, duration) },
                enabled = selectedChildId.isNotBlank() && title.isNotBlank() && durationText.toIntOrNull()?.let { it > 0 } == true
            ) { Text("حفظ") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}
