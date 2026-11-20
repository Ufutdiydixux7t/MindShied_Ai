package com.mindguard.feature.protection

import android.app.Application
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
        _uiState.update { it.copy(activeSubScreen = subScreen) }
    }

    fun navigateBack() {
        _uiState.update { it.copy(activeSubScreen = null) }
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
                    isReelsBlocked = false,
                    isAppFrozen = false
                )
            }
            .sortedBy { it.name.lowercase() }
            .toList()

        val reelApps = installedApps.filter { it.id in reelSupportedPackages }
        reelApps.forEach { persistPeriods(it) }
        _uiState.update {
            it.copy(
                appProtectionItems = reelApps,
                availableApps = installedApps
            )
        }
    }

    fun addApp(appId: String) {
        _uiState.update { state ->
            val app = state.availableApps.firstOrNull { it.id == appId } ?: return@update state
            if (state.appProtectionItems.any { it.id == appId }) return@update state
            persistPeriods(app)
            state.copy(appProtectionItems = state.appProtectionItems + app)
        }
    }

    fun isAccessibilityServiceEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(
            getApplication<Application>().contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ).orEmpty()
        val serviceName = "${getApplication<Application>().packageName}/${MindGuardAccessibilityService::class.java.name}"
        return enabledServices.split(':').any { it.equals(serviceName, ignoreCase = true) }
    }

    // 1. App Protection Handlers
    fun toggleAppExpansion(appId: String) {
        _uiState.update { state ->
            val updatedItems = state.appProtectionItems.map { item ->
                if (item.id == appId) item.copy(isExpanded = !item.isExpanded)
                else item
            }
            state.copy(appProtectionItems = updatedItems)
        }
    }

    fun togglePeriodsVisibility(appId: String) {
        _uiState.update { state ->
            state.copy(appProtectionItems = state.appProtectionItems.map { item ->
                if (item.id == appId) item.copy(arePeriodsVisible = !item.arePeriodsVisible) else item
            })
        }
    }

    fun toggleAppReelsBlock(appId: String, enabled: Boolean) {
        ProtectionPreferences.setReelsEnabled(getApplication(), appId, enabled)
        _uiState.update { state ->
            val updatedItems = state.appProtectionItems.map { item ->
                if (item.id == appId) item.copy(isReelsBlocked = enabled)
                else item
            }
            state.copy(appProtectionItems = updatedItems)
        }
    }

    fun setAppReelsDuration(appId: String, duration: String) {
        _uiState.update { state ->
            val updatedItems = state.appProtectionItems.map { item ->
                if (item.id == appId) item.copy(reelsBlockDuration = duration)
                else item
            }
            state.copy(appProtectionItems = updatedItems)
        }
    }

    fun toggleAppFreeze(appId: String, enabled: Boolean) {
        ProtectionPreferences.setFreezeEnabled(getApplication(), appId, enabled)
        _uiState.update { state ->
            val updatedItems = state.appProtectionItems.map { item ->
                if (item.id == appId) item.copy(isAppFrozen = enabled)
                else item
            }
            state.copy(appProtectionItems = updatedItems)
        }
    }

    fun setAppFreezeDuration(appId: String, duration: String) {
        _uiState.update { state ->
            val updatedItems = state.appProtectionItems.map { item ->
                if (item.id == appId) item.copy(appFreezeDuration = duration)
                else item
            }
            state.copy(appProtectionItems = updatedItems)
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
            getApplication(),
            item.id,
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

    // 2. Content Protection Toggles
    fun toggleAiFiltering(enabled: Boolean) {
        _uiState.update { it.copy(isAiFilteringActive = enabled) }
    }

    fun toggleAdultContent(enabled: Boolean) {
        _uiState.update { it.copy(isAdultContentBlocked = enabled) }
    }

    fun toggleSafeBrowsing(enabled: Boolean) {
        _uiState.update { it.copy(isSafeBrowsingActive = enabled) }
    }

    fun updateStrictnessLevel(level: Int) {
        _uiState.update { it.copy(filterStrictnessLevel = level) }
    }

    // 3. Rest & Sleep Toggles
    fun toggleSleepSchedule(enabled: Boolean) {
        _uiState.update { it.copy(isSleepScheduleActive = enabled) }
    }

    fun updateBedtime(start: String, end: String) {
        _uiState.update { it.copy(bedtimeStart = start, bedtimeEnd = end) }
    }

    fun toggleLockScreenDuringSleep(enabled: Boolean) {
        _uiState.update { it.copy(lockScreenDuringSleep = enabled) }
    }

    // 4. Settings Lock Toggles
    fun toggleSettingsLock(enabled: Boolean) {
        _uiState.update { it.copy(isSettingsLockActive = enabled) }
    }

    fun toggleBiometrics(enabled: Boolean) {
        _uiState.update { it.copy(isBiometricsActive = enabled) }
    }

    fun toggleLockTimeAndDate(enabled: Boolean) {
        _uiState.update { it.copy(lockTimeAndDateChange = enabled) }
    }

    fun updatePinCode(pin: String) {
        _uiState.update { it.copy(pinCode = pin) }
    }

    // 5. Advanced Protection Handlers (الحماية المتقدمة)
    fun setBlockDurationEditMode(mode: BlockDurationEditMode) {
        _uiState.update { it.copy(blockDurationEditMode = mode) }
    }

    fun togglePreventUninstall(enabled: Boolean) {
        _uiState.update { it.copy(isPreventUninstallActive = enabled) }
    }

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
