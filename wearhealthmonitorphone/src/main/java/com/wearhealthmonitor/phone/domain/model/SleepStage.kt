package com.wearhealthmonitor.phone.domain.model

import java.time.Instant

data class SleepStage(
    val start: Instant,
    val end: Instant,
    val type: StageType
) {
    enum class StageType {
        UNKNOWN,
        AWAKE,
        SLEEPING,
        LIGHT,
        DEEP,
        REM
    }
}