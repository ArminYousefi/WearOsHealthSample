package com.google.wearhealthmonitor.data.model

/**
 * مدل داده‌ای (data model) برای نگه‌داری متریک‌های لحظه‌ای تمرین (exercise).
 *
 * @property heartRateBpm آخرین مقدار ضربان قلب بر حسب bpm (اگر در آن لحظه موجود باشد)
 * @property steps تعداد گام‌های افزایشی (delta) در بازه‌ی آخرین آپدیت
 * @property stepsPerMin تعداد گام بر دقیقه (در صورت محاسبه شدن)
 * @property calories کالری سوخته شده در بازه‌ی آخرین آپدیت (به کیلوکالری)
 * @property distanceMeters مسافت پیموده ه‌شده در بازه‌ی آخرین آپدیت (متر)
 * @property speedMps سرعت لحظه‌ای (متر بر ثانیه)
 * @property paceMsPerKm pace تقریبی (میلی‌ثانیه بر کیلومتر)
 * @property elevationGainMeters افزایش ارتفاع در بازه‌ی آخرین آپدیت (متر)
 * @property floors تعداد طبقات طی‌شده در بازه‌ی آخرین آپدیت
 * @property activeSeconds مدت زمان فعال بودن تمرین تا این لحظه (ثانیه)، در صورت قابل محاسبه بودن
 */
data class ExerciseMetrics(
    val heartRateBpm: Double? = null,
    val steps: Long = 0L,
    val stepsPerMin: Long? = null,
    val calories: Double? = null,
    val distanceMeters: Double? = null,
    val speedMps: Double? = null,
    val paceMsPerKm: Double? = null,
    val elevationGainMeters: Double? = null,
    val floors: Double? = null,
    val activeSeconds: Long? = null
)
