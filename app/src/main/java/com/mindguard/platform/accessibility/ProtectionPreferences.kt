package com.mindguard.platform.accessibility

import android.content.Context
import java.time.LocalDate
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
    private const val REMOVED_APPS_KEY = "removed_protection_apps"
    private const val ADDED_APPS_KEY = "added_protection_apps"

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun appSet(context: Context, key: String): Set<String> =
        prefs(context).getStringSet(key, emptySet()).orEmpty()

    fun isAppRemoved(context: Context, packageName: String) =
        packageName in appSet(context, REMOVED_APPS_KEY)

    fun isAppAdded(context: Context, packageName: String) =
        packageName in appSet(context, ADDED_APPS_KEY)

    fun markAppAdded(context: Context, packageName: String) {
        val removed = appSet(context, REMOVED_APPS_KEY).toMutableSet().apply { remove(packageName) }
        val added = appSet(context, ADDED_APPS_KEY).toMutableSet().apply { add(packageName) }
        prefs(context).edit()
            .putStringSet(REMOVED_APPS_KEY, removed)
            .putStringSet(ADDED_APPS_KEY, added)
            .apply()
    }

    fun markAppRemoved(context: Context, packageName: String) {
        val removed = appSet(context, REMOVED_APPS_KEY).toMutableSet().apply { add(packageName) }
        val added = appSet(context, ADDED_APPS_KEY).toMutableSet().apply { remove(packageName) }
        prefs(context).edit()
            .putStringSet(REMOVED_APPS_KEY, removed)
            .putStringSet(ADDED_APPS_KEY, added)
            .apply()
    }
    private fun reelsKey(packageName: String) = "reels_$packageName"
    private fun reelsLimitKey(packageName: String) = "reels_limit_minutes_$packageName"
    private fun freezeKey(packageName: String) = "freeze_$packageName"
    private fun periodsEnabledKey(packageName: String) = "periods_enabled_$packageName"
    private fun periodsConfiguredKey(packageName: String) = "periods_configured_$packageName"
    private fun periodsKey(packageName: String) = "periods_$packageName"
    private fun usageKey(packageName: String, date: String, periodId: String) = "usage_${packageName}_${date}_$periodId"
    private fun usageSecondsKey(packageName: String, date: String, periodId: String) = "usage_seconds_${packageName}_${date}_$periodId"
    private const val BLOCK_DURATION_MODE_KEY = "block_duration_edit_mode"
    private const val BLOCK_DURATION_LOCK_UNTIL_KEY = "block_duration_edit_lock_until"
    private const val PREVENT_UNINSTALL_KEY = "prevent_uninstall"
    private const val TWO_DAYS_MILLIS = 48L * 60L * 60L * 1000L

    fun getBlockDurationEditMode(context: Context): String =
        prefs(context).getString(BLOCK_DURATION_MODE_KEY, "allowed_now") ?: "allowed_now"

    fun setBlockDurationEditMode(context: Context, mode: String) {
        val editor = prefs(context).edit().putString(BLOCK_DURATION_MODE_KEY, mode)
        if (mode == "after_two_days") {
            editor.putLong(BLOCK_DURATION_LOCK_UNTIL_KEY, System.currentTimeMillis() + TWO_DAYS_MILLIS)
        } else {
            editor.remove(BLOCK_DURATION_LOCK_UNTIL_KEY)
        }
        editor.apply()
    }

    fun canEditBlockDuration(context: Context): Boolean =
        prefs(context).getLong(BLOCK_DURATION_LOCK_UNTIL_KEY, 0L) <= System.currentTimeMillis()

    fun getBlockDurationLockRemainingMillis(context: Context): Long =
        (prefs(context).getLong(BLOCK_DURATION_LOCK_UNTIL_KEY, 0L) - System.currentTimeMillis()).coerceAtLeast(0L)

    fun isPreventUninstallEnabled(context: Context): Boolean =
        prefs(context).getBoolean(PREVENT_UNINSTALL_KEY, false)

    fun setPreventUninstallEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(PREVENT_UNINSTALL_KEY, enabled).apply()
    }

    fun setReelsEnabled(context: Context, packageName: String, enabled: Boolean) {
        prefs(context).edit().putBoolean(reelsKey(packageName), enabled).apply()
    }

    fun setFreezeEnabled(context: Context, packageName: String, enabled: Boolean) {
        prefs(context).edit().putBoolean(freezeKey(packageName), enabled).apply()
    }

    fun isReelsEnabled(context: Context, packageName: String) =
        prefs(context).getBoolean(reelsKey(packageName), false)

    fun setReelsLimitMinutes(context: Context, packageName: String, minutes: Int) {
        prefs(context).edit().putInt(reelsLimitKey(packageName), minutes.coerceAtLeast(0)).apply()
    }

    fun getReelsLimitMinutes(context: Context, packageName: String): Int =
        prefs(context).getInt(reelsLimitKey(packageName), 30)

    fun isFrozen(context: Context, packageName: String) =
        prefs(context).getBoolean(freezeKey(packageName), false)

    fun setPeriodsEnabled(context: Context, packageName: String, enabled: Boolean) {
        prefs(context).edit().putBoolean(periodsEnabledKey(packageName), enabled).apply()
    }

    fun arePeriodsEnabled(context: Context, packageName: String) =
        prefs(context).getBoolean(periodsEnabledKey(packageName), false)

    fun savePeriods(context: Context, packageName: String, periods: List<StoredProtectionPeriod>) {
        val encoded = periods.joinToString(";") {
            "${it.id}|${it.start}|${it.end}|${it.minutes}"
        }
        prefs(context).edit()
            .putString(periodsKey(packageName), encoded)
            .putBoolean(periodsConfiguredKey(packageName), true)
            .apply()
    }

    fun hasSavedPeriods(context: Context, packageName: String): Boolean =
        prefs(context).getBoolean(periodsConfiguredKey(packageName), false)

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

    fun getUsedSeconds(context: Context, packageName: String, date: String, periodId: String): Int =
        prefs(context).getInt(usageSecondsKey(packageName, date, periodId), 0)

    fun getTotalUsedSeconds(context: Context, packageName: String, date: String, periodId: String): Int =
        getUsedMinutes(context, packageName, date, periodId) * 60 +
                getUsedSeconds(context, packageName, date, periodId)

    fun addUsedSeconds(context: Context, packageName: String, date: String, periodId: String, seconds: Int) {
        if (seconds <= 0) return
        val key = usageSecondsKey(packageName, date, periodId)
        val current = getUsedSeconds(context, packageName, date, periodId)
        prefs(context).edit().putInt(key, current + seconds).apply()
    }

    /**
     * Returns only seconds recorded by MindGuard for this package in the
     * half-open local-date range. Both minute and second counters are read so
     * older saved records remain included; arbitrary period IDs are supported.
     */
    fun getRecordedUsageSecondsBetween(
        context: Context,
        packageName: String,
        startInclusive: LocalDate,
        endExclusive: LocalDate
    ): Long {
        if (endExclusive <= startInclusive) return 0L

        val allEntries = prefs(context).all
        val secondsPrefix = "usage_seconds_${packageName}_"
        val minutesPrefix = "usage_${packageName}_"

        fun totalForPrefix(prefix: String, multiplier: Long): Long = allEntries.entries.sumOf { (key, value) ->
            if (!key.startsWith(prefix)) return@sumOf 0L
            val date = runCatching { LocalDate.parse(key.removePrefix(prefix).take(10)) }.getOrNull()
                ?: return@sumOf 0L
            if (date < startInclusive || date >= endExclusive) return@sumOf 0L
            ((value as? Int) ?: 0).coerceAtLeast(0).toLong() * multiplier
        }

        return totalForPrefix(secondsPrefix, 1L) + totalForPrefix(minutesPrefix, 60L)
    }

    /** True only when MindGuard has a saved counter for this package in the date range. */
    fun hasRecordedUsageBetween(
        context: Context,
        packageName: String,
        startInclusive: LocalDate,
        endExclusive: LocalDate
    ): Boolean {
        if (endExclusive <= startInclusive) return false
        val allEntries = prefs(context).all
        val prefixes = listOf("usage_seconds_${packageName}_", "usage_${packageName}_")
        return allEntries.keys.any { key ->
            val prefix = prefixes.firstOrNull { key.startsWith(it) } ?: return@any false
            val date = runCatching { LocalDate.parse(key.removePrefix(prefix).take(10)) }.getOrNull()
                ?: return@any false
            date >= startInclusive && date < endExclusive
        }
    }
}
