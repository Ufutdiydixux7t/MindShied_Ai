package com.mindguard.feature.parent

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.CompositionLocalProvider
import com.mindguard.feature.family.CreateChildScreen
import com.mindguard.feature.family.FamilyChildDraft
import com.mindguard.feature.family.FamilyChildDetailsScreen
import com.mindguard.feature.family.FamilyHomeScreen
import com.mindguard.feature.family.FamilyRequestsScreen
import com.mindguard.feature.family.FamilyRewardsScreen
import com.mindguard.feature.family.FamilySavedScreen
import com.mindguard.feature.family.FamilyUnavailableScreen
import com.mindguard.feature.family.FamilyViewModel
import com.mindguard.feature.family.LinkChildDeviceScreen

sealed class FamilyFlowStep {
    data object List : FamilyFlowStep()
    data object CreateChild : FamilyFlowStep()
    data class LinkDevice(val draft: FamilyChildDraft) : FamilyFlowStep()
    data class Saved(val childId: String) : FamilyFlowStep()
    data class ChildDetails(val childId: String) : FamilyFlowStep()
    data object ExceptionRequests : FamilyFlowStep()
    data class Rewards(val childId: String?, val returnTo: FamilyFlowStep) : FamilyFlowStep()
}

@Composable
fun ParentHomeScreen(
    onSubScreenVisibilityChange: (Boolean) -> Unit = {}
) {
    val familyViewModel: FamilyViewModel = viewModel()
    val uiState by familyViewModel.uiState.collectAsState()
    var currentStep by remember { mutableStateOf<FamilyFlowStep>(FamilyFlowStep.List) }
    var currentDraft by remember { mutableStateOf<FamilyChildDraft?>(null) }

    BackHandler(enabled = currentStep !is FamilyFlowStep.List) {
        currentStep = when (val step = currentStep) {
            FamilyFlowStep.CreateChild -> FamilyFlowStep.List
            is FamilyFlowStep.LinkDevice -> FamilyFlowStep.CreateChild
            is FamilyFlowStep.Saved -> FamilyFlowStep.List
            is FamilyFlowStep.ChildDetails -> FamilyFlowStep.List
            FamilyFlowStep.ExceptionRequests -> FamilyFlowStep.List
            is FamilyFlowStep.Rewards -> step.returnTo
            FamilyFlowStep.List -> FamilyFlowStep.List
        }
    }

    LaunchedEffect(currentStep) {
        onSubScreenVisibilityChange(currentStep == FamilyFlowStep.List)
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        when (val step = currentStep) {
            FamilyFlowStep.List -> FamilyHomeScreen(
                state = uiState,
                onAddChild = {
                    currentDraft = null
                    currentStep = FamilyFlowStep.CreateChild
                },
                onOpenChild = { childId -> currentStep = FamilyFlowStep.ChildDetails(childId) },
                onOpenRequests = { currentStep = FamilyFlowStep.ExceptionRequests },
                onOpenRewards = { currentStep = FamilyFlowStep.Rewards(childId = null, returnTo = FamilyFlowStep.List) }
            )

            FamilyFlowStep.CreateChild -> CreateChildScreen(
                onBack = {
                    currentDraft = null
                    currentStep = FamilyFlowStep.List
                },
                onNextToLink = { draft ->
                    currentDraft = draft
                    currentStep = FamilyFlowStep.LinkDevice(draft)
                },
                initialDraft = currentDraft
            )

            is FamilyFlowStep.LinkDevice -> LinkChildDeviceScreen(
                draft = step.draft,
                onBack = { currentStep = FamilyFlowStep.CreateChild },
                onSaveLocally = {
                    val child = familyViewModel.addChild(step.draft)
                    currentDraft = null
                    currentStep = FamilyFlowStep.Saved(child.id)
                }
            )

            is FamilyFlowStep.Saved -> {
                val child = uiState.children.firstOrNull { it.id == step.childId }
                if (child != null) {
                    FamilySavedScreen(child = child, onContinue = { currentStep = FamilyFlowStep.List })
                } else {
                    FamilyUnavailableScreen(onBack = { currentStep = FamilyFlowStep.List })
                }
            }

            is FamilyFlowStep.ChildDetails -> {
                val child = uiState.children.firstOrNull { it.id == step.childId }
                if (child != null) {
                    FamilyChildDetailsScreen(
                        child = child,
                        rewards = uiState.rewards.filter { it.childId == child.id },
                        onBack = { currentStep = FamilyFlowStep.List },
                        onUpdateChild = familyViewModel::updateChild,
                        onManageRewards = {
                            currentStep = FamilyFlowStep.Rewards(childId = child.id, returnTo = step)
                        }
                    )
                } else {
                    FamilyUnavailableScreen(onBack = { currentStep = FamilyFlowStep.List })
                }
            }

            FamilyFlowStep.ExceptionRequests -> FamilyRequestsScreen(
                onBack = { currentStep = FamilyFlowStep.List }
            )

            is FamilyFlowStep.Rewards -> FamilyRewardsScreen(
                state = uiState,
                initialChildId = step.childId,
                onBack = { currentStep = step.returnTo },
                onAddReward = familyViewModel::addReward,
                onCompleteReward = familyViewModel::completeReward
            )
        }
    }
}
