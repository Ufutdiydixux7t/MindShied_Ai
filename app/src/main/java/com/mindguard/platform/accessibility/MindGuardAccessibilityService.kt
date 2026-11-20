package com.mindguard.platform.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
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
    private var activeShortVideoPackage: String? = null
    private var overlayTargetPackage: String? = null
    private var suppressReelsUntil = 0L
    private var reelsOverlay: View? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Keep the event configuration active after Android rebinds the service.
        setServiceInfo(AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOWS_CHANGED or
                    AccessibilityEvent.TYPE_VIEW_CLICKED or
                    AccessibilityEvent.TYPE_VIEW_SELECTED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                    AccessibilityEvent.TYPE_VIEW_SCROLLED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                    AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
            notificationTimeout = 100
        })
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val packageName = event?.packageName?.toString() ?: return
        if (packageName == applicationContext.packageName) return
        if (System.currentTimeMillis() < suppressReelsUntil) return

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
        if (hasExceededReelsLimit(packageName)) {
            showReelsOverlay(packageName)
        }
    }

    private fun hasExceededReelsLimit(packageName: String): Boolean {
        // A manually entered zero always means: show the overlay immediately.
        if (ProtectionPreferences.getReelsLimitMinutes(this, packageName) <= 0) return true
        val period = if (ProtectionPreferences.arePeriodsEnabled(this, packageName)) {
            ProtectionPreferences.currentPeriod(this, packageName) ?: return false
        } else null
        val limit = period?.minutes ?: ProtectionPreferences.getReelsLimitMinutes(this, packageName)
        if (limit <= 0) return true
        val usagePeriodId = period?.id ?: "global"
        val used = ProtectionPreferences.getUsedMinutes(
            this,
            packageName,
            LocalDate.now().toString(),
            usagePeriodId
        )
        return used >= limit
    }

    private fun updateUsage(packageName: String, reelsSurface: Boolean) {
        val now = LocalDateTime.now()
        if (trackedPackage != packageName || !reelsSurface) {
            trackedPackage = if (reelsSurface) packageName else null
            trackedSince = if (reelsSurface) now else null
            return
        }

        val since = trackedSince ?: now.also { trackedSince = it }
        if (!ProtectionPreferences.isReelsEnabled(this, packageName)) {
            trackedPackage = null
            trackedSince = null
            return
        }
        val period = if (ProtectionPreferences.arePeriodsEnabled(this, packageName)) {
            ProtectionPreferences.currentPeriod(this, packageName) ?: return
        } else null
        val usagePeriodId = period?.id ?: "global"
        if (java.time.Duration.between(since, now).toMinutes() >= 1) {
            ProtectionPreferences.addUsedMinute(this, packageName, LocalDate.now().toString(), usagePeriodId)
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
            if (activeShortVideoPackage == packageName) activeShortVideoPackage = null
            return false
        }
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED && isShortVideoLabel) {
            activeShortVideoPackage = packageName
            return true
        }

        if (activeShortVideoPackage == packageName) return true

        if (packageName == YOUTUBE_PACKAGE &&
            YOUTUBE_SHORTS_VIEW_IDS.any { source?.viewIdResourceName?.lowercase()?.contains(it) == true }
        ) {
            activeShortVideoPackage = packageName
            return true
        }

        val root = rootInActiveWindow ?: return false
        if (packageName == YOUTUBE_PACKAGE && containsYoutubeShortsContainer(root, 0)) {
            activeShortVideoPackage = packageName
            return true
        }
        return containsSelectedShortVideoControl(root, 0)
    }

    private fun containsYoutubeShortsContainer(node: AccessibilityNodeInfo, depth: Int): Boolean {
        if (depth > 20) return false
        val viewId = node.viewIdResourceName?.lowercase().orEmpty()
        if (YOUTUBE_SHORTS_VIEW_IDS.any(viewId::contains)) return true
        for (index in 0 until node.childCount) {
            val child = node.getChild(index) ?: continue
            if (containsYoutubeShortsContainer(child, depth + 1)) return true
        }
        return false
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
        overlayTargetPackage = packageName
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
                val targetPackage = overlayTargetPackage
                removeReelsOverlay()
                trackedPackage = null
                trackedSince = null
                activeShortVideoPackage = null
                suppressReelsUntil = System.currentTimeMillis() + 1500L
                overlayTargetPackage = null
                // Let WindowManager restore YouTube's window before reading
                // its accessibility tree. Otherwise rootInActiveWindow may
                // still point to the overlay and the button appears stuck.
                mainHandler.postDelayed({
                    val returnedToHome = if (targetPackage == YOUTUBE_PACKAGE) {
                        clickHomeInCurrentWindow()
                    } else false
                    // If this YouTube build does not expose a clickable Home
                    // node, one normal Back returns from Shorts to YouTube.
                    if (!returnedToHome) performGlobalAction(GLOBAL_ACTION_BACK)
                }, 180L)
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

    private fun clickHomeInCurrentWindow(): Boolean {
        val root = rootInActiveWindow ?: return false
        return clickHomeNode(root, 0)
    }

    private fun clickHomeNode(node: AccessibilityNodeInfo, depth: Int): Boolean {
        if (depth > 20) return false
        val label = "${node.text ?: ""} ${node.contentDescription ?: ""}".lowercase()
        val isHome = listOf("home", "الرئيسية", "الصفحة الرئيسية").any(label::contains)
        if (isHome && node.isVisibleToUser) {
            if (node.isClickable && node.performAction(AccessibilityNodeInfo.ACTION_CLICK)) return true
            val parent = node.parent
            if (parent != null && parent.isVisibleToUser && parent.isClickable) {
                return parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }
        for (index in 0 until node.childCount) {
            val child = node.getChild(index) ?: continue
            if (clickHomeNode(child, depth + 1)) return true
        }
        return false
    }

    override fun onInterrupt() {
        mainHandler.removeCallbacksAndMessages(null)
        removeReelsOverlay()
        trackedPackage = null
        trackedSince = null
        activeShortVideoPackage = null
        overlayTargetPackage = null
    }

    override fun onDestroy() {
        mainHandler.removeCallbacksAndMessages(null)
        removeReelsOverlay()
        activeShortVideoPackage = null
        overlayTargetPackage = null
        super.onDestroy()
    }

    companion object {
        private const val YOUTUBE_PACKAGE = "com.google.android.youtube"
        private val YOUTUBE_SHORTS_VIEW_IDS = listOf(
            "reel_watch_fragment_root",
            "reel_recycler",
            "reel_progress_bar",
            "shorts_player"
        )
    }
}
