package com.google.wearhealthmonitor.data.repository

import android.content.Context
import android.util.Log
import androidx.health.services.client.HealthServices
import androidx.health.services.client.PassiveListenerCallback
import androidx.health.services.client.clearPassiveListenerCallback
import androidx.health.services.client.data.PassiveListenerConfig
import androidx.health.services.client.data.UserActivityInfo
import androidx.health.services.client.data.UserActivityState
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ریپازیتوری مسئول مانیتور کردن وضعیت فعالیت کاربر (خواب، بیداری، تمرین) با Passive Monitoring.
 *
 * - از HealthServices.passiveMonitoringClient برای دریافت UserActivityInfo استفاده می‌کند
 */
@Singleton
class UserActivityRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val passiveClient = HealthServices.getClient(context).passiveMonitoringClient

    private val _sleepState = MutableStateFlow("Unknown")
    val sleepState: StateFlow<String> = _sleepState

    private val callback = object : PassiveListenerCallback {
        override fun onUserActivityInfoReceived(info: UserActivityInfo) {
            val state = info.userActivityState
            val label = when (state) {
                UserActivityState.USER_ACTIVITY_ASLEEP -> "Asleep"
                UserActivityState.USER_ACTIVITY_PASSIVE -> "Awake"
                UserActivityState.USER_ACTIVITY_EXERCISE -> "Exercise"
                UserActivityState.USER_ACTIVITY_UNKNOWN -> "Unknown"
                else -> "Unknown"
            }
            Log.d("UserActivityRepo", "User activity = $state → $label")
            _sleepState.value = label
        }
    }

    /**
     * شروع مانیتورینگ فعالیت کاربر.
     */
    fun startMonitoring() {
        val config = PassiveListenerConfig.builder()
            .setShouldUserActivityInfoBeRequested(true)
            .build()
        passiveClient.setPassiveListenerCallback(config, callback)
        Log.d("UserActivityRepo", "Started monitoring user activity")
    }

    /**
     * توقف مانیتورینگ فعالیت کاربر و پاک‌کردن callback.
     *
     * - بهتر است در onCleared ViewModel یا زمانی که دیگر به داده‌ی فعالیت نیاز نیست
     *   صدا زده شود تا از نشت منابع و callback جلوگیری شود.
     */
    suspend fun stopMonitoring() {
        passiveClient.clearPassiveListenerCallback()
        Log.d("UserActivityRepo", "Stopped monitoring user activity")
    }
}
