package com.google.wearhealthmonitor.presentation.ui.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import com.google.wearhealthmonitor.presentation.theme.AwakeColor
import com.google.wearhealthmonitor.presentation.theme.CaloriesRingColor
import com.google.wearhealthmonitor.presentation.theme.DeepSleepColor
import com.google.wearhealthmonitor.presentation.theme.DistanceTextColor
import com.google.wearhealthmonitor.presentation.theme.DividerColor
import com.google.wearhealthmonitor.presentation.theme.ExerciseOffBgColor
import com.google.wearhealthmonitor.presentation.theme.ExerciseOnColor
import com.google.wearhealthmonitor.presentation.theme.FloorsTextColor
import com.google.wearhealthmonitor.presentation.theme.HeartRateColor
import com.google.wearhealthmonitor.presentation.theme.LightSleepColor
import com.google.wearhealthmonitor.presentation.theme.PaceTextColor
import com.google.wearhealthmonitor.presentation.theme.REMColor
import com.google.wearhealthmonitor.presentation.theme.ScreenBackgroundColor
import com.google.wearhealthmonitor.presentation.theme.SleepIconColor
import com.google.wearhealthmonitor.presentation.ui.componenets.FullTrackCircularProgress
import com.google.wearhealthmonitor.presentation.ui.componenets.SleepStagesRing
import java.util.Locale
import kotlin.math.roundToInt

/**
 *  صفحه داشبورد سلامت.
 *
 * وضعیت سلامت شامل ضربان قلب، وضعیت خواب، گام‌ها، فاصله، طبقات و کالری را نمایش می‌دهد.
 *
 * @param viewModel ویومدل داده‌های سلامت (HealthViewModel)
 *
 * این کامپوننت بر اساس وضعیت ورودی، اجزای هر بخش (ضربان قلب، خواب، ورزش، گام‌ها و ...) را با رنگ و آیکون مناسب نمایش می‌دهد.
 * طراحی شده برای خوانایی و واکنش‌گرایی بالا روی Wear OS.
 */
@Composable
fun HealthDashboardScreen(viewModel: HealthViewModel) {
    val state by viewModel.state.collectAsState()

    val showSleep =
        state.totalSleep != "0h" && // default when no HC / no data
                (state.deepSleepPercent + state.lightSleepPercent +
                        state.remSleepPercent + state.awakePercent) > 0

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackgroundColor),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ───── Heart rate only ─────
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "♥",
                        color = HeartRateColor,      // red heart
                        style = MaterialTheme.typography.title2
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = state.heartRate.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.title1
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "HEART RATE (BPM)",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.caption2
                )

                Spacer(Modifier.height(12.dp))
                ThinDivider()
                Text(
                    text = "Now: ${state.currentSleepState}",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.caption3
                )
                ThinDivider()
            }
        }

        // Exercise ON/OFF toggle
        item {
            ToggleChip(
                checked = state.isExerciseOn,
                onCheckedChange = { checked -> viewModel.setExerciseEnabled(checked) },
                label = {
                    Text(
                        text = if (state.isExerciseOn) "Exercise running" else "Start exercise",
                        color = Color.White
                    )
                },
                secondaryLabel = {
                    Text(
                        text = if (state.isExerciseOn) "Tap to stop" else "Tap to start synthetic run",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.caption3
                    )
                },
                toggleControl = {
                    Switch(checked = state.isExerciseOn)
                },
                colors = ToggleChipDefaults.toggleChipColors(
                    checkedStartBackgroundColor = ExerciseOnColor,
                    checkedEndBackgroundColor = ExerciseOnColor,
                    uncheckedStartBackgroundColor = ExerciseOffBgColor,
                    uncheckedEndBackgroundColor = ExerciseOffBgColor
                )
            )
        }

        // ───── Sleep (only if Health Connect / data available) ─────
        if (showSleep) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌙", color = SleepIconColor)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = state.totalSleep,
                            color = Color.White,
                            style = MaterialTheme.typography.title1
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "TOTAL SLEEP",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.caption2
                    )

                    Spacer(Modifier.height(12.dp))
                    SleepStagesRing(
                        deepPercent = state.deepSleepPercent.toFloat(),
                        lightPercent = state.lightSleepPercent.toFloat(),
                        remPercent = state.remSleepPercent.toFloat(),
                        awakePercent = state.awakePercent.toFloat()
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "SLEEP Stages",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.caption3
                    )

                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(0.95f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LegendItem("Deep", "${state.deepSleepPercent}%", DeepSleepColor)
                        LegendItem("Light", "${state.lightSleepPercent}%", LightSleepColor)
                        LegendItem("REM", "${state.remSleepPercent}%", REMColor)
                        LegendItem("Awake", "${state.awakePercent}%", AwakeColor)
                    }

                    Spacer(Modifier.height(12.dp))
                    ThinDivider()
                }
            }
        }

        // ───── Steps / distance / pace / floors ─────
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val stepsGoal = 10_000
                Text(
                    text = "%,d".format(state.steps),
                    color = Color.White,
                    style = MaterialTheme.typography.title2
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "STEPS / ${"%,d".format(stepsGoal)}",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.caption2
                )

                Spacer(Modifier.height(10.dp))

                val km = state.distanceMeters / 1000.0
                val paceMinPerKm = if (state.paceMsPerKm > 0.0) {
                    val totalMin = state.paceMsPerKm / 60000.0
                    val min = totalMin.toInt()
                    val sec = ((totalMin - min) * 60).toInt()
                    String.format(Locale.getDefault(), "%d:%02d", min, sec)
                } else "--:--"

                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f", km),
                            color = DistanceTextColor,
                            style = MaterialTheme.typography.caption1
                        )
                        Text(
                            text = "KM",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.caption3
                        )
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                        Text(
                            text = paceMinPerKm,
                            color = PaceTextColor,
                            style = MaterialTheme.typography.caption1
                        )
                        Text(
                            text = "time:km",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.caption3
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    text = state.floors.roundToInt().toString(),
                    color = FloorsTextColor,
                    style = MaterialTheme.typography.caption1
                )
                Text(
                    text = "FLOORS CLIMBED",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.caption3
                )

                Spacer(Modifier.height(12.dp))
                ThinDivider()
            }
        }

        // ───── Calories ring ─────
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val goal = 2500.0
                val progress = (state.caloriesBurnedKcal / goal).coerceIn(0.0, 1.0)
                val percent = (progress * 100).roundToInt()

                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FullTrackCircularProgress(
                        progress = progress.toFloat(),
                        color = CaloriesRingColor,
                        strokeWidth = 8.dp,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = "$percent%",
                        color = Color.White,
                        style = MaterialTheme.typography.caption1
                    )
                }

                Spacer(Modifier.height(6.dp))
                Text(
                    text = "${state.caloriesBurnedKcal.roundToInt()} / ${goal.toInt()} 🔥",
                    color = Color.White,
                    style = MaterialTheme.typography.caption2
                )
                Text(
                    text = "CALORIES",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.caption3
                )
            }
        }
    }
}

/**
 * یک کامپوزابل جداکننده نازک برای تفکیک بخش‌های صفحه داشبورد.
 */
@Composable
private fun ThinDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(1.dp)
            .background(DividerColor)
    )
}

/**
 * آیتم راهنمای رنگی برای نمایش مرحله خواب یا داده سلامت.
 *
 * @param label نام مرحله یا داده (مانند Deep یا REM)
 * @param value مقدار عددی یا درصد
 * @param color رنگ نمایش‌دهنده مرحله یا داده
 */
@Composable
fun LegendItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Text(value, color = Color.White, style = MaterialTheme.typography.caption3)
        Text(label, color = Color.White.copy(0.7f), style = MaterialTheme.typography.caption3)
    }
}