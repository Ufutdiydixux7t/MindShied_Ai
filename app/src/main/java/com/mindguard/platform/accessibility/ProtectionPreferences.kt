package com.mindguard.platform.accessibility

import android.content.Context
import java.time.LocalTime

/** Shared, lightweight configuration used by the protection UI and AccessibilityService. */
data class StoredProtectionPeriod(
    val id: String,
    val start: String,
    val end: String,
    val minutes: Int
)

object ProtectionPreferences {
    private const val PREFS_NAME = "mindguard_protection"

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private fun reelsKey(packageName: String) = "reels_$packageName"
    private fun freezeKey(packageName: String) = "freeze_$packageName"
    private fun periodsKey(packageName: String) = "periods_$packageName"
    private fun usageKey(packageName: String, date: String, periodId: String) = "usage_${packageName}_${date}_$periodId"

    fun setReelsEnabled(context: Context, packageName: String, enabled: Boolean) {
        prefs(context).edit().putBoolean(reelsKey(packageName), enabled).apply()
    }

    fun setFreezeEnabled(context: Context, packageName: String, enabled: Boolean) {
        prefs(context).edit().putBoolean(freezeKey(packageName), enabled).apply()
    }

    fun isReelsEnabled(context: Context, packageName: String) =
        prefs(context).getBoolean(reelsKey(packageName), false)

    fun isFrozen(context: Context, packageName: String) =
        prefs(context).getBoolean(freezeKey(packageName), false)

    fun savePeriods(context: Context, packageName: String, periods: List<StoredProtectionPeriod>) {
        val encoded = periods.joinToString(";") {
            "${it.id}|${it.start}|${it.end}|${it.minutes}"
        }
        prefs(context).edit().putString(periodsKey(packageName), encoded).apply()
    }

    fun getPeriods(context: Context, packageName: String): List<StoredProtectionPeriod> {
        return prefs(context).getString(periodsKey(packageName), null)
            ?.split(';')
            ?.mapNotNull { row ->
                val parts = row.split('|')
                if (parts.size != 4) return@mapNotNull null
                StoredProtectionPeriod(
                    id = parts[0],
                    start = parts[1],
                    end = parts[2],
                    minutes = parts[3].toIntOrNull() ?: 0
                )
            }
            ?: emptyList()
    }

    fun currentPeriod(context: Context, packageName: String, now: LocalTime = LocalTime.now()): StoredProtectionPeriod? {
        return getPeriods(context, packageName).firstOrNull { period ->
            val start = runCatching { LocalTime.parse(period.start) }.getOrNull() ?: return@firstOrNull false
            val end = runCatching { LocalTime.parse(period.end) }.getOrNull() ?: return@firstOrNull false
            if (start <= end) now >= start && now < end else now >= start || now < end
        }
    }

    fun getUsedMinutes(context: Context, packageName: String, date: String, periodId: String): Int =
        prefs(context).getInt(usageKey(packageName, date, periodId), 0)

    fun addUsedMinute(context: Context, packageName: String, date: String, periodId: String) {
        val key = usageKey(packageName, date, periodId)
        val current = prefs(context).getInt(key, 0)
        prefs(context).edit().putInt(key, current + 1).apply()
    }
}
