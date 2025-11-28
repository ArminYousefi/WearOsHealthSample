package com.wearhealthmonitor.phone.domain.usecase

import com.wearhealthmonitor.phone.domain.model.SleepSession
import com.wearhealthmonitor.phone.domain.repository.SleepRepository
import jakarta.inject.Inject
import java.time.Instant

class GetSleepSessionsUseCase @Inject constructor(
    private val repository: SleepRepository
) {
    suspend operator fun invoke(
        start: Instant,
        end: Instant
    ): List<SleepSession> = repository.getSleepSessions(start, end)
}