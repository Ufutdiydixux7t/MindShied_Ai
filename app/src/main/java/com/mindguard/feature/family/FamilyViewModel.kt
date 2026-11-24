package com.mindguard.feature.family

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class FamilyViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(FamilyLocalStore.load(application))
    val uiState = _uiState.asStateFlow()

    fun addChild(draft: FamilyChildDraft): FamilyChildProfile {
        val child = FamilyChildProfile(
            id = UUID.randomUUID().toString(),
            name = draft.name.trim(),
            email = draft.email.trim(),
            age = draft.age,
            createdAtMillis = System.currentTimeMillis(),
            protectionSettings = draft.protectionSettings
        )
        updateState(_uiState.value.copy(children = _uiState.value.children + child))
        return child
    }

    fun updateChild(updated: FamilyChildProfile) {
        updateState(
            _uiState.value.copy(
                children = _uiState.value.children.map { current ->
                    if (current.id == updated.id) updated else current
                }
            )
        )
    }

    fun addReward(childId: String, title: String, type: String, durationMinutes: Int) {
        val reward = FamilyReward(
            id = UUID.randomUUID().toString(),
            childId = childId,
            title = title.trim(),
            type = type,
            durationMinutes = durationMinutes,
            createdAtMillis = System.currentTimeMillis()
        )
        updateState(_uiState.value.copy(rewards = _uiState.value.rewards + reward))
    }

    fun completeReward(rewardId: String) {
        val completedAt = System.currentTimeMillis()
        updateState(
            _uiState.value.copy(
                rewards = _uiState.value.rewards.map { reward ->
                    if (reward.id == rewardId && reward.completedAtMillis == null) {
                        reward.copy(completedAtMillis = completedAt)
                    } else {
                        reward
                    }
                }
            )
        )
    }

    private fun updateState(state: FamilyUiState) {
        _uiState.value = state
        FamilyLocalStore.save(getApplication<Application>(), state)
    }
}
