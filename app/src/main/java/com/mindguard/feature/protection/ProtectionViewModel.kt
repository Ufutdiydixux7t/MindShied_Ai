package com.mindguard.feature.protection

import android.app.Application
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import com.mindguard.platform.accessibility.MindGuardAccessibilityService
import com.mindguard.platform.accessibility.ProtectionPreferences
import com.mindguard.platform.accessibility.StoredProtectionPeriod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProtectionViewModel(application: Application) : AndroidViewModel(application) {

    private var appsLoaded = false

    private val _uiState = MutableStateFlow(ProtectionUiState())
    val uiState: StateFlow<ProtectionUiState> = _uiState.asStateFlow()

    fun navigateToSubScreen(subScreen: ProtectionSubScreen) {
        collapseAllApps()
        _uiState.update { it.copy(activeSubScreen = subScreen) }
    }

    fun navigateBack() {
        collapseAllApps()
        _uiState.update { it.copy(activeSubScreen = null) }
    }

    fun collapseAllApps() {
        _uiState.update { state ->
            state.copy(appProtectionItems = state.appProtectionItems.map { it.copy(isExpanded = false) })
        }
    }

    fun loadInstalledApps() {
        if (appsLoaded) return
        appsLoaded = true

        val application = getApplication<Application>()
        val packageManager = application.packageManager
        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val installedApps = packageManager.queryIntentActivities(launcherIntent, 0)
            .asSequence()
            .filter { it.activityInfo.packageName != application.packageName }
            .distinctBy { it.activityInfo.packageName }
            .map { resolveInfo ->
                val applicationInfo = resolveInfo.activityInfo.applicationInfo
                AppProtectionItemState(
                    id = applicationInfo.packageName,
                    name = resolveInfo.loadLabel(packageManager).toString(),
                    icon = resolveInfo.loadIcon(packageManager),
                    isReelsBlocked = ProtectionPreferences.isReelsEnabled(application, applicationInfo.packageName),
                    reelsBlockDuration = ProtectionPreferences.getReelsLimitMinutes(application, applicationInfo.packageName).toString(),
                    isAppFrozen = ProtectionPreferences.isFrozen(application, applicationInfo.packageName),
                    reelsPeriods = restorePeriods(applicationInfo.packageName),
                    arePeriodsVisible = ProtectionPreferences.arePeriodsEnabled(application, applicationInfo.packageName)
                )
            }
            .sortedBy { it.name.lowercase() }
            .toList()

        // Keep the main list limited to apps known to expose a Reels/Shorts surface.
        // Other apps remain available through the explicit "إضافة تطبيق" action.
        val reelApps = installedApps.filter { it.id in reelSupportedPackages }
        reelApps.forEach { item ->
            if (ProtectionPreferences.getPeriods(application, item.id).isEmpty()) {
                persistPeriods(item)
            }
        }
        _uiState.update {
            it.copy(
                appProtectionItems = reelApps,
                availableApps = installedApps
            )
        }
    }

    private fun restorePeriods(appId: String): List<ReelsTimePeriod> {
        val stored = ProtectionPreferences.getPeriods(getApplication(), appId)
        if (stored.isEmpty()) return defaultPeriods()
        return stored.map { period ->
            ReelsTimePeriod(
                id = period.id,
                name = periodName(period.id),
                startTime = period.start,
                endTime = period.end,
                minutes = period.minutes.toString()
            )
        }
    }

    private fun defaultPeriods() = listOf(
        ReelsTimePeriod("morning", "الصباح", "06:00", "12:00", "10"),
        ReelsTimePeriod("afternoon", "الظهر", "12:00", "17:00", "20"),
        ReelsTimePeriod("evening", "المساء", "17:00", "22:00", "15")
    )

    private fun periodName(id: String) = when (id) {
        "morning" -> "الصباح"
        "afternoon" -> "الظهر"
        "evening" -> "المساء"
        else -> "فترة جديدة"
    }

    fun addApp(appId: String) {
        _uiState.update { state ->
            val app = state.availableApps.firstOrNull { it.id == appId } ?: return@update state
            if (state.appProtectionItems.any { it.id == appId }) return@update state
            if (ProtectionPreferences.getPeriods(getApplication(), appId).isEmpty()) persistPeriods(app)
            state.copy(appProtectionItems = state.appProtectionItems + app)
        }
    }

    fun isAccessibilityServiceEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(
            getApplication<Application>().contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ).orEmpty()
        val component = ComponentName(getApplication(), MindGuardAccessibilityService::class.java)
        val fullName = component.flattenToString()
        val shortName = component.flattenToShortString()
        return enabledServices.split(':').any {
            it.equals(fullName, ignoreCase = true) || it.equals(shortName, ignoreCase = true)
        }
    }

    fun isUsageStatsAccessEnabled(): Boolean {
        val application = getApplication<Application>()
        val appOps = application.getSystemService(AppOpsManager::class.java) ?: return false
        return appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            application.applicationInfo.uid,
            application.packageName
        ) == AppOpsManager.MODE_ALLOWED
    }

    fun toggleAppExpansion(appId: String) {
        _uiState.update { state ->
            state.copy(appProtectionItems = state.appProtectionItems.map { item ->
                if (item.id == appId) item.copy(isExpanded = !item.isExpanded) else item
            })
        }
    }

    fun togglePeriodsVisibility(appId: String) {
        val enabled = _uiState.value.appProtectionItems.firstOrNull { it.id == appId }?.arePeriodsVisible != true
        ProtectionPreferences.setPeriodsEnabled(getApplication(), appId, enabled)
        _uiState.update { state ->
            state.copy(appProtectionItems = state.appProtectionItems.map { item ->
                if (item.id == appId) item.copy(arePeriodsVisible = enabled) else item
            })
        }
    }

    fun toggleAppReelsBlock(appId: String, enabled: Boolean) {
        ProtectionPreferences.setReelsEnabled(getApplication(), appId, enabled)
        _uiState.update { state ->
            state.copy(appProtectionItems = state.appProtectionItems.map { item ->
                if (item.id == appId) item.copy(isReelsBlocked = enabled) else item
            })
        }
    }

    fun setAppReelsDuration(appId: String, duration: String) {
        val minutes = duration.filter(Char::isDigit)
        ProtectionPreferences.setReelsLimitMinutes(getApplication(), appId, minutes.toIntOrNull() ?: 0)
        _uiState.update { state ->
            state.copy(appProtectionItems = state.appProtectionItems.map { item ->
                if (item.id == appId) item.copy(reelsBlockDuration = minutes) else item
            })
        }
    }

    fun toggleAppFreeze(appId: String, enabled: Boolean) {
        ProtectionPreferences.setFreezeEnabled(getApplication(), appId, enabled)
        _uiState.update { state ->
            state.copy(appProtectionItems = state.appProtectionItems.map { item ->
                if (item.id == appId) item.copy(isAppFrozen = enabled) else item
            })
        }
    }

    fun setAppFreezeDuration(appId: String, duration: String) {
        _uiState.update { state ->
            state.copy(appProtectionItems = state.appProtectionItems.map { item ->
                if (item.id == appId) item.copy(appFreezeDuration = duration) else item
            })
        }
    }

    fun updateReelsPeriodMinutes(appId: String, periodId: String, minutes: String) {
        _uiState.update { state ->
            val updatedState = state.copy(appProtectionItems = state.appProtectionItems.map { item ->
                if (item.id != appId) item else item.copy(
                    reelsPeriods = item.reelsPeriods.map { period ->
                        if (period.id == periodId) period.copy(minutes = minutes.filter(Char::isDigit)) else period
                    }
                )
            })
            updatedState.appProtectionItems.firstOrNull { it.id == appId }?.let { persistPeriods(it) }
            updatedState
        }
    }

    fun addReelsPeriod(appId: String) {
        _uiState.update { state ->
            val updatedState = state.copy(appProtectionItems = state.appProtectionItems.map { item ->
                if (item.id != appId) item else item.copy(
                    reelsPeriods = item.reelsPeriods + ReelsTimePeriod(
                        id = "period_${item.reelsPeriods.size + 1}",
                        name = "فترة جديدة",
                        startTime = "22:00",
                        endTime = "23:59",
                        minutes = "10"
                    )
                )
            })
            updatedState.appProtectionItems.firstOrNull { it.id == appId }?.let { persistPeriods(it) }
            updatedState
        }
    }

    private fun persistPeriods(item: AppProtectionItemState) {
        ProtectionPreferences.savePeriods(
            getApplication(), item.id,
            item.reelsPeriods.map { period ->
                StoredProtectionPeriod(
                    id = period.id,
                    start = period.startTime,
                    end = period.endTime,
                    minutes = period.minutes.toIntOrNull() ?: 0
                )
            }
        )
    }

    fun toggleAiFiltering(enabled: Boolean) { _uiState.update { it.copy(isAiFilteringActive = enabled) } }
    fun toggleAdultContent(enabled: Boolean) { _uiState.update { it.copy(isAdultContentBlocked = enabled) } }
    fun toggleSafeBrowsing(enabled: Boolean) { _uiState.update { it.copy(isSafeBrowsingActive = enabled) } }
    fun updateStrictnessLevel(level: Int) { _uiState.update { it.copy(filterStrictnessLevel = level) } }
    fun toggleSleepSchedule(enabled: Boolean) { _uiState.update { it.copy(isSleepScheduleActive = enabled) } }
    fun updateBedtime(start: String, end: String) { _uiState.update { it.copy(bedtimeStart = start, bedtimeEnd = end) } }
    fun toggleLockScreenDuringSleep(enabled: Boolean) { _uiState.update { it.copy(lockScreenDuringSleep = enabled) } }
    fun toggleSettingsLock(enabled: Boolean) { _uiState.update { it.copy(isSettingsLockActive = enabled) } }
    fun toggleBiometrics(enabled: Boolean) { _uiState.update { it.copy(isBiometricsActive = enabled) } }
    fun toggleLockTimeAndDate(enabled: Boolean) { _uiState.update { it.copy(lockTimeAndDateChange = enabled) } }
    fun updatePinCode(pin: String) { _uiState.update { it.copy(pinCode = pin) } }
    fun setBlockDurationEditMode(mode: BlockDurationEditMode) { _uiState.update { it.copy(blockDurationEditMode = mode) } }
    fun togglePreventUninstall(enabled: Boolean) { _uiState.update { it.copy(isPreventUninstallActive = enabled) } }

    companion object {
        private val reelSupportedPackages = setOf(
            "com.instagram.android",
            "com.zhiliaoapp.musically",
            "com.ss.android.ugc.trill",
            "com.google.android.youtube",
            "com.facebook.katana"
        )

    }
}
