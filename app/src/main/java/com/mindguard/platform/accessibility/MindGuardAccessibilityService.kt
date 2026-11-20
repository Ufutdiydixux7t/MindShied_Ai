package com.mindguard.platform.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
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

        val reelsSurface = isShortVideoSurface(event)
        updateUsage(packageName, reelsSurface)
        if (!shouldBlock(packageName, reelsSurface)) return

        val now = System.currentTimeMillis()
        if (now - lastBlockedAt < 1200L) return
        lastBlockedAt = now

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
                putExtra(MainActivity.EXTRA_BLOCKED_PACKAGE, packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        )
    }

    private fun shouldBlock(packageName: String, reelsSurface: Boolean): Boolean {
        if (ProtectionPreferences.isFrozen(this, packageName)) return true
        if (!ProtectionPreferences.isReelsEnabled(this, packageName) || !reelsSurface) return false

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

    private fun updateUsage(packageName: String, reelsSurface: Boolean) {
        val now = LocalDateTime.now()
        if (trackedPackage != packageName || !reelsSurface) {
            trackedPackage = if (reelsSurface) packageName else null
            trackedSince = if (reelsSurface) now else null
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

    private fun isShortVideoSurface(event: AccessibilityEvent): Boolean {
        val root = rootInActiveWindow ?: return false
        return containsShortVideoText(root, 0)
    }

    private fun containsShortVideoText(node: AccessibilityNodeInfo, depth: Int): Boolean {
        if (depth > 12) return false
        val value = "${node.text ?: ""} ${node.contentDescription ?: ""} ${node.viewIdResourceName ?: ""}".lowercase()
        val isShortVideoLabel = listOf("reels", "shorts", "ريلز", "الريلز", "short_video")
            .any { value.contains(it) }
        val isActiveControl = node.isSelected || node.isChecked
        if (isShortVideoLabel && isActiveControl) return true
        for (index in 0 until node.childCount) {
            val child = node.getChild(index) ?: continue
            if (containsShortVideoText(child, depth + 1)) return true
        }
        return false
    }

    override fun onInterrupt() {
        trackedPackage = null
        trackedSince = null
    }
}
