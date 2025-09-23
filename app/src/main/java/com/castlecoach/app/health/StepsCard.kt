package com.castlecoach.app.health

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission   // <-- correct for 1.x
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter

import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime

private const val HEALTH_CONNECT_PACKAGE = "com.google.android.apps.healthdata"

@Composable
fun StepsCard() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val sdkStatus = remember {
        HealthConnectClient.getSdkStatus(context, HEALTH_CONNECT_PACKAGE)
    }
    val hcAvailable = HealthConnectClient.isProviderAvailable(context)


    // Only create client if available
    val client = remember(hcAvailable) {
        if (hcAvailable) HealthConnectClient.getOrCreate(context) else null
    }

    val stepsPermission = remember {
        androidx.health.connect.client.permission.HealthPermission
            .getReadPermission(StepsRecord::class)
    }

    var hasPermission by remember { mutableStateOf(false) }
    var steps by remember { mutableStateOf<Long?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
    contract = PermissionController.createRequestPermissionResultContract()
) { granted: Set<String> -> ... }
 granted: Set<String> ->
        hasPermission = stepsPermission in granted
        if (hasPermission && client != null) {
            scope.launch { loadTodaySteps(client, { steps = it }, { error = it }) }
        }
    }

    LaunchedEffect(client) {
        if (client != null) {
            hasPermission = runCatching {
                stepsPermission in client.permissionController.getGrantedPermissions()
            }.getOrDefault(false)
            if (hasPermission) {
                loadTodaySteps(client, { steps = it }, { error = it })
            }
        }
    }

    ElevatedCard {
        Column(Modifier.padding(16.dp)) {
            Text("Steps (today)")
            Spacer(Modifier.height(4.dp))

            when {
                !hcAvailable -> {
                    Text("Health Connect isn’t installed on this device.")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("market://details?id=$HEALTH_CONNECT_PACKAGE")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        runCatching { context.startActivity(intent) }
                    }) { Text("Install Health Connect") }
                }
                client == null -> Text("Initializing…")
                !hasPermission -> {
                    Text("Connect Health Connect to show steps.")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { permissionLauncher.launch(setOf(stepsPermission)) }) {
                        Text("Connect")
                    }
                }
                error != null -> Text("Error: $error")
                steps == null -> Text("Loading…")
                else -> Text("$steps steps")
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
