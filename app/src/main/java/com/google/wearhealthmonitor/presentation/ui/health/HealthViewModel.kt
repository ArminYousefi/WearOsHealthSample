package com.google.wearhealthmonitor.presentation.ui.health

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.wearhealthmonitor.domain.usecase.GetHealthDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * ViewModel اصلی داشبورد سلامت.
 *
 * بهترین‌عمل‌کردها:
 *   (رعایت separation of concerns).
 * - به کمک Hilt و @HiltViewModel وابستگی‌ها را به‌صورت خودکار دریافت می‌کند (constructor injection).
 * - وضعیت UI را با StateFlow از نوع HealthUiState نگه می‌دارد تا Compose به صورت reactive رندر شود.
 * مسئولیت‌ها:
 * - شروع مانیتورینگ فعالیت کاربر و وضعیت خواب (startObserving).
 * - مدیریت شروع/توقف سناریوی تمرین و جمع کردن متریک‌های ورزش (setExerciseEnabled).
 * - خواندن خلاصه خواب شب گذشته از Health Connect و نگاشت آن به درصدها (refreshSleepOnce).
 * - پاک‌سازی منابع و توقف مانیتورینگ هنگام بسته شدن ViewModel (onCleared).
 */
@HiltViewModel
class HealthViewModel @Inject constructor(
    private val getHealthDataUseCase: GetHealthDataUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HealthUiState())
    val state: StateFlow<HealthUiState> = _state

    private var started = false
    private var exerciseJob: Job? = null

    fun startObserving() {
        if (started) {
            Log.d("HealthVM", "startObserving() called again, ignoring")
            return
        }

        started = true
        Log.d("HealthVM", "startObserving() → background monitors only")

        // 1) Start user activity (awake/asleep)
        viewModelScope.launch {
            getHealthDataUseCase.startUserActivityMonitoring()
            getHealthDataUseCase.sleepAwakeState.collect { label ->
                _state.update { it.copy(currentSleepState = label) }
            }
        }

        // 2) Nightly sleep from Health Connect (if available)
        viewModelScope.launch {
            refreshSleepOnce()
        }
    }

    /** Called by UI switch / toggle chip. */
    fun setExerciseEnabled(enabled: Boolean) {
        if (enabled) {
            if (exerciseJob != null) return // already running
            Log.d("HealthVM", "Starting exercise collection")
            exerciseJob = viewModelScope.launch {
                getHealthDataUseCase.exerciseMetrics.collect { m ->
                    Log.d("HealthVM", "Delta metrics = $m")
                    _state.update { s ->
                        s.copy(
                            heartRate = m.heartRateBpm?.toInt() ?: s.heartRate,
                            steps = s.steps + m.steps.toInt(),
                            stepsPerMin = m.stepsPerMin?.toInt() ?: s.stepsPerMin,
                            caloriesBurnedKcal = s.caloriesBurnedKcal + (m.calories ?: 0.0),
                            distanceMeters = s.distanceMeters + (m.distanceMeters ?: 0.0),
                            speedMps = m.speedMps ?: s.speedMps,
                            paceMsPerKm = m.paceMsPerKm ?: s.paceMsPerKm,
                            elevationGainMeters = s.elevationGainMeters +
                                    (m.elevationGainMeters ?: 0.0),
                            floors = s.floors + (m.floors ?: 0.0),
                            activeSeconds = m.activeSeconds ?: s.activeSeconds
                        )
                    }
                }
            }
        } else {
            Log.d("HealthVM", "Stopping exercise collection")
            exerciseJob?.cancel()
            exerciseJob = null
            viewModelScope.launch {
                getHealthDataUseCase.stopExercise()
            }
        }

        _state.update { it.copy(isExerciseOn = enabled) }
    }

    private suspend fun refreshSleepOnce() {
        Log.d("HealthVM", "refreshSleepOnce() called")
        val summary = getHealthDataUseCase.getLastNightSleep() ?: return
        val total = summary.totalMinutes.coerceAtLeast(1)

        val deepPct = summary.deepMinutes * 100 / total
        val lightPct = summary.lightMinutes * 100 / total
        val remPct = summary.remMinutes * 100 / total
        val awakePct = summary.awakeMinutes * 100 / total

        _state.update {
            it.copy(
                totalSleep = String.format(Locale.getDefault(), "%.1fh", total / 60f),
                deepSleepPercent = deepPct,
                lightSleepPercent = lightPct,
                remSleepPercent = remPct,
                awakePercent = awakePct
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            getHealthDataUseCase.stopUserActivityMonitoring()
        }
    }
}