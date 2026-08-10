# Implementation Plan - Settings Sub-Screens

This plan covers creating dedicated sub-screens for the categories in the "More" (Settings) tab, moving existing logic to these new screens, and adding placeholders for future features.

## Proposed Changes

### UI Components

#### [MODIFY] [HomeScreen.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/example/mindshieldai/ui/home/HomeScreen.kt)
- **Sub-Navigation State**: Add a state variable within `MoreContent` to track the current sub-screen (Main List, Account, Notifications, Privacy, Security).
- **Sub-Screen Composables**: Create the following new internal composables:
    - `AccountSettingsScreen`: Contains "Change Name" and "Logout".
    - `NotificationsSettingsScreen`: A placeholder for future notification settings.
    - `PrivacySettingsScreen`: A placeholder for privacy and data settings.
    - `SecuritySettingsScreen`: Contains "PIN" and "Fingerprint" options.
- **Navigation Logic**:
    - Update the `SettingsItem` clicks in the main list to switch the `subScreen` state.
    - Add a "Back" button to each sub-screen to return to the main settings list.

## Verification Plan

### Manual Verification
- Deploy to the device and navigate to "المزيد" (More).
- Click on "الحساب" (Account) and verify it opens a new screen with Logout and Change Name.
- Click "الأمان" (Security) and verify it opens a new screen with PIN and Fingerprint.
- Verify the "Back" button in each sub-screen returns you to the main settings list.
- Confirm Notifications and Privacy also open their respective (empty) screens.
