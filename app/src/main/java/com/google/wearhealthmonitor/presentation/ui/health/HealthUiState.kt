package com.google.wearhealthmonitor.presentation.ui.health

/**
 * وضعیت نمایشی (UI State) برای داشبورد سلامت.
 *
 * بهترین‌عمل‌کردها:
 * - این data class فقط داده‌ی نهایی آماده‌ی نمایش را نگه می‌دارد (بدون منطق).
 * - ViewModel وظیفه‌ی نگاشت از لایه‌ی دیتا/دامین به این مدل ساده را دارد (separation of concerns).
 */
data class HealthUiState(
    // مقادیر زنده‌ی فعلی (metrics لحظه‌ای)
    val heartRate: Int = 72,
    // داده‌های فعالیت/ورزش
    val caloriesBurnedKcal: Double = 0.0,
    val distanceMeters: Double = 0.0,
    val speedMps: Double = 0.0,
    val paceMsPerKm: Double = 0.0,
    val elevationGainMeters: Double = 0.0,
    val floors: Double = 0.0,
    val activeSeconds: Long = 0L,
    val steps: Int = 0,
    val stepsPerMin: Int = 0,
    // وضعیت فعلی خواب و پرچم شروع/پایان تمرین
    val currentSleepState: String = "Unknown",
    val isExerciseOn: Boolean = false,
    // داده‌های خواب (خوانده‌شده از Health Connect)
    val totalSleep: String = "0h",
    val deepSleepPercent: Int = 0,
    val lightSleepPercent: Int = 0,
    val remSleepPercent: Int = 0,
    val awakePercent: Int = 0
)