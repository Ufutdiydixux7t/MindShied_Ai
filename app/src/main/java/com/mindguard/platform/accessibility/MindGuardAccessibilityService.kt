package com.mindguard.platform.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Path
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.FrameLayout
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
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.WHITE)
        }
        root.addView(
            OverlayWaveView(this, top = true),
            FrameLayout.LayoutParams(-1, 270).apply { gravity = Gravity.TOP }
        )
        root.addView(
            OverlayWaveView(this, top = false),
            FrameLayout.LayoutParams(-1, 300).apply { gravity = Gravity.BOTTOM }
        )
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(56, 48, 56, 48)
        }

        val title = TextView(this).apply {
            text = "لقد انتهى وقت المشاهدة اليوم"
            textSize = 25f
            setTextColor(Color.rgb(23, 74, 156))
            gravity = Gravity.CENTER
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        val subtitle = TextView(this).apply {
            text = "الاستخدام المفرط للمقاطع القصيرة\nقد يشتت الانتباه ويؤثر في النوم والتركيز.\nخذ استراحة قصيرة وامنح عقلك وقتًا للراحة."
            textSize = 15f
            setTextColor(Color.rgb(82, 106, 146))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 0)
        }
        val buttonWidth = (236 * resources.displayMetrics.density).toInt()
        val buttonHeight = (58 * resources.displayMetrics.density).toInt()
        val button = Button(this).apply {
            text = "العودة إلى الرئيسية"
            textSize = 17f
            setTextColor(Color.WHITE)
            isAllCaps = false
            minHeight = 0
            minWidth = 0
            setPadding(28, 0, 28, 0)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            background = GradientDrawable().apply {
                setColor(Color.rgb(36, 79, 165))
                cornerRadius = 32 * resources.displayMetrics.density
                setStroke((2 * resources.displayMetrics.density).toInt(), Color.rgb(19, 53, 126))
            }
            elevation = 6 * resources.displayMetrics.density
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

        content.addView(title, LinearLayout.LayoutParams(-1, -2).apply {
            bottomMargin = 30
        })
        content.addView(subtitle, LinearLayout.LayoutParams(-1, -2).apply {
            bottomMargin = 44
        })
        content.addView(button, LinearLayout.LayoutParams(buttonWidth, buttonHeight).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        })
        root.addView(
            content,
            FrameLayout.LayoutParams(-1, -1).apply { gravity = Gravity.CENTER }
        )

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

    private class OverlayWaveView(
        context: android.content.Context,
        private val top: Boolean
    ) : View(context) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        override fun onDraw(canvas: android.graphics.Canvas) {
            super.onDraw(canvas)
            val width = width.toFloat()
            val height = height.toFloat()
            val path = Path()
            paint.style = Paint.Style.FILL
            paint.color = Color.rgb(201, 219, 247)
            if (top) {
                path.moveTo(0f, 0f)
                path.lineTo(width * .42f, 0f)
                path.cubicTo(width * .34f, height * .40f, width * .18f, height * .52f, 0f, height * .76f)
                path.close()
            } else {
                path.moveTo(0f, height)
                path.lineTo(width, height)
                path.cubicTo(width * .78f, height * .60f, width * .56f, height * .54f, width * .38f, height)
                path.close()
            }
            canvas.drawPath(path, paint)

            path.reset()
            paint.color = Color.rgb(175, 201, 241)
            if (top) {
                path.moveTo(0f, height * .34f)
                path.cubicTo(width * .25f, height * .08f, width * .48f, height * .20f, width * .66f, 0f)
                path.lineTo(width, 0f)
                path.lineTo(width, height * .14f)
                path.cubicTo(width * .55f, height * .48f, width * .30f, height * .40f, 0f, height * .82f)
            } else {
                path.moveTo(0f, height)
                path.cubicTo(width * .32f, height * .55f, width * .62f, height * .78f, width, height * .20f)
                path.lineTo(width, height)
            }
            path.close()
            canvas.drawPath(path, paint)
        }
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
