package com.castlecoach.app.health

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.permissions.HealthPermission
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun StepsCard() {
    val context = LocalContext.current
    var steps by remember { mutableStateOf<Long?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val client = HealthConnectClient.getOrCreate(context)

            // Permissions: now compare Strings, not HealthPermission objects.
            val readStepsPermission = HealthPermission.getReadPermission(StepsRecord::class)
            val granted: Set<String> = client.permissionController.getGrantedPermissions()

            if (readStepsPermission !in granted) {
                // In CI or first run without UI permission flow: show a message and stop.
                error = "Health Connect steps permission not granted"
                return@LaunchedEffect
            }

            val zone = ZoneId.systemDefault()
            val end: ZonedDateTime = ZonedDateTime.now(zone)
            val start: ZonedDateTime = end.toLocalDate().atStartOfDay(zone)

            // Read today’s steps
            val response = client.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(
                        start.toInstant(),
                        end.toInstant()
                    )
                )
            )

            val total = response.records.sumOf { it.count }
            steps = total
        } catch (t: Throwable) {
            error = t.message ?: "Unknown error"
        }
    }

    ElevatedCard {
        Column(Modifier.padding(16.dp)) {
            Text("Steps (today)")
            Spacer(Modifier.height(4.dp))
            when {
                error != null -> Text("Error: $error")
                steps == null -> Text("Loading…")
                else -> Text("${steps} steps")
            }
        }
    }
}
