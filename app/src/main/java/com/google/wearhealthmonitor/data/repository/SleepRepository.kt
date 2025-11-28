package com.google.wearhealthmonitor.data.repository

import android.content.Context
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.google.wearhealthmonitor.data.model.SleepSummary
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * ریپازیتوری مسئول خواندن داده‌های خواب شب گذشته از Health Connect.
 *
 * - قبل از هرگونه استفاده، با HealthConnectClient.getSdkStatus بررسی می‌کند
 *   که Health Connect روی دستگاه در دسترس است، و در غیر این صورت به شکل امن null
 *   برمی‌گرداند تا اپ روی امولاتور/دستگاه‌های ناسازگار کرش نکند.
 * - تنها وظیفه‌ی این لایه، صحبت با API سطح پایین Health Connect و تبدیل آن
 * - محاسبه‌ی زمان هر stage خواب با Duration و جمع‌زدن روی چهار دسته‌ی اصلی
 *   (deep, light, REM, awake) انجام می‌شود و در نهایت مجموع را حداقل ۱ دقیقه
 *   در نظر می‌گیرد تا از تقسیم بر صفر و حالت‌های مرزی جلوگیری شود.
 *
 */
@Singleton
class SleepRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * خواندن خلاصه‌ی خواب شب گذشته به صورت SleepSummary.
     *
     * - اگر Health Connect در دسترس نباشد یا رکوردی پیدا نشود، مقدار null برمی‌گرداند.
     * - آخرین SleepSessionRecord در بازه‌ی ۲۴ ساعت گذشته انتخاب می‌شود و
     *   بر اساس stageهای آن، مجموع دقیقه‌های هر نوع خواب محاسبه می‌گردد.
     */
    suspend fun getLastNightSleep(): SleepSummary? {
        val status = HealthConnectClient.getSdkStatus(context)
        if (status != HealthConnectClient.SDK_AVAILABLE) {
            Log.d("SleepRepo", "Health Connect SDK not available, status=$status")
            return null
        }

        val client = HealthConnectClient.getOrCreate(context)

        val now = Instant.now()
        val yesterday = now.minus(1, ChronoUnit.DAYS)

        val sessionResponse = client.readRecords(
            ReadRecordsRequest(
                recordType = SleepSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(yesterday, now)
            )
        )
        val sessions: List<SleepSessionRecord> = sessionResponse.records
        val latest: SleepSessionRecord =
            sessions.maxByOrNull { it.endTime } ?: return null

        var deep = 0L
        var light = 0L
        var rem = 0L
        var awake = 0L

        for (stage in latest.stages) {
            val minutes = Duration.between(stage.startTime, stage.endTime).toMinutes()
            when (stage.stage) {
                SleepSessionRecord.STAGE_TYPE_DEEP -> deep += minutes
                SleepSessionRecord.STAGE_TYPE_LIGHT -> light += minutes
                SleepSessionRecord.STAGE_TYPE_REM -> rem += minutes
                SleepSessionRecord.STAGE_TYPE_AWAKE,
                SleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED,
                SleepSessionRecord.STAGE_TYPE_OUT_OF_BED -> awake += minutes
            }
        }

        val total = (deep + light + rem + awake).toInt().coerceAtLeast(1)
        return SleepSummary(
            totalMinutes = total,
            deepMinutes = deep.toInt(),
            lightMinutes = light.toInt(),
            remMinutes = rem.toInt(),
            awakeMinutes = awake.toInt()
        )
    }
}