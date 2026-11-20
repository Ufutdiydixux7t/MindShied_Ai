# Remove Welcome Screen and Set Login Screen as Start Destination

Remove the initial welcome screen (`WelcomeScreen.kt`), remove its route from `AppRoute.kt`, update `AppNavHost.kt` to start directly at `LoginScreen`, and delete `WelcomeScreen.kt` file.

## User Review Required

> [!IMPORTANT]
> This will remove `WelcomeScreen.kt` and make the newly styled `LoginScreen` the starting screen of the application. Please confirm if you would like to delete `WelcomeScreen.kt` from the project.

## Open Questions

- None.

## Proposed Changes

### Navigation & Auth Feature

#### [MODIFY] [AppRoute.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/mindguard/navigation/AppRoute.kt)
- Remove `object Welcome : AppRoute("welcome")`.

#### [MODIFY] [AppNavHost.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/mindguard/navigation/AppNavHost.kt)
- Change `startDestination` from `AppRoute.Welcome.route` to `AppRoute.Login.route`.
- Remove the `composable(AppRoute.Welcome.route)` block.
- Update `popUpTo` references from `AppRoute.Welcome.route` to `AppRoute.Login.route` (or remove popUpTo where appropriate).

#### [DELETE] [WelcomeScreen.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/mindguard/feature/auth/WelcomeScreen.kt)
- Delete the file entirely.

## Verification Plan

### Automated Tests
- Run Gradle build (`gradle_build` -> `app:assembleDebug`) to ensure no compilation errors remain after removing `WelcomeScreen`.

### Manual Verification
- Verify app starts directly on the Login screen.
