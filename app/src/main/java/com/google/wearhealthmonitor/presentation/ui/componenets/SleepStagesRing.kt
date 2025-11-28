package com.google.wearhealthmonitor.presentation.ui.componenets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.wearhealthmonitor.presentation.theme.AwakeColor
import com.google.wearhealthmonitor.presentation.theme.DeepSleepColor
import com.google.wearhealthmonitor.presentation.theme.LightSleepColor
import com.google.wearhealthmonitor.presentation.theme.REMColor

/**
 * حلقه‌ی مراحل خواب به صورت نمودار دایره‌ای (ring chart).
 *
 * @param deepPercent درصد خواب عمیق
 * @param lightPercent درصد خواب سبک
 * @param remPercent درصد خواب REM
 * @param awakePercent درصد بیداری در طول شب
 * @param modifier برای تنظیم سایز/پدینگ از بیرون
 */
@Composable
fun SleepStagesRing(
    deepPercent: Float,
    lightPercent: Float,
    remPercent: Float,
    awakePercent: Float,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        DeepSleepColor to deepPercent,
        LightSleepColor to lightPercent,
        REMColor to remPercent,
        AwakeColor to awakePercent
    )

    Canvas(modifier = modifier.size(140.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val strokeWidth = 28f
        val radius = (canvasWidth.coerceAtMost(canvasHeight) - strokeWidth) / 2f
        val center = Offset(canvasWidth / 2f, canvasHeight / 2f)

        var startAngle = -90f // از بالا شروع بشه

        colors.forEach { (color, percent) ->
            if (percent > 0f) {
                val sweepAngle = percent / 100f * 360f
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth),
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
                startAngle += sweepAngle
            }
        }
    }

    Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
        Text(
            text = "Sleep\nStages",
            color = Color.White,
            style = MaterialTheme.typography.caption1,
            textAlign = TextAlign.Center
        )
    }
}