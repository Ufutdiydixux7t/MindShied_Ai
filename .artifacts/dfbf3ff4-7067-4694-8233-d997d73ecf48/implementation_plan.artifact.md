# Implementation Plan - Statistics Screen

This plan outlines the implementation of the detailed "Statistics" screen in MindShield AI, featuring daily, weekly, and monthly usage insights with interactive charts and summaries.

## User Review Required

> [!IMPORTANT]
> - I will implement a custom **Line Chart** and **Bar Chart** using Jetpack Compose `Canvas` as no third-party charting library is currently in the project.
> - The UI will transition between Daily, Weekly, and Monthly views using a centralized `StatisticsUiState`.

## Proposed Changes

### [Data Modeling]

#### [NEW] [StatisticsUiState.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/example/mindshieldai/ui/home/StatisticsUiState.kt)
- Define `AppUsageData`, `UsagePeriod`, `ChartPoint`, and `StatisticsData` models.
- Create `StatisticsUiState` to hold the data for the three view types (Daily, Weekly, Monthly).
- Include mock data providers for each view type.

### [UI Components]

#### [MODIFY] [HomeScreen.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/example/mindshieldai/ui/home/HomeScreen.kt)
- Update `StatsContent` to:
    - Include a top title "الإحصائيات".
    - Implement a `PrimaryTabRow` or custom `TabRow` for "يومي", "أسبوعي", and "شهري".
    - Render a scrollable `Column` containing:
        1. **Usage Summary Card**: Grid-like layout for Total Usage, Reels, Sessions, and Videos.
        2. **Usage Chart Section**: Line chart for Daily/Weekly, Bar chart for Monthly (implemented via Canvas).
        3. **App Usage List**: Detailed progress bars for each protected app.
        4. **Peak Hours Card**: Highlight the busiest usage time.
        5. **Period Analysis**: List of time slots with completion status.
        6. **AI Insight Card**: AI-driven analysis of usage patterns.
        7. **Weekly Goal Card**: Progress towards the weekly wellness goal.
- Implement an **Empty State** view for when data is unavailable.

### [Chart Implementation]
- **Daily Line Chart**: X-axis (Hours), Y-axis (Minutes).
- **Weekly Line Chart**: X-axis (Days of the week).
- **Monthly Bar Chart**: X-axis (Weeks of the month).
- Draw reference lines for limits (e.g., "Daily Limit: 50 mins").

## Verification Plan

### Manual Verification
- Navigate to the **Statistics** tab from the Bottom Bar.
- Verify that the "Daily" tab is selected by default and displays correct mock data.
- Switch to "Weekly" and "Monthly" tabs; ensure the summary, chart, and lists update accordingly.
- Test the scrolling behavior of the entire screen.
- Verify RTL alignment of chart labels, text, and cards.
- Check accessibility and color contrast in both Light and Dark modes.
