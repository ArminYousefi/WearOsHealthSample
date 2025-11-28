package com.google.wearhealthmonitor.data.repository

import android.content.Context
import android.util.Log
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServices
import androidx.health.services.client.clearUpdateCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.WarmUpConfig
import androidx.health.services.client.endExercise
import androidx.health.services.client.getCurrentExerciseInfo
import androidx.health.services.client.prepareExercise
import androidx.health.services.client.startExercise
import com.google.wearhealthmonitor.data.model.ExerciseMetrics
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import javax.inject.Singleton

/**
 * ریپازیتوری مسئول مدیریت تمرین ورزشی و استریم متریک‌ها از Health Services.
 */
@Singleton
class ExerciseRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val exerciseClient = HealthServices.getClient(context).exerciseClient
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * شروع یک تمرین RUNNING و استریم تمام متریک‌ها به صورت Flow<ExerciseMetrics>.
     *
     * نکات:
     * - از callbackFlow برای پل‌زدن بین ExerciseUpdateCallback (callback-based) و Flow
     *   استفاده شده است تا ViewModel بتواند به شکل واکنش‌گرا collect کند.
     * - فقط deltaها (تغییرات) ارسال می‌شود و ViewModel می‌تواند در صورت نیاز مجموع‌ها
     *   را روی state خودش accumulate کند.
     * - ثبت و پاک کردن callback داخل بلاک callbackFlow و awaitClose مدیریت می‌شود
     *   تا نشت callback اتفاق نیفتد.
     */
    fun exerciseMetricsFlow(): Flow<ExerciseMetrics> = callbackFlow {
        val callback = object : ExerciseUpdateCallback {

            override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
                val metrics = update.latestMetrics

                // متریک‌ها را به صورت امن و آخرین مقدار قابل‌دسترس می‌خوانیم
                val hr = metrics.getData(DataType.HEART_RATE_BPM)
                    .lastOrNull()?.value

                val stepsDelta = metrics.getData(DataType.STEPS)
                    .lastOrNull()?.value ?: 0L

                val spm = metrics.getData(DataType.STEPS_PER_MINUTE)
                    .lastOrNull()?.value

                val calsDelta = metrics.getData(DataType.CALORIES)
                    .lastOrNull()?.value

                val distDelta = metrics.getData(DataType.DISTANCE)
                    .lastOrNull()?.value

                val speed = metrics.getData(DataType.SPEED)
                    .lastOrNull()?.value

                val pace = metrics.getData(DataType.PACE)
                    .lastOrNull()?.value

                val elevGainDelta = metrics.getData(DataType.ELEVATION_GAIN)
                    .lastOrNull()?.value

                val floorsDelta = metrics.getData(DataType.FLOORS)
                    .lastOrNull()?.value

                // محاسبه‌ی زمان فعال بودن تمرین بر اساس checkpoint فعلی
                val checkpoint = update.activeDurationCheckpoint
                val activeSec = checkpoint?.let {
                    val now = Instant.now()
                    val duration = it.activeDuration + Duration.between(it.time, now)
                    duration.seconds
                }

                trySend(
                    ExerciseMetrics(
                        heartRateBpm = hr,
                        // deltaها ارسال می‌شوند؛ ViewModel در صورت نیاز آن‌ها را جمع می‌زند.
                        steps = stepsDelta,
                        stepsPerMin = spm,
                        calories = calsDelta,
                        distanceMeters = distDelta,
                        speedMps = speed,
                        paceMsPerKm = pace,
                        elevationGainMeters = elevGainDelta,
                        floors = floorsDelta,
                        activeSeconds = activeSec
                    )
                )
            }

            override fun onAvailabilityChanged(
                dataType: DataType<*, *>,
                availability: Availability
            ) {
                Log.d("ExerciseRepo", "Availability ${dataType.name}: $availability")
            }

            override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) {
                Log.d("ExerciseRepo", "Lap summary: $lapSummary")
            }

            override fun onRegistered() {
                Log.d("ExerciseRepo", "ExerciseUpdateCallback registered")
            }

            override fun onRegistrationFailed(throwable: Throwable) {
                Log.e("ExerciseRepo", "ExerciseUpdateCallback registration failed", throwable)
                close(throwable)
            }
        }

        // ثبت callback برای دریافت آپدیت‌های تمرین
        exerciseClient.setUpdateCallback(callback)

        // Warm-up و شروع تمرین روی thread بک‌گراند
        ioScope.launch {
            try {
                // 0) اگر قبلاً تمرینی در حال اجراست، همان را دوباره استفاده کن
                val info = exerciseClient.getCurrentExerciseInfo()
                if (info.exerciseType != ExerciseType.UNKNOWN) {
                    Log.d(
                        "ExerciseRepo",
                        "Exercise already in progress (${info.exerciseType}), reusing existing session"
                    )
                    return@launch   // فقط callback وصل شده، نیازی به prepare/start دوباره نیست
                }

                val dataTypes = setOf(
                    DataType.HEART_RATE_BPM,
                    DataType.STEPS,
                    DataType.STEPS_PER_MINUTE,
                    DataType.DISTANCE,
                    DataType.SPEED,
                    DataType.PACE,
                    DataType.CALORIES,
                    DataType.ELEVATION_GAIN,
                    DataType.FLOORS
                )

                val warmUpConfig = WarmUpConfig(
                    exerciseType = ExerciseType.RUNNING,
                    dataTypes = dataTypes
                )
                exerciseClient.prepareExercise(warmUpConfig)

                val config = ExerciseConfig(
                    exerciseType = ExerciseType.RUNNING,
                    dataTypes = dataTypes,
                    isAutoPauseAndResumeEnabled = false,
                    isGpsEnabled = false
                )
                exerciseClient.startExercise(config)
                Log.d("ExerciseRepo", "Exercise started with dataTypes=$dataTypes")
            } catch (e: Exception) {
                Log.e("ExerciseRepo", "Failed to start exercise", e)
                close(e)
            }
        }

        // پاک‌سازی callback و منابع وقتی collector Flow را cancel می‌کند
        awaitClose {
            ioScope.launch {
                try {
                    exerciseClient.clearUpdateCallback(callback)
                } catch (_: Exception) {
                }
                // در صورت نیاز می‌توان endExercise() غیرهمزمان را هم اینجا صدا زد.
            }
        }
    }

    /**
     * توقف تمرین فعلی وقتی کاربر در UI سوئیچ exercise را خاموش می‌کند.
     *
     * - خطاها هندل می‌شوند تا در صورت نبود تمرین فعال، اپ کرش نکند.
     * - لاگ‌گرفتن کمک می‌کند مشکلات Health Services یا وضعیت تمرین را دیباگ کنید.
     */
    suspend fun stopCurrentExercise() {
        try {
            exerciseClient.endExercise()
            Log.d("ExerciseRepo", "endExercise() called from UI")
        } catch (e: Exception) {
            Log.e("ExerciseRepo", "endExercise() failed or no active exercise", e)
        }
    }
}
