# Implementation Plan - MindShield AI Login Screen Refinement

This plan outlines the steps to update the current login screen to match the provided design image exactly, focusing on the UI/UX details, typography, and RTL support.

## User Review Required

> [!IMPORTANT]
> The design uses specific fonts and icons. I will use **Cairo** from Google Fonts and Material Symbols that best match the brain-shield logo.

## Proposed Changes

### [Design System]

#### [MODIFY] [Color.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/example/mindshieldai/ui/theme/Color.kt)
- Update primary and secondary colors to match the exact shades in the image.
- Add a specific color for the "Remember Me" checkbox and text links.

#### [MODIFY] [Type.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/example/mindshieldai/ui/theme/Type.kt)
- Integrate **Cairo** font using `androidx.compose.ui.text.googlefonts`.
- Adjust font sizes and weights to match the hierarchy in the image.

### [UI Components]

#### [MODIFY] [LoginScreen.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/example/mindshieldai/ui/login/LoginScreen.kt)
- **Logo:** Create a composite icon using a shield and a brain symbol (`Psychology`).
- **Input Fields:** Customize `OutlinedTextField` to match the rounded corners, specific icon placements, and placeholder styles.
- **Button:** Implement the "تسجيل الدخول" button with the arrow icon.
- **Footer:** Add the "أو" divider and the "إنشاء حساب" section with the arrow.
- **Interactions:** Add "Remember Me" checkbox logic (UI only).

## Verification Plan

### Automated Tests
- Build the project to ensure no compilation errors.

### Manual Verification
- Use `render_compose_preview` to verify the UI against the provided image.
- Check RTL layout behavior.
