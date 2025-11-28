package com.google.wearhealthmonitor.data.model

/**
 * مدل خلاصه‌ی خواب برای استفاده در لایه‌های بالاتر (use case / ViewModel / UI).
 *
 * - این data class فقط یک نمای ساده و تجمیع‌شده از داده‌های خام خواب (SleepSessionRecord و stageها)
 * - هر فیلد بر حسب دقیقه است تا محاسبه‌ی درصدها، گراف‌ها و نمایش در UI ساده و سرراست باشد.
 *
 * @property totalMinutes مجموع کل دقایق خواب (شامل deep، light، REM و awake)
 * @property deepMinutes مجموع دقایق خواب عمیق
 * @property lightMinutes مجموع دقایق خواب سبک
 * @property remMinutes مجموع دقایق خواب REM
 * @property awakeMinutes مجموع دقایق بیداری
 */
data class SleepSummary(
    val totalMinutes: Int,
    val deepMinutes: Int,
    val lightMinutes: Int,
    val remMinutes: Int,
    val awakeMinutes: Int
)
