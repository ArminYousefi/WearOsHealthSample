package com.wearhealthmonitor.phone.domain.model

import java.time.Instant

data class SleepSession(
    val id: String,
    val start: Instant,
    val end: Instant,
    val title: String?,
    val stages: List<SleepStage>
)