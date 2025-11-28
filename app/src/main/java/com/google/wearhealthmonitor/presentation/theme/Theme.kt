package com.google.wearhealthmonitor.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.MaterialTheme

/**
 * رنگ‌های ثابت مربوط به مراحل خواب.
 *
 * طبق توصیه‌های تم‌نویسی در Compose، رنگ‌ها به صورت ثابت سطح‌بالا در یک فایل
 * متمرکز نگه‌داری می‌شوند تا همه‌ی کامپوننت‌ها از یک منبع واحد استفاده کنند.
 */
val DeepSleepColor = Color(0xFF1E88E5)
val LightSleepColor = Color(0xFF42A5F5)
val REMColor       = Color(0xFFAB47BC)
val AwakeColor     = Color(0xFFFF7043)

/**
 * رنگ‌های ثابت مربوط به سایر اجزای داشبورد سلامت
 * (ضربان قلب، دکمه‌ی ورزش، آیکون خواب، متن فاصله و ...).
 */
val HeartRateColor        = Color(0xFFFF5252)
val ExerciseOnColor       = Color(0xFF00C853)
val ExerciseOffBgColor    = Color.DarkGray
val SleepIconColor        = Color(0xFF64B5F6)
val DistanceTextColor     = Color(0xFF81C784)
val PaceTextColor         = Color(0xFFBA68C8)
val FloorsTextColor       = Color(0xFFFFEE58)
val CaloriesRingColor     = Color(0xFF00C853)
val DividerColor          = Color.White.copy(alpha = 0.12f)
val ScreenBackgroundColor = Color(0xFF0B0B0B)

/**
 * تم اصلی اپ روی Wear OS.
 *
 * @param content محتوای کامپوزابلی که در کانتکست این تم رندر می‌شود.
 */
@Composable
fun WearHealthMonitorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}
