package com.mindguard.feature.family

data class FamilyProtectionSettings(
    val shortVideosLimitEnabled: Boolean = true,
    val protectedAppsEnabled: Boolean = true,
    val sleepTimeEnabled: Boolean = true,
    val contentProtectionEnabled: Boolean = true,
    val lockRulesEnabled: Boolean = true,
    val extraTimeEnabled: Boolean = true,
    val dailyUsageLimitEnabled: Boolean = true,
    val deviceRestrictionEnabled: Boolean = false
)

data class FamilyChildDraft(
    val name: String,
    val email: String,
    val age: Int,
    val protectionSettings: FamilyProtectionSettings
)

data class FamilyChildProfile(
    val id: String,
    val name: String,
    val email: String,
    val age: Int,
    val createdAtMillis: Long,
    val protectionSettings: FamilyProtectionSettings
)

data class FamilyReward(
    val id: String,
    val childId: String,
    val title: String,
    val type: String,
    val durationMinutes: Int,
    val createdAtMillis: Long,
    val completedAtMillis: Long? = null
)

data class FamilyUiState(
    val children: List<FamilyChildProfile> = emptyList(),
    val rewards: List<FamilyReward> = emptyList()
)
