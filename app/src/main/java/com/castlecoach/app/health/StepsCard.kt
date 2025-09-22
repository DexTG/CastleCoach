package com.castlecoach.app.health

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import androidx.compose.foundation.layout.padding

@Composable
fun StepsCard() {
    val context = LocalContext.current
    val client = remember { HealthConnectClient.getOrCreate(context) }
    val scope = rememberCoroutineScope()

    // Permission for reading steps
    val stepsPermission = remember { HealthPermission.getReadPermission(StepsRecord::class) }

    var hasPermission by remember { mutableStateOf(false) }
    var steps by remember { mutableStateOf<Long?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    // Launcher for the Health Connect permission UI
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // After the permissions UI closes, re-check permissions and, if granted, load steps
        scope.launch {
            hasPermission = runCatching {
                stepsPermission in client.permissionController.getGrantedPermissions()
            }.getOrDefault(false)

            if (hasPermission) {
                loadTodaySteps(client, onResult = { steps = it }, onError = { error = it })
            }
        }
    }

    // Initial permission check + load
    LaunchedEffect(Unit) {
        hasPermission = runCatching {
            stepsPermission in client.permissionController.getGrantedPermissions()
        }.getOrDefault(false)

        if (hasPermission) {
            loadTodaySteps(client, onResult = { steps = it }, onError = { error = it })
        }
    }

    ElevatedCard {
        Column(Modifier.padding(16.dp)) {
            Text("Steps (today)")
            Spacer(Modifier.height(4.dp))

            when {
                !hasPermission -> {
                    Text("Connect Health Connect to show steps.")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        scope.launch {
                            val intent = client.permissionController
                                .createRequestPermissionIntent(setOf(stepsPermission))
                            permissionLauncher.launch(intent)
                        }
                    }) { Text("Connect") }
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
