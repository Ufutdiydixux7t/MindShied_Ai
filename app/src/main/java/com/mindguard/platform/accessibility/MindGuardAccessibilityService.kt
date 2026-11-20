package com.mindguard.platform.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.mindguard.MainActivity
import java.time.LocalDate
import java.time.LocalDateTime

class MindGuardAccessibilityService : AccessibilityService() {
    private var trackedPackage: String? = null
    private var trackedSince: LocalDateTime? = null
    private var lastBlockedAt = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val packageName = event?.packageName?.toString() ?: return
        if (packageName == applicationContext.packageName) return

        updateUsage(packageName)
        if (!shouldBlock(packageName)) return

        val now = System.currentTimeMillis()
        if (now - lastBlockedAt < 1200L) return
        lastBlockedAt = now

        // Leave the blocked app first. No overlay is created; the user sees MindGuard instead.
        performGlobalAction(GLOBAL_ACTION_HOME)
        val label = runCatching {
            applicationContext.packageManager
                .getApplicationLabel(applicationContext.packageManager.getApplicationInfo(packageName, 0))
                .toString()
        }.getOrDefault(packageName)
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_SHOW_BLOCKED, true)
                putExtra(MainActivity.EXTRA_BLOCKED_APP, label)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        )
    }

    private fun shouldBlock(packageName: String): Boolean {
        if (ProtectionPreferences.isFrozen(this, packageName)) return true
        if (!ProtectionPreferences.isReelsEnabled(this, packageName)) return false

        val period = ProtectionPreferences.currentPeriod(this, packageName) ?: return true
        if (period.minutes <= 0) return true
        val used = ProtectionPreferences.getUsedMinutes(
            this,
            packageName,
            LocalDate.now().toString(),
            period.id
        )
        return used >= period.minutes
    }

    private fun updateUsage(packageName: String) {
        val now = LocalDateTime.now()
        if (trackedPackage != packageName) {
            trackedPackage = packageName
            trackedSince = now
            return
        }

        val since = trackedSince ?: now.also { trackedSince = it }
        if (!ProtectionPreferences.isReelsEnabled(this, packageName)) return
        val period = ProtectionPreferences.currentPeriod(this, packageName) ?: return
        if (java.time.Duration.between(since, now).toMinutes() >= 1) {
            ProtectionPreferences.addUsedMinute(this, packageName, LocalDate.now().toString(), period.id)
            trackedSince = now
        }
    }

    override fun onInterrupt() {
        trackedPackage = null
        trackedSince = null
    }
}
