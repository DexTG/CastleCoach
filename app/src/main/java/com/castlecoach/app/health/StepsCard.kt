package com.castlecoach.app.health
import android.content.pm.PackageManager

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
import androidx.health.connect.client.permission.HealthPermission // <-- use this one
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController // <- correct PermissionController
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime

// Top-level: provider package for 1.0.0-alpha*
private const val HEALTH_CONNECT_PACKAGE = "com.google.android.apps.healthdata"

@Composable
fun StepsCard() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val hcAvailable = remember {
    HealthConnectClient.isProviderAvailable(context)
}

    // Only create the client if available
    val client = remember(hcAvailable) {
        if (hcAvailable) runCatching { HealthConnectClient.getOrCreate(context) }.getOrNull()
        else null
    }

    val stepsPermission = remember { 
        androidx.health.connect.client.permission.HealthPermission.getReadPermission(StepsRecord::class)
    }

    var hasPermission by remember { mutableStateOf(false) }
    var steps by remember { mutableStateOf<Long?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult<Set<String>, Set<String>>(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        hasPermission = stepsPermission in granted
        if (hasPermission && client != null) {
            scope.launch { loadTodaySteps(client, onResult = { steps = it }, onError = { error = it }) }
        }
    }

    LaunchedEffect(hcAvailable, client) {
        if (hcAvailable && client != null) {
            hasPermission = runCatching {
                stepsPermission in client.permissionController.getGrantedPermissions()
            }.getOrDefault(false)
            if (hasPermission) {
                loadTodaySteps(client, onResult = { steps = it }, onError = { error = it })
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
        Button(onClick = { openHealthConnectInstall(context) }) {
            Text("Install Health Connect")
        }
    }
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
