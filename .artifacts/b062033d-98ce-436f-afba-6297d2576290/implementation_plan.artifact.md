# Implementation Plan - MindShield AI Splash Screen

This plan outlines the implementation of a modern, premium Splash Screen for the "MindShield AI" application using Jetpack Compose and Material Design 3.

## User Review Required

> [!IMPORTANT]
> - **Navigation:** I will add the `androidx-navigation-compose` dependency to the project to handle the transition from the Splash Screen to the Home/Welcome screens.
> - **Logo:** Since a specific vector logo was not provided, I will implement a modern shield icon using Compose `ImageVector` or a custom `Canvas` drawing to represent the "MindShield AI" brand.
> - **Fonts:** I will use Material 3's default typography with rounded corner modifications as requested.

## Proposed Changes

### Dependencies & Configuration

#### [MODIFY] [libs.versions.toml](file:///D:/src/MindShieldAi/gradle/libs.versions.toml)
- Add `navigation-compose` to `[versions]` and `[libraries]`.

#### [MODIFY] [build.gradle.kts](file:///D:/src/MindShieldAi/app/build.gradle.kts)
- Add the navigation dependency to the `dependencies` block.

### UI & Theming

#### [MODIFY] [Color.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/example/mindshieldai/ui/theme/Color.kt)
- Define the specific color palette:
    - Primary: #1A237E
    - Secondary: #42A5F5
    - Background: #F8FAFC
    - Text: #111827
    - Secondary Text: #6B7280

#### [MODIFY] [Theme.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/example/mindshieldai/ui/theme/Theme.kt)
- Update `LightColorScheme` and `DarkColorScheme` with the new colors.
- Ensure rounded corners are applied to shapes.

#### [MODIFY] [Type.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/example/mindshieldai/ui/theme/Type.kt)
- Configure Material 3 typography for a modern look.

### Components & Screens

#### [NEW] [SplashScreen.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/example/mindshieldai/ui/screens/SplashScreen.kt)
- Implement the Splash Screen layout.
- Add fade-in animation using `Animatable` or `AnimatedVisibility`.
- Implement a 2-second delay logic using `LaunchedEffect`.

#### [NEW] [NavGraph.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/example/mindshieldai/ui/navigation/NavGraph.kt)
- Set up the `NavHost` with routes for Splash, Home, and Welcome screens.

#### [NEW] [PlaceholderScreens.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/example/mindshieldai/ui/screens/PlaceholderScreens.kt)
- Simple "Home" and "Welcome" screens to demonstrate navigation.

### Main Activity

#### [MODIFY] [MainActivity.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/example/mindshieldai/MainActivity.kt)
- Update `setContent` to use the new `NavGraph`.

## Verification Plan

### Automated Tests
- Run `gradle build` to ensure no compilation errors.
- (Optional) Implement a basic UI test to verify the Splash Screen appears.

### Manual Verification
- Deploy the app to an emulator or physical device.
- Observe the 2-second splash screen with fade-in animation.
- Verify RTL support by switching the device language to Arabic.
- Verify navigation to either Home or Welcome screen (simulating login status).
