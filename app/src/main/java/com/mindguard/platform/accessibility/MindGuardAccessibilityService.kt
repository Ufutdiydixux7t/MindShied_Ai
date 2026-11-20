package com.mindguard.platform.accessibility

import android.accessibilityservice.AccessibilityService
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.time.LocalDate
import java.time.LocalDateTime

class MindGuardAccessibilityService : AccessibilityService() {
    private var trackedPackage: String? = null
    private var trackedSince: LocalDateTime? = null
    private var lastFrozenActionAt = 0L
    private var reelsPackage: String? = null
    private var reelsOverlay: View? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val packageName = event?.packageName?.toString() ?: return
        if (packageName == applicationContext.packageName) return

        val reelsSurface = isShortVideoSurface(event, packageName)
        updateUsage(packageName, reelsSurface)

        if (ProtectionPreferences.isFrozen(this, packageName)) {
            removeReelsOverlay()
            val now = System.currentTimeMillis()
            if (now - lastFrozenActionAt > 1000L) {
                lastFrozenActionAt = now
                performGlobalAction(GLOBAL_ACTION_HOME)
            }
            return
        }

        if (!reelsSurface || !ProtectionPreferences.isReelsEnabled(this, packageName)) return
        if (hasExceededCurrentPeriod(packageName)) {
            showReelsOverlay(packageName)
        }
    }

    private fun hasExceededCurrentPeriod(packageName: String): Boolean {
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

    private fun isShortVideoSurface(event: AccessibilityEvent, packageName: String): Boolean {
        val source = event.source
        val sourceText = "${source?.text ?: ""} ${source?.contentDescription ?: ""} ${source?.viewIdResourceName ?: ""}"
        val eventText = "${event.text.joinToString(" ")} ${event.contentDescription ?: ""} $sourceText".lowercase()
        val isShortVideoLabel = listOf("reels", "shorts", "short video", "ريلز", "الريلز", "مقاطع قصيرة")
            .any { eventText.contains(it) }
        val isHomeLabel = listOf("home", "الرئيسية", "الصفحة الرئيسية")
            .any { eventText.contains(it) }

        if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED && isHomeLabel) {
            if (reelsPackage == packageName) reelsPackage = null
            return false
        }
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED && isShortVideoLabel) {
            reelsPackage = packageName
            return true
        }
        if (reelsPackage == packageName) return true

        val root = rootInActiveWindow ?: return false
        return containsSelectedShortVideoControl(root, 0)
    }

    private fun containsSelectedShortVideoControl(node: AccessibilityNodeInfo, depth: Int): Boolean {
        if (depth > 14) return false
        val value = "${node.text ?: ""} ${node.contentDescription ?: ""} ${node.viewIdResourceName ?: ""}".lowercase()
        val isShortVideoLabel = listOf("reels", "shorts", "ريلز", "الريلز", "short_video")
            .any { value.contains(it) }
        if (isShortVideoLabel && (node.isSelected || node.isChecked)) return true
        for (index in 0 until node.childCount) {
            val child = node.getChild(index) ?: continue
            if (containsSelectedShortVideoControl(child, depth + 1)) return true
        }
        return false
    }

    private fun showReelsOverlay(packageName: String) {
        if (reelsOverlay != null) return
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = 0f
            }
        }

        val title = TextView(this).apply {
            text = "لقد انتهى وقت المشاهدة اليوم"
            textSize = 28f
            setTextColor(Color.rgb(23, 74, 156))
            gravity = Gravity.CENTER
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        val subtitle = TextView(this).apply {
            text = "الاستخدام المفرط للمقاطع القصيرة قد يشتت الانتباه ويؤثر في النوم والتركيز. خذ استراحة قصيرة وامنح عقلك وقتًا للراحة."
            textSize = 16f
            setTextColor(Color.rgb(82, 106, 146))
            gravity = Gravity.CENTER
            setPadding(0, 28, 0, 42)
        }
        val button = Button(this).apply {
            text = "العودة إلى الرئيسية"
            textSize = 17f
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                setColor(Color.rgb(36, 79, 165))
                cornerRadius = 64f
            }
            setOnClickListener {
                removeReelsOverlay()
                reelsPackage = null
                trackedPackage = null
                trackedSince = null
                // Back returns from Shorts/Reels to the target app's normal home/feed.
                performGlobalAction(GLOBAL_ACTION_BACK)
            }
        }

        root.addView(title, LinearLayout.LayoutParams(-1, -2))
        root.addView(subtitle, LinearLayout.LayoutParams(-1, -2))
        root.addView(button, LinearLayout.LayoutParams(-1, 128))

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        windowManager.addView(root, params)
        reelsOverlay = root
    }

    private fun removeReelsOverlay() {
        val overlay = reelsOverlay ?: return
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        runCatching { windowManager.removeView(overlay) }
        reelsOverlay = null
    }

    override fun onInterrupt() {
        removeReelsOverlay()
        trackedPackage = null
        trackedSince = null
        reelsPackage = null
    }

    override fun onDestroy() {
        removeReelsOverlay()
        super.onDestroy()
    }
}
