package com.google.wearhealthmonitor.domain.usecase

import com.google.wearhealthmonitor.data.model.ExerciseMetrics
import com.google.wearhealthmonitor.data.repository.ExerciseRepository
import com.google.wearhealthmonitor.data.repository.SleepRepository
import com.google.wearhealthmonitor.data.repository.UserActivityRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * use case اصلی برای دسترسی به داده‌های سلامت (ورزش، فعالیت کاربر و خواب).
 *
 * - این کلاس به عنوان لایه‌ی دامین بین ViewModel و Repositoryها عمل می‌کند
 *   و منطق دسترسی به داده را در یک نقطه متمرکز نگه می‌دارد (separation of concerns).
 * - با @Singleton و DI (Hilt) به صورت یک نمونه‌ی مشترک در کل اپ تزریق می‌شود.
 * - ViewModel باید فقط با این use case صحبت کند و از جزئیات پیاده‌سازی Repositoryها بی‌خبر باشد.
 * @param exerciseRepository ریپو برای دریافت متریک‌های ورزشی (ضربان قلب، قدم‌ها، سرعت، کالری و ...)
 * @param userActivityRepository ریپو برای دریافت وضعیت فعلی خواب/بیداری کاربر
 * @param sleepRepository ریپو برای دریافت دریافت خلاصه‌ی خواب شب گذشته
 */
@Singleton
class GetHealthDataUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val userActivityRepository: UserActivityRepository,
    private val sleepRepository: SleepRepository
) {
    /**
     * جریان زنده‌ی متریک‌های ورزشی (ضربان قلب، قدم‌ها، سرعت، کالری و ...).
     */
    val exerciseMetrics: Flow<ExerciseMetrics> =
        exerciseRepository.exerciseMetricsFlow()

    /**
     * وضعیت فعلی خواب/بیداری کاربر به صورت StateFlow (برای UI واکنش‌گرا).
     */
    val sleepAwakeState: StateFlow<String> =
        userActivityRepository.sleepState

    /**
     * شروع مانیتورینگ فعالیت کاربر (مثلاً خواب/بیداری).
     */
    fun startUserActivityMonitoring() =
        userActivityRepository.startMonitoring()

    /**
     * دریافت خلاصه‌ی خواب شب گذشته از لایه‌ی sleepRepository.
     * می‌تواند null برگرداند اگر داده‌ای در دسترس نباشد.
     */
    suspend fun getLastNightSleep() =
        sleepRepository.getLastNightSleep()

    /**
     * توقف مانیتورینگ فعالیت کاربر
     */
    suspend fun stopUserActivityMonitoring() =
        userActivityRepository.stopMonitoring()

    /**
     * توقف تمرین/ورزش جاری
     */
    suspend fun stopExercise() =
        exerciseRepository.stopCurrentExercise()
}
