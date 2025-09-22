package com.castlecoach.app.health

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.launch
import java.time.ZoneId

@Composable
fun StepsCard(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val client = remember { HealthConnectClient.getOrCreate(ctx) }

    val permissions = remember {
        setOf(HealthPermission.getReadPermission(StepsRecord::class))
    }

    var steps by remember { mutableStateOf<Long?>(null) }
    var hasPerm by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted: Set<String> ->
        hasPerm = granted.containsAll(permissions)
        if (hasPerm) {
            scope.launch { steps = readTodaySteps(ctx) ?: 0L }
        }
    }

    LaunchedEffect(Unit) {
        val granted = client.permissionController.getGrantedPermissions(permissions)
        hasPerm = granted.containsAll(permissions)
        if (hasPerm) {
            steps = readTodaySteps(ctx)
        }
    }

    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Today’s steps", style = MaterialTheme.typography.titleMedium)

            when {
                error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
                steps != null  -> Text(steps.toString(), style = MaterialTheme.typography.headlineMedium)
                hasPerm        -> Text("Loading…")
                else           -> Text("Connect Health Connect to read steps.")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!hasPerm) {
                    Button(onClick = { permissionLauncher.launch(permissions) }) {
                        Text("Grant permission")
                    }
                } else {
                    Button(onClick = {
                        scope.launch { steps = readTodaySteps(ctx) }
                    }) { Text("Refresh") }
                }
            }
        }
    }
}

private suspend fun readTodaySteps(ctx: Context): Long? {
    val client = HealthConnectClient.getOrCreate(ctx)
    val zone = ZoneId.systemDefault()
    val start = java.time.LocalDate.now(zone).atStartOfDay(zone).toInstant()
    val end = java.time.LocalDate.now(zone).plusDays(1).atStartOfDay(zone).toInstant()

    val result = client.readRecords(
        StepsRecord::class,
        timeRangeFilter = TimeRangeFilter.between(start, end)
    )
    return result.records.sumOf { it.count }
}
