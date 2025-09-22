package com.castlecoach.app.health

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.permissions.HealthPermission
import java.time.Instant
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

            // Build permission set
            val readSteps = HealthPermission.getReadPermission(StepsRecord::class)
            val granted: Set<HealthPermission> = client.permissionController.getGrantedPermissions()

            if (readSteps !in granted) {
                // In Compose, you normally launch the permission UI via Activity.
                // If this Composable sits in an Activity, request like this:
                PermissionController(context).createRequestPermissionResultContract()
                // ^ In production you’d wire up a launcher. For CI builds, skip requesting.
                // For now, just bail if not granted:
                error = "Health Connect permission for steps not granted"
                return@LaunchedEffect
            }

            val zone = ZoneId.systemDefault()
            val end: ZonedDateTime = ZonedDateTime.now(zone)
            val start: ZonedDateTime = end.toLocalDate().atStartOfDay(zone)

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
