package com.google.wearhealthmonitor.di

import com.google.wearhealthmonitor.data.repository.ExerciseRepository
import com.google.wearhealthmonitor.data.repository.SleepRepository
import com.google.wearhealthmonitor.data.repository.UserActivityRepository
import com.google.wearhealthmonitor.domain.usecase.GetHealthDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

/**
 * ماژول سطح اپ برای تعریف وابستگی‌های مربوط به داده‌های سلامت.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * سازنده‌ی اصلی GetHealthDataUseCase برای گراف DI.
     *
     * @param exerciseRepository منبع داده‌ی تمرین/ورزش
     * @param userActivityRepository منبع داده‌ی فعالیت کاربر (خواب/بیداری و ...)
     * @param sleepRepository منبع داده‌ی خواب شبانه
     *
     * - به‌صورت @Singleton ارائه می‌شود تا در کل اپ فقط یک نمونه از use case وجود داشته باشد.
     * - استفاده از constructor injection در GetHealthDataUseCase مطابق توصیه‌ی Hilt
     *   برای سادگی تست و نگه‌داری است.
     */
    @Provides
    @Singleton
    fun provideGetHealthDataUseCase(
        exerciseRepository: ExerciseRepository,
        userActivityRepository: UserActivityRepository,
        sleepRepository: SleepRepository
    ): GetHealthDataUseCase =
        GetHealthDataUseCase(
            exerciseRepository = exerciseRepository,
            userActivityRepository = userActivityRepository,
            sleepRepository = sleepRepository
        )
}
