package com.mindguard.feature.family

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

internal object FamilyLocalStore {
    private const val PREFS_NAME = "mindguard_family_local"
    private const val KEY_CHILDREN = "children"
    private const val KEY_REWARDS = "rewards"

    fun load(context: Context): FamilyUiState {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return FamilyUiState(
            children = parseChildren(preferences.getString(KEY_CHILDREN, "[]")),
            rewards = parseRewards(preferences.getString(KEY_REWARDS, "[]"))
        )
    }

    fun save(context: Context, state: FamilyUiState) {
        val children = JSONArray().apply {
            state.children.forEach { child ->
                put(
                    JSONObject()
                        .put("id", child.id)
                        .put("name", child.name)
                        .put("email", child.email)
                        .put("age", child.age)
                        .put("createdAtMillis", child.createdAtMillis)
                        .put("protectionSettings", child.protectionSettings.toJson())
                )
            }
        }
        val rewards = JSONArray().apply {
            state.rewards.forEach { reward ->
                put(
                    JSONObject()
                        .put("id", reward.id)
                        .put("childId", reward.childId)
                        .put("title", reward.title)
                        .put("type", reward.type)
                        .put("durationMinutes", reward.durationMinutes)
                        .put("createdAtMillis", reward.createdAtMillis)
                        .put("completedAtMillis", reward.completedAtMillis ?: JSONObject.NULL)
                )
            }
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_CHILDREN, children.toString())
            .putString(KEY_REWARDS, rewards.toString())
            .apply()
    }

    private fun parseChildren(raw: String?): List<FamilyChildProfile> = runCatching {
        val array = JSONArray(raw ?: "[]")
        buildList {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                add(
                    FamilyChildProfile(
                        id = item.optString("id"),
                        name = item.optString("name"),
                        email = item.optString("email"),
                        age = item.optInt("age"),
                        createdAtMillis = item.optLong("createdAtMillis"),
                        protectionSettings = item.optJSONObject("protectionSettings").toProtectionSettings()
                    )
                )
            }
        }
    }.getOrDefault(emptyList())

    private fun parseRewards(raw: String?): List<FamilyReward> = runCatching {
        val array = JSONArray(raw ?: "[]")
        buildList {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                add(
                    FamilyReward(
                        id = item.optString("id"),
                        childId = item.optString("childId"),
                        title = item.optString("title"),
                        type = item.optString("type"),
                        durationMinutes = item.optInt("durationMinutes"),
                        createdAtMillis = item.optLong("createdAtMillis"),
                        completedAtMillis = if (item.isNull("completedAtMillis")) null else item.optLong("completedAtMillis")
                    )
                )
            }
        }
    }.getOrDefault(emptyList())

    private fun FamilyProtectionSettings.toJson() = JSONObject()
        .put("shortVideosLimitEnabled", shortVideosLimitEnabled)
        .put("protectedAppsEnabled", protectedAppsEnabled)
        .put("sleepTimeEnabled", sleepTimeEnabled)
        .put("contentProtectionEnabled", contentProtectionEnabled)
        .put("lockRulesEnabled", lockRulesEnabled)
        .put("extraTimeEnabled", extraTimeEnabled)
        .put("dailyUsageLimitEnabled", dailyUsageLimitEnabled)
        .put("deviceRestrictionEnabled", deviceRestrictionEnabled)

    private fun JSONObject?.toProtectionSettings(): FamilyProtectionSettings {
        val json = this ?: JSONObject()
        return FamilyProtectionSettings(
            shortVideosLimitEnabled = json.optBoolean("shortVideosLimitEnabled", true),
            protectedAppsEnabled = json.optBoolean("protectedAppsEnabled", true),
            sleepTimeEnabled = json.optBoolean("sleepTimeEnabled", true),
            contentProtectionEnabled = json.optBoolean("contentProtectionEnabled", true),
            lockRulesEnabled = json.optBoolean("lockRulesEnabled", true),
            extraTimeEnabled = json.optBoolean("extraTimeEnabled", true),
            dailyUsageLimitEnabled = json.optBoolean("dailyUsageLimitEnabled", true),
            deviceRestrictionEnabled = json.optBoolean("deviceRestrictionEnabled", false)
        )
    }
}
