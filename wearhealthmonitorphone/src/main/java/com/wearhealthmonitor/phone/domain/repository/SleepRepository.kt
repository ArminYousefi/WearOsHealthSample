package com.wearhealthmonitor.phone.domain.repository

import com.wearhealthmonitor.phone.domain.model.SleepSession
import java.time.Instant

interface SleepRepository {
    suspend fun getSleepSessions(
        start: Instant,
        end: Instant
    ): List<SleepSession>

    suspend fun insertFakeSleepSession()
}