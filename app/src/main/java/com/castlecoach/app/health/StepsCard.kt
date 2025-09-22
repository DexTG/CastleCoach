package com.castlecoach.app.health

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
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
    val client = remember { HealthConnectClient.getOrCreate(context) }

    // Health Connect permission string for steps
    val readStepsPermission = remember { HealthPermission.getReadPermission(StepsRecord::class) }
    var hasPermission by remember { mutableStateOf(false) }
    var steps by remember { mutableStateOf<Long?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    // Launcher to show the Health Connect permission UI
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // After the dialog closes, check again
        hasPermission = runCatching {
            readStepsPermission in client.permissionController.getGrantedPermissions()
        }.getOrDefault(false)

        if (hasPermission) {
            // re-load steps after permission granted
            loadTodaySteps(client, onResult = { steps = it }, onError = { error = it })
        }
    }

    // Initial permission check + load
    LaunchedEffect(Unit) {
        hasPermission = runCatching {
            readStepsPermission in client.permissionController.getGrantedPermissions()
        }.getOrDefault(false)

        if (hasPermission) {
            loadTodaySteps(client, onResult = { steps = it }, onError = { error = it })
        }
    }

    ElevatedCard {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Steps (today)")

            when {
                !hasPermission -> {
                    Text("Connect Health Connect to show steps.")
                    Button(onClick = {
                        val intent = client.permissionController
                            .createRequestPermissionIntent(setOf(readStepsPermission))
                        permissionLauncher.launch(intent)
                    }) {
                        Text("Connect")
                    }
                }
                error != null -> Text("Error: $error")
                steps == null -> Text("Loading…")
                else -> Text("${steps} steps")
            }
        }
    }
}

private suspend fun loadTodaySteps(
    client: HealthConnectClient,
    onResult: (Long) -> Unit,
    onError: (String) -> Unit
) {
    try {
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
        onResult(response.records.sumOf { it.count })
    } catch (t: Throwable) {
        onError(t.message ?: "Unknown error")
    }
}
