package com.wearhealthmonitor.phone.presentation

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wearhealthmonitor.phone.domain.HealthConnectPermissionManager
import com.wearhealthmonitor.phone.domain.repository.SleepRepository
import com.wearhealthmonitor.phone.domain.usecase.GetSleepSessionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit

@HiltViewModel
class SleepViewModel @Inject constructor(
    private val permissionManager: HealthConnectPermissionManager,
    private val getSleepSessions: GetSleepSessionsUseCase,
    private val sleepRepository: SleepRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SleepUiState())
    val uiState: StateFlow<SleepUiState> = _uiState

    fun requiredPermissions(): Set<String> = permissionManager.requiredPermissions()
    fun permissionResultContract() = permissionManager.createRequestPermissionResultContract()

    fun initialize(context: Context) {
        viewModelScope.launch {
            val status = permissionManager.getAvailability(context)
            if (status != HealthConnectClient.SDK_AVAILABLE) {
                _uiState.update {
                    it.copy(
                        isHealthConnectAvailable = false,
                        error = "Health Connect unavailable: $status"
                    )
                }
                return@launch
            }

            val granted = permissionManager.getGrantedPermissions()
            val hasPermission = granted.containsAll(permissionManager.requiredPermissions())

            _uiState.update { it.copy(hasPermission = hasPermission) }

            if (hasPermission) {
                loadLastNightSleep()
            }
        }
    }

    private fun loadLastNightSleep() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val end = Instant.now()
            val start = end.minus(3, ChronoUnit.DAYS)

            runCatching { getSleepSessions(start, end) }
                .onSuccess { sessions ->
                    _uiState.update { it.copy(isLoading = false, sessions = sessions) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Unknown error")
                    }
                }
        }
    }

    fun refreshGrants() = viewModelScope.launch {
        val granted = permissionManager.getGrantedPermissions()
        val ok = granted.containsAll(permissionManager.requiredPermissions())
        _uiState.update { it.copy(hasPermission = ok) }
        if (ok) loadLastNightSleep()
    }

    fun insertFakeSleepAndReload() {
        viewModelScope.launch {
            try {
                sleepRepository.insertFakeSleepSession()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Failed to insert fake sleep")
                }
            }
            loadLastNightSleep()
        }
    }
}