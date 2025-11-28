package com.wearhealthmonitor.phone.data

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.records.SleepSessionRecord
import com.wearhealthmonitor.phone.domain.model.SleepSession
import com.wearhealthmonitor.phone.domain.model.SleepStage
import com.wearhealthmonitor.phone.domain.repository.SleepRepository
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class SleepRepositoryImpl @Inject constructor(
    private val client: HealthConnectClient
) : SleepRepository {

    override suspend fun getSleepSessions(start: Instant, end: Instant): List<SleepSession> {
        val req = ReadRecordsRequest(
            recordType = SleepSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val res = client.readRecords(req)
        return res.records.map { r ->
            SleepSession(
                id = r.metadata.id,
                start = r.startTime,
                end = r.endTime,
                title = r.title,
                stages = r.stages.sortedBy { it.startTime }.map { st ->
                    SleepStage(
                        start = st.startTime,
                        end = st.endTime,
                        type = when (st.stage) {
                            SleepSessionRecord.STAGE_TYPE_AWAKE -> SleepStage.StageType.AWAKE
                            SleepSessionRecord.STAGE_TYPE_SLEEPING -> SleepStage.StageType.SLEEPING
                            SleepSessionRecord.STAGE_TYPE_LIGHT -> SleepStage.StageType.LIGHT
                            SleepSessionRecord.STAGE_TYPE_DEEP -> SleepStage.StageType.DEEP
                            SleepSessionRecord.STAGE_TYPE_REM -> SleepStage.StageType.REM
                            else -> SleepStage.StageType.UNKNOWN
                        }
                    )
                }
            )
        }
    }

    override suspend fun insertFakeSleepSession() {
        val end = Instant.now().truncatedTo(ChronoUnit.HOURS)
        val start = end.minus(8, ChronoUnit.HOURS)
        val zone = ZoneOffset.systemDefault().rules.getOffset(end)

        val stages = listOf(
            SleepSessionRecord.Stage(
                startTime = start,
                endTime = start.plus(3, ChronoUnit.HOURS),
                stage = SleepSessionRecord.STAGE_TYPE_LIGHT
            ),
            SleepSessionRecord.Stage(
                startTime = start.plus(3, ChronoUnit.HOURS),
                endTime = end,
                stage = SleepSessionRecord.STAGE_TYPE_DEEP
            )
        )
        val metadata = Metadata.manualEntry()
        val record = SleepSessionRecord(
            startTime = start,
            startZoneOffset = zone,
            endTime = end,
            endZoneOffset = zone,
            title = "Debug sleep",
            notes = "Inserted by WearHealthMonitor debug",
            stages = stages,
            metadata = metadata

        )

        client.insertRecords(listOf(record))
    }
}