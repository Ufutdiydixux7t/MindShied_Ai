package com.mindguard.feature.protection

import android.graphics.drawable.Drawable

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

data class ReelsTimePeriod(
    val id: String,
    val name: String,
    val startTime: String,
    val endTime: String,
    val minutes: String = "10"
)

data class AppProtectionItemState(
    val id: String,
    val name: String,
    val icon: Drawable? = null,
    val isExpanded: Boolean = false,
    val isReelsBlocked: Boolean = false,
    val reelsBlockDuration: String = "30",
    val isAppFrozen: Boolean = false,
    val appFreezeDuration: String = "ساعة",
    val reelsPeriods: List<ReelsTimePeriod> = defaultReelsPeriods(),
    val arePeriodsVisible: Boolean = false
)

private fun defaultReelsPeriods() = listOf(
    ReelsTimePeriod("morning", "الصباح", "06:00", "12:00", "10"),
    ReelsTimePeriod("afternoon", "الظهر", "12:00", "17:00", "20"),
    ReelsTimePeriod("evening", "المساء", "17:00", "22:00", "15")
)

data class ProtectionUiState(
    val activeSubScreen: ProtectionSubScreen? = null,

    // 1. App Protection Apps List (loaded from the device PackageManager)
    val appProtectionItems: List<AppProtectionItemState> = emptyList(),
    val availableApps: List<AppProtectionItemState> = emptyList(),

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
    val isPreventUninstallActive: Boolean = false,
    val advancedProtectionMessage: String? = null
)
