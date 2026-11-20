package com.mindguard.feature.protection

enum class ProtectionSubScreen {
    APP_PROTECTION,
    CONTENT_PROTECTION,
    REST_AND_SLEEP,
    SETTINGS_LOCK,
    ADVANCED_SETTINGS
}

enum class BlockDurationEditMode {
    ALLOWED_NOW,
    ALLOW_AFTER_TWO_DAYS
}

data class AppProtectionItemState(
    val id: String,
    val name: String,
    val isExpanded: Boolean = false,
    val isReelsBlocked: Boolean = true,
    val reelsBlockDuration: String = "30 دقيقة",
    val isAppFrozen: Boolean = true,
    val appFreezeDuration: String = "ساعة"
)

data class ProtectionUiState(
    val activeSubScreen: ProtectionSubScreen? = null,

    // 1. App Protection Apps List
    val appProtectionItems: List<AppProtectionItemState> = listOf(
        AppProtectionItemState(id = "tiktok", name = "TikTok", isExpanded = true, reelsBlockDuration = "30 دقيقة", appFreezeDuration = "ساعة"),
        AppProtectionItemState(id = "instagram", name = "Instagram", isExpanded = false, reelsBlockDuration = "45 دقيقة", appFreezeDuration = "ساعتان"),
        AppProtectionItemState(id = "youtube", name = "YouTube", isExpanded = false, reelsBlockDuration = "30 دقيقة", appFreezeDuration = "ساعة"),
        AppProtectionItemState(id = "snapchat", name = "Snapchat", isExpanded = false, reelsBlockDuration = "15 دقيقة", appFreezeDuration = "30 دقيقة")
    ),

    // 2. Content Protection State
    val isAiFilteringActive: Boolean = true,
    val isAdultContentBlocked: Boolean = true,
    val isSafeBrowsingActive: Boolean = true,
    val filterStrictnessLevel: Int = 2, // 1: Mild, 2: Moderate, 3: Strict

    // 3. Rest and Sleep State
    val isSleepScheduleActive: Boolean = true,
    val bedtimeStart: String = "22:00",
    val bedtimeEnd: String = "07:00",
    val lockScreenDuringSleep: Boolean = true,

    // 4. Settings Lock State
    val isSettingsLockActive: Boolean = true,
    val isBiometricsActive: Boolean = true,
    val lockTimeAndDateChange: Boolean = true,
    val pinCode: String = "1234",

    // 5. Advanced Protection State (الحماية المتقدمة)
    val blockDurationEditMode: BlockDurationEditMode = BlockDurationEditMode.ALLOWED_NOW,
    val isPreventUninstallActive: Boolean = true
)
