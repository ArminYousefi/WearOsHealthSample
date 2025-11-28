package com.wearhealthmonitor.phone.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wearhealthmonitor.phone.domain.model.SleepSession
import com.wearhealthmonitor.phone.domain.model.SleepStage
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.util.Locale

@Composable
fun SleepScreen(
    state: SleepUiState,
    onRequestPermission: () -> Unit,
    onInsertFakeSleep: () -> Unit
) {
    Scaffold {
        when {
            !state.isHealthConnectAvailable -> {
                CenteredMessage("Health Connect is not available on this device.")
            }
            !state.hasPermission -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = it)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Sleep permission is required to read sleep data.")
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onRequestPermission) {
                        Text("Grant Health Connect permission")
                    }
                }
            }
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                CenteredMessage("Error: ${state.error}")
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (state.sessions.isEmpty()) {
                        CenteredMessage("No sleep sessions found in the last 3 days.")
                    } else {
                        SleepSessionList(
                            sessions = state.sessions,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues = it)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    FloatingActionButton(
                        onClick = onInsertFakeSleep,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        Text("Fake")

                    }
                }
            }
        }

    }
}

@Composable
private fun CenteredMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message)
    }
}

@Composable
private fun SleepSessionList(
    sessions: List<SleepSession>,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    LazyColumn(
        modifier = modifier
    ) {
        items(sessions) { session ->
            val zone = ZoneId.systemDefault()
            val startLocal = session.start.atZone(zone)
            val endLocal = session.end.atZone(zone)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = dateFormatter.format(startLocal),
                        style = MaterialTheme.typography.titleMedium
                    )

                    val durationHours = java.time.Duration
                        .between(session.start, session.end)
                        .toMinutes() / 60.0

                    val durationText = String.format(
                        Locale.getDefault(),   // or Locale.ENGLISH / Locale.US if you prefer
                        "%.1f h",
                        durationHours
                    )
                    Text(
                        text = "${timeFormatter.format(startLocal)} – " +
                                "${timeFormatter.format(endLocal)}  ·  " +
                                durationText,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(8.dp))

                    session.stages.forEach { stage ->
                        StageRow(stage, timeFormatter, zone)
                    }
                }
            }
        }
    }
}

@Composable
private fun StageRow(
    stage: SleepStage,
    timeFormatter: DateTimeFormatter,
    zone: ZoneId
) {
    val start = timeFormatter.format(stage.start.atZone(zone))
    val end = timeFormatter.format(stage.end.atZone(zone))
    val label = when (stage.type) {
        SleepStage.StageType.LIGHT -> "Light"
        SleepStage.StageType.DEEP -> "Deep"
        SleepStage.StageType.REM -> "REM"
        SleepStage.StageType.AWAKE -> "Awake"
        SleepStage.StageType.SLEEPING -> "Asleep"
        SleepStage.StageType.UNKNOWN -> "Unknown"
    }

    Text(
        text = "• $label: $start – $end",
        style = MaterialTheme.typography.bodySmall
    )
}