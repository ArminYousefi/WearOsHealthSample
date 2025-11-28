# ⌚ Wear Health Monitor

A sample **multi‑module** project for Wear OS + Android phone that shows how to:

- Track heart rate, exercise metrics, steps, floors, calories with **Health Services** on Wear OS.
- Read nightly **sleep sessions and stages** from **Health Connect** on the phone.
- Share domain models and clean architecture between watch and phone.

Built with **Kotlin, Jetpack Compose, Hilt, Coroutines, and MVVM**.

---

## 🧩 Modules

- `:app` – Wear OS app  
  Live exercise metrics, heart rate, and a simple dashboard for the watch.

- `:phone` – Android phone app  
  - Connects to **Health Connect** to read `SleepSessionRecord`s. 
  - Shows a nightly sleep timeline (duration + stages).  
  - Handles Health Connect permissions with the official Activity Result contract.
  - Includes a **debug “Insert fake sleep”** button to seed data on an emulator.

---

## 🔧 Tech & Architecture

### Tech Stack

- Kotlin
- Jetpack Compose (Wear OS + Phone)
- Hilt (Dependency Injection)
- Health Services (exercise & passive metrics)
- Health Connect (sleep sessions & stages) 
- Coroutines / Flow / StateFlow
- MVVM + Use Cases

### High‑Level Architecture

- **data**
  - Repositories (`ExerciseRepository`, `SleepRepository`, `UserActivityRepository`, …)
  - Health Services integrations (Wear)
  - Health Connect integration (Phone)
- **domain**
  - Use cases like:
    - `GetHealthDataUseCase` (Wear dashboard)
    - `GetSleepSessionsUseCase` (Phone sleep timeline)
- **presentation**
  - Wear:
    - `HealthViewModel`, `HealthUiState`
    - Composables: `HealthDashboardScreen`, `SleepStagesRing`, `FullTrackCircularProgress`
  - Phone:
    - `SleepViewModel`, `SleepUiState`
    - Composables: `SleepScreen`, `SleepSessionCard`, per‑stage rows
- **di**
  - Hilt modules wiring:
    - Health Services / repositories (Wear)
    - `HealthConnectClient`, `SleepRepository`, `HealthConnectPermissionManager` (Phone)

This keeps platform APIs (Health Services / Health Connect), domain logic, and UI concerns clearly separated and easy to test.

---

## 🚀 Getting Started

### 1. Clone the repository

git clone https://github.com/ArminYousefi/WearOsHealthSample.git
cd WearOsHealthSample

text

### 2. Open in Android Studio

1. Open Android Studio.
2. `File > Open…` and select the project root.
3. Wait for Gradle sync to finish.

### 3. Run the Wear OS app (`:app`)

1. Create or select a **Wear OS** emulator / device.
2. Choose the `:app` run configuration.
3. Click **Run**.
4. On the watch, grant runtime permissions when prompted:
   - Activity recognition
   - Body sensors / heart rate

### 4. Run the Phone app (`:phone`) with Health Connect

1. Use a **Google Play** phone emulator (or real device) with **Health Connect** installed/enabled.
2. Choose the `:phone` run configuration and click **Run**.
3. On first launch:
   - The app checks `HealthConnectClient.getSdkStatus` and shows a message if HC is unavailable.
   - Tap **Grant Health Connect permission** to open the official permission sheet.
4. After granting **Sleep** permissions, the phone app:
   - Reads sleep sessions from Health Connect for the last 3 days.
   - Renders each night as a card (date, duration, per‑stage breakdown).

### 5. Debug: insert fake sleep data (for emulator only)

Because emulators rarely have real sleep data, the phone app exposes a debug button:

- Tap **Insert fake sleep** to write a synthetic `SleepSessionRecord` for last night (Light + Deep stages).
- The app then reloads and shows:
  - Session date
  - Start/end time
  - Total hours
  - Stage list with time ranges

On a real phone with a sleep‑tracking app connected to Health Connect (e.g. Samsung Health), you can disable or hide this debug button and display only real data.

---

## 📊 Features

### Wear OS app (`:app`)

- Live heart rate with a simple header.
- Exercise toggle (start/stop synthetic run) with live metrics:
  - Steps & cadence (steps/min)
  - Distance (km)
  - Speed & pace
  - Floors climbed / elevation
  - Calories & active time
- Sleep state label (e.g., Asleep / Awake / Exercising) using Health Services.
- Sleep stages ring:
  - Visual breakdown of Deep / Light / REM / Awake
  - Color legend composable
- Calories ring with full‑track circular progress.
- Compose‑based, theme‑aware UI tuned for round watch displays.

### Phone app (`:phone`)

- Health Connect integration:
  - Reads `SleepSessionRecord` + per‑stage `Stage` list via `readRecords`. 
  - Uses a dedicated permission manager for status + grants.
- Sleep timeline UI:
  - One card per night:
    - Date (e.g., `Fri, Nov 28`)
    - Time range (e.g., `18:30 – 02:30`)
    - Total duration (e.g., `8.0 h`)
    - Per‑stage summary (Deep/Light/REM durations)
    - Detailed stage rows (e.g., `Light: 18:30 – 21:30`)
- Debug “Insert fake sleep” button:
  - Writes a synthetic sleep session via `insertRecords`.
  - Useful for emulator demos and screenshots.

---

## ✅ Project Best Practices

- **Layered architecture**
  - Repositories in `data`, use cases in `domain`, ViewModels + Composables in `presentation`.
- **Immutable UI state**
  - State classes exposed as `StateFlow` (e.g., `HealthUiState`, `SleepUiState`) for predictable recomposition.
- **Centralized theming**
  - Colors, typography, and shapes defined once; no magic hex values inside composables.
- **Dependency injection**
  - Hilt with constructor injection and modules per module (`:app`, `:phone`) for easy testing and reuse.
- **Permission handling**
  - Health Connect permission flow encapsulated in `HealthConnectPermissionManager` and used via Activity Result contracts. 
- **Debug tooling**
  - Optional fake data insertion for Health Connect, guarded so it can be disabled in production builds.

---

## 🧭 Roadmap / Ideas

- Sync sleep summaries from Wear OS to the phone and write them to Health Connect.
- Add charts for weekly/monthly sleep & activity trends.
- Persist local history (Room / DataStore) for offline browsing.
- Add export / share of sleep reports.
- Improve accessibility (TalkBack, larger fonts, color‑blind friendly palette).

---

## 🇮🇷 نسخهٔ فارسی

<h2 dir="rtl" align="right">معرفی پروژه</h2>

<div dir="rtl" align="right">

<p>
اپلیکیشن <b>Wear Health Monitor</b> یک نمونهٔ چند ماژوله برای <b>Wear OS</b> و گوشی اندرویدی است که نشان می‌دهد چطور می‌توان:
</p>

<ul>
  <li>متریک‌های تمرین (ضربان قلب، گام‌ها، مسافت، سرعت، کالری و ...) را روی ساعت با <b>Health Services</b> دریافت و نمایش داد.</li>
  <li>داده‌های خواب (سشن‌های خواب و مراحل خواب) را روی گوشی از <b>Health Connect</b> خواند و به صورت <b>تایم‌لاین</b> و کارت‌های شبانه نمایش داد. </li>
  <li>جریان مجوزهای Health Connect (وضعیت SDK، گرفتن مجوز، بررسی مجدد) را در یک کلاس متمرکز مدیریت کرد.</li>
  <li>از معماری تمیز (data / domain / presentation) با <b>MVVM</b>، <b>Hilt</b> و <b>Jetpack Compose</b> برای هر دو ماژول ساعت و گوشی استفاده کرد.</li>
</ul>

<h3>ماژول‌ها</h3>

<ul dir="rtl" align="right">
  <li dir="rtl" align="right"><b>:app</b> – اپ Wear OS<br/>
    داشبورد زندهٔ سلامت روی ساعت، شامل تمرین، ضربان قلب و ویجت‌های Compose مخصوص صفحه گرد.
  </li>
  <li dir="rtl" align="right"><b>:phone</b> – اپ گوشی اندرویدی<br/>
    خواندن داده‌های خواب از Health Connect و نمایش شب‌ها به صورت کارت (تاریخ، ساعت شروع/پایان، مدت کل، مجموع Deep/Light/REM و لیست مراحل).
    همچنین یک دکمهٔ <b>«Insert fake sleep»</b> برای تولید دادهٔ تست روی شبیه‌ساز دارد.
  </li>
</ul>

<h3>راه‌اندازی سریع</h3>

<ol>
  <li>
    <b>کلون کردن مخزن</b><br/>
    <code>git clone https://github.com/ArminYousefi/WearOsHealthSample.git</code><br/>
    <code>cd WearOsHealthSample</code>
  </li>
  <li>
    <b>باز کردن در Android Studio</b><br/>
    از منوی <code>File &gt; Open</code> پوشهٔ پروژه را انتخاب کنید و صبر کنید تا Gradle کامل سینک شود.
  </li>
  <li>
    <b>اجرای ماژول ساعت (:app)</b><br/>
    یک شبیه‌ساز یا دستگاه Wear OS بسازید، کانفیگ اجرا را روی ماژول ساعت بگذارید و اپ را اجرا کنید. روی ساعت مجوزهای لازم (تشخیص فعالیت و سنسورهای بدن) را تأیید کنید.
  </li>
  <li>
    <b>اجرای ماژول گوشی (:phone) همراه با Health Connect</b><br/>
    روی یک شبیه‌ساز/دستگاه دارای Google Play و Health Connect، ماژول گوشی را اجرا کنید.
    در اولین اجرا، دکمهٔ «Grant Health Connect permission» را بزنید و در دیالوگ Health Connect مجوز خواب را تأیید کنید؛ سپس کارت‌های خواب سه روز اخیر را خواهید دید.
  </li>
</ol>

<h3>نکات توسعه</h3>

<ul>
  <li>برای تست روی شبیه‌ساز (که دادهٔ خواب واقعی ندارد) می‌توانید با دکمهٔ <b>Insert fake sleep</b> یک سشن خواب مصنوعی در Health Connect بنویسید و بلافاصله خروجی UI را ببینید.</li>
  <li>روی گوشی واقعی، فقط کافی است یک اپ سلامت (مثلاً Samsung Health) را به Health Connect وصل کنید تا دادهٔ خواب واقعی در همان UI نمایش داده شود. </li>
</ul>

</div>
