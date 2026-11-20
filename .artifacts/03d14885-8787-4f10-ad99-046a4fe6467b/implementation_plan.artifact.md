# Implementation Plan: Child Profile Creation & Device Pairing Flow

Implement the multi-step child creation and device linking flow as requested and illustrated in the user's reference designs:
1. **Create Child Profile Screen (`CreateChildScreen.kt`)**: Allows parent to enter child name ("آدم"), age ("10 سنوات"), and configure 6 key protection rules with toggles (Short videos limit, Protected apps with chips, Rest/sleep time, Content protection, Lock rule changes, Extra time request).
2. **Link Child Device Screen (`LinkChildDeviceScreen.kt`)**: Displays QR code with pairing code (`MG-482731`), Copy & Share actions, step-by-step activation instructions, and a finish button.
3. **Family Screen Integration (`FamilyScreen.kt`)**: Update `FamilyScreen` to show a proper family dashboard with an "إضافة طفل / ربط جهاز جديد" button that launches the new child creation flow.
4. **Navigation Integration**: Connect `FamilyScreen` -> `CreateChildScreen` -> `LinkChildDeviceScreen` via NavController or state-based navigation.

## User Review Required

> [!IMPORTANT]
> The new screens will precisely replicate the UI layouts, colors (`DarkBlue`, `Primary`, `Success`, chips, card shapes), and typography of the provided design screenshots.

## Proposed Changes

### Feature: Family & Child Setup Flow

#### [NEW] [CreateChildScreen.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/mindguard/feature/family/CreateChildScreen.kt)
- Create the "أنشئ ملف طفلك" screen with step indicator ("1 من 2"), child data inputs (Name, Age), and 6 interactive protection rules cards with toggles and chips.
- Include bottom button "التالي: ربط جهاز الطفل".

#### [NEW] [LinkChildDeviceScreen.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/mindguard/feature/family/LinkChildDeviceScreen.kt)
- Create the "ربط جهاز الطفل" screen with QR code container, pairing code (`MG-482731`), copy and share buttons, step-by-step activation instructions, and finish button.

#### [MODIFY] [FamilyScreen.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/mindguard/feature/family/FamilyScreen.kt)
- Redesign `FamilyScreen` to display existing children/devices or an empty state with an "إضافة طفل جديد" button that navigates to `CreateChildScreen`.

#### [MODIFY] [HomeScreen.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/mindguard/feature/self/HomeScreen.kt) or Navigation
- Support navigation routes or internal sub-screens for adding a child.

## Verification Plan

### Automated Tests
- Build project using `./gradlew assembleDebug` to verify compilation.

### Manual Verification
- Test navigation from Family tab -> Add Child -> Configure Rules -> Next -> QR Pairing Screen -> Finish.
