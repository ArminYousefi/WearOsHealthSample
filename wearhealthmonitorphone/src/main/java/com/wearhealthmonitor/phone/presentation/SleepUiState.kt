package com.wearhealthmonitor.phone.presentation

import com.wearhealthmonitor.phone.domain.model.SleepSession

data class SleepUiState(
    val isHealthConnectAvailable: Boolean = true,
    val hasPermission: Boolean = false,
    val isLoading: Boolean = false,
    val sessions: List<SleepSession> = emptyList(),
    val error: String? = null
)