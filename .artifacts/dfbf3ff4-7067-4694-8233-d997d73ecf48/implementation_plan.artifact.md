# Implementation Plan - Enhanced General Settings

This plan details the addition of advanced wellness and security settings to the "General Settings" screen to make the app more functional and professional.

## User Review Required

> [!TIP]
> I have grouped the new settings into logical categories: **Usage Alerts**, **Security**, and **Data Management**.
> These will use `Switch` and `Clickable Rows` consistent with the existing design.

## Proposed Changes

### [UI Components]

#### [MODIFY] [GeneralSettingsScreen.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/example/mindshieldai/ui/home/GeneralSettingsScreen.kt)
- **New Section: Wellness & AI**
    - **Usage Alerts:** Switch to enable/disable pre-limit notifications.
    - **AI Insights:** Switch to toggle personalized behavior analysis.
- **New Section: Security**
    - **Strict Mode:** Switch to prevent settings changes during active protection.
    - **App Lock:** Option to enable biometric/PIN protection for MindShield AI.
- **New Section: Data Management**
    - **Clear Cache/History:** A red-themed action to reset local stats.

## Verification Plan

### Manual Verification
- Open "General Settings".
- Verify the new sections appear with proper Arabic labels and icons.
- Ensure all Switches are functional (state changes visually).
- Check that the layout remains scrollable and looks good in both Light and Dark modes.
