package com.mindguard.feature.protection

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProtectionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProtectionUiState())
    val uiState: StateFlow<ProtectionUiState> = _uiState.asStateFlow()

    fun navigateToSubScreen(subScreen: ProtectionSubScreen) {
        _uiState.update { it.copy(activeSubScreen = subScreen) }
    }

    fun navigateBack() {
        _uiState.update { it.copy(activeSubScreen = null) }
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

    fun toggleAppReelsBlock(appId: String, enabled: Boolean) {
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
}
