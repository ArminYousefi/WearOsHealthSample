# ⌚ Wear Health Monitor

A sample Wear OS app that monitors heart rate, exercise metrics, steps, floors, calories, and sleep stages using Health Services and Health Connect, built with Jetpack Compose and Hilt.

---

## 🔧 Tech & Architecture

### Tech Stack

- Kotlin
- Jetpack Compose for Wear OS
- Hilt (Dependency Injection)
- Health Services (Exercise & Passive Monitoring)
- Health Connect (Sleep sessions)
- Coroutines & Flow / StateFlow
- MVVM (ViewModel + Repository + UseCase)

### High‑Level Architecture

- **data**
  - Repositories (`ExerciseRepository`, `SleepRepository`, `UserActivityRepository`)
  - Data models (`ExerciseMetrics`, `SleepSummary`)
  - Direct integration with Health Services & Health Connect
- **domain**
  - Use cases like `GetHealthDataUseCase` to orchestrate repositories
- **presentation**
  - `HealthViewModel`
  - `HealthUiState`
  - Composables such as `HealthDashboardScreen`, `SleepStagesRing`, `FullTrackCircularProgress`
- **di**
  - Hilt module(s) such as `AppModule` to wire up repositories and use cases

This separation keeps platform APIs, business rules, and UI concerns isolated, which makes refactoring and testing much easier.

---

## 🚀 Getting Started

### 1. Clone the repository

git clone https://github.com/ArminYousefi/WearOsHealthSample.git
cd WearOsHealthSample

text

### 2. Open in Android Studio

- Open Android Studio.
- `File` > `Open...` and select the project root.
- Let Gradle sync finish.

### 3. Run on a Wear OS device/emulator

- Select a **Wear OS** run configuration or create one for your device/emulator.
- Click **Run**.
- On the watch, grant permissions when prompted:
  - Activity recognition
  - Heart rate / body sensors
  - Health Connect permissions (if the Health Connect sheet is shown)

---

## 📊 Features

- Live heart rate display with a simple heart icon header.
- Current sleep state label (e.g. Asleep / Awake / Exercise).
- Exercise toggle (start/stop synthetic run) with live updates for:
  - Steps and steps per minute
  - Distance (km)
  - Speed & pace
  - Floors climbed
  - Calories and active time
- Sleep section (when data is available via Health Connect):
  - Total sleep duration
  - Sleep stages ring (`Deep / Light / REM / Awake`)
  - Color legend for each sleep stage
- Calories ring using a full‑track circular progress indicator.
- Centralized theme and color palette for consistent styling across the app.

---

## ✅ Project Best Practices

- **Layered architecture**
  - Keep repositories in `data`, use cases in `domain`, and UI/ViewModel code in `presentation` to avoid tight coupling.
- **Immutable UI state**
  - Use immutable data classes (e.g. `HealthUiState`) and expose them as `StateFlow` in ViewModels for predictable rendering.
- **Theming & colors**
  - Define all colors in the theme module (no inline hex colors in composables) so visual changes are easy and consistent.
- **Dependency injection**
  - Use Hilt with `@HiltViewModel`, constructor injection, and modules in `di` so classes are easy to test and reuse.
- **Permissions handling**
  - Centralize permission logic in a dedicated class (`HealthPermissionManager`) instead of scattering it across Activities or ViewModels.
- **Logging & diagnostics**
  - Log key events (permission results, Health Services/Health Connect status, exercise start/stop) to aid debugging on real devices.
- **GitHub hygiene**
  - Use a proper Android `.gitignore`.
  - Avoid committing secrets (API keys, tokens).
  - Use clear commit messages and meaningful branches/PRs for new features.

---

## 🧭 Roadmap / Ideas

- Add more exercise types (e.g. Walking, Hiking, Cycling) and let the user choose.
- Persist history of exercises and sleep summaries locally.
- Add charts for daily/weekly trends.
- Improve accessibility (font sizes, contrast, TalkBack support).

---

## 🇮🇷 فارسی

<!-- استفاده از HTML برای RTL و راست‌چین شدن در گیت‌هاب -->
<h2 dir="rtl" align="right">معرفی</h2>

<div dir="rtl" align="right">

<p>
اپلیکیشن <b>Wear Health Monitor</b> یک نمونهٔ آموزشی برای <b>Wear OS</b> است که نشان می‌دهد چطور می‌توان:
</p>

<ul>
  <li>متریک‌های زندهٔ تمرین (ضربان قلب، مسافت، سرعت، کالری، طبقات و ارتفاع) را از <b>Health Services</b> دریافت کرد.</li>
  <li>داده‌های خواب شب گذشته و مراحل خواب (عمیق، سبک، <b>REM</b>، بیدار) را از <b>Health Connect</b> خواند.</li>
  <li>مدیریت مجوزهای <b>runtime</b> و <b>Health Connect</b> را در یک کلاس متمرکز انجام داد.</li>
  <li>یک داشبورد سلامت با <b>Jetpack Compose</b> روی صفحهٔ گرد ساعت پیاده‌سازی کرد.</li>
  <li>از معماری <b>MVVM</b> همراه با <b>Hilt</b>، <b>coroutines</b> و <b>Flow</b> برای UI واکنش‌گرا استفاده کرد.</li>
</ul>

<h3>امکانات</h3>

<ul>
  <li>نمایش لحظه‌ای ضربان قلب و وضعیت فعلی خواب/بیداری کاربر.</li>
  <li>سوئیچ شروع/پایان تمرین با داده‌های زنده (گام‌ها، مسافت، سرعت، کالری و ...).</li>
  <li>حلقهٔ مراحل خواب (Deep / Light / REM / Awake) به همراه Legend رنگی.</li>
  <li>نمایش گام‌ها، مسافت (کیلومتر)، <b>pace</b> و تعداد طبقات طی‌شده.</li>
  <li>حلقهٔ کالری با <b>Circular Progress</b> برای نمایش درصد رسیدن به هدف کالری.</li>
  <li>تم و پالت رنگ متمرکز برای یک‌دست بودن ظاهر رابط کاربری.</li>
</ul>

<h3>معماری و ساختار پوشه‌ها</h3>

<ul>
  <li><b>data</b>: ریپازیتوری‌ها، مدل‌های داده و ارتباط مستقیم با <b>Health Services</b> و <b>Health Connect</b>.</li>
  <li><b>domain</b>: <b>Use case</b>ها (مثل <code>GetHealthDataUseCase</code>) برای هماهنگی بین ریپازیتوری‌ها.</li>
  <li><b>presentation</b>: <b>ViewModel</b>‌ها، مدل‌های <b>UI state</b> و کامپوننت‌های Compose رابط کاربری.</li>
  <li><b>di</b>: ماژول‌های <b>Hilt</b> مثل <code>AppModule</code> برای تزریق وابستگی‌ها.</li>
</ul>

<p>
این جداسازی باعث می‌شود تغییر در لایهٔ داده، کمترین تأثیر را روی UI داشته باشد و تست‌نویسی و نگه‌داری پروژه ساده‌تر شود.
</p>

<h3>راه‌اندازی پروژه</h3>

<ol>
  <li>
    <b>کلون کردن مخزن</b><br/>
    <code>git clone https://github.com/ArminYousefi/WearOsHealthSample.git</code>
  </li>
  <li>
    <b>باز کردن در Android Studio</b><br/>
    از منوی de>File &gt; Open</code> پوشهٔ پروژه را انتخاب کنید و منتظر بمانید تا <b>Gradle</b> به طور کامل سینک شود.
  </li>
  <li>
    <b>اجرای اپ روی ساعت یا شبیه‌ساز Wear OS</b><br/>
    کانفیگ اجرای Wear OS را انتخاب کنید، اپ را اجرا کنید و روی ساعت مجوزهای لازم
    (تشخیص فعالیت، سنسورهای بدن / ضربان قلب، مجوزهای Health Connect) را تأیید کنید.
  </li>
</ol>
</div>
