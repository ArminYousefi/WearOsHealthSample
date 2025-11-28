package com.google.wearhealthmonitor.presentation.ui.componenets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator

/**
 * یک Progress دایره‌ای با track کامل ۳۶۰ درجه.
 *
 * @param progress مقدار پیشرفت نرمال‌شده بین ۰ تا ۱
 * @param modifier برای ترکیب با Layout/Size از بیرون
 * @param color رنگ خط اصلی پیشرفت
 * @param trackColor رنگ پس‌زمینه‌ی مسیر دایره
 * @param strokeWidth ضخامت خط دایره
 */
@Composable
fun FullTrackCircularProgress(
    progress: Float, // 0f..1f
    modifier: Modifier = Modifier,
    color: Color,
    trackColor: Color = Color.DarkGray.copy(alpha = 0.3f),
    strokeWidth: Dp = 10.dp
) {
    CircularProgressIndicator(
        progress = progress.coerceIn(0f, 1f),
        startAngle = 0f,
        endAngle = 360f,
        indicatorColor = color,
        trackColor = trackColor,
        strokeWidth = strokeWidth,
        modifier = modifier
    )
}