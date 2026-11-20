# تحديث الهوية إلى MindGuard وتطوير الواجهات الأولى وقارئ الـ QR

يتضمن هذا المخطط تنفيذ مجموعة التحديثات والتحسينات البصرية والوظيفية المطلوبة:

1. **تحديث اسم التطبيق الرسمي إلى MindGuard**:
   - تحديث النصوص والعناوين والشعار عبر كافة الشاشات والموارد (`strings.xml`, `HomeTopBar`, `WelcomeScreen`, `LoginScreen`, `SettingsScreen`, إلخ).

2. **تطوير شاشة الترحيب الأولى (`WelcomeScreen.kt`)**:
   - رفع الأزرار الثلاثة إلى منتصف الشاشة وتوزيع المساحات بشكل أنيق ومتوازن.
   - تحسين التصميم البصري للشعار والحاويات بالألوان الأنيقة المتناسقة.

3. **تحديث واجهة تسجيل الدخول (`LoginScreen.kt`)**:
   - **حذف زر "المتابعة بواسطة جوجل"** نهائيًا من واجهة تسجيل الدخول بناءً على الطلب.
   - تنسيق المساحات بين حقول الاسم/كلمة المرور وزر الدخول وزر الانضمام كطفل.

4. **تفعيل قارئ الـ QR المباشر في واجهة الانضمام (`ChildActivationScreen.kt`)**:
   - إضافة إذن الكاميرا `android.permission.CAMERA` في `AndroidManifest.xml`.
   - تفعيل معايينة الكاميرا وقارئ الكود المباشر عبر الكاميرا مع إطار المسح الضوئي، وإمكانية التبديل للرمز اليدوي.

---

## Proposed Changes

### 1. Project Strings & Manifest Configurations

#### [MODIFY] [strings.xml](file:///D:/src/MindShieldAi/app/src/main/res/values/strings.xml)
- تحديث `<string name="app_name">MindGuard</string>`.

#### [MODIFY] [AndroidManifest.xml](file:///D:/src/MindShieldAi/app/src/main/AndroidManifest.xml)
- إضافة `<uses-permission android:name="android.permission.CAMERA" />`.

---

### 2. UI Enhancements & Refactoring

#### [MODIFY] [WelcomeScreen.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/mindguard/feature/auth/WelcomeScreen.kt)
- تحديث اسم التطبيق ليكون **MindGuard**.
- محاذاة الأزرار الثلاثة نحو منتصف الشاشة بتصميم أنيق وجذاب.

#### [MODIFY] [LoginScreen.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/mindguard/feature/auth/LoginScreen.kt)
- **إزالة زر المتابعة بواسطة جوجل** ومحاذاتها بشكل نظيف ومتناسق.
- تحديث النصوص إلى **MindGuard**.

#### [MODIFY] [ChildActivationScreen.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/mindguard/feature/child/ChildActivationScreen.kt)
- تفعيل واجهة قارئ الـ QR التفاعلي مع معاينة الكاميرا وإطار المسح الحقيقي.

#### [MODIFY] Core UI & Feature Screens
تحديث اسم التطبيق إلى MindGuard في:
- [HomeTopBar.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/mindguard/core/components/HomeTopBar.kt)
- [GeneralSettingsScreen.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/mindguard/feature/settings/GeneralSettingsScreen.kt)
- [SettingsScreen.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/mindguard/feature/settings/SettingsScreen.kt)
- [SelfHomeScreen.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/mindguard/feature/self/SelfHomeScreen.kt)
- [UsageScreen.kt](file:///D:/src/MindShieldAi/app/src/main/java/com/mindguard/feature/usage/UsageScreen.kt)

---

## Verification Plan

### Automated Tests
- تشغيل بناء المشروع والتأكد من خلوه من الأخطاء:
  `gradle_build("app:assembleDebug")`
