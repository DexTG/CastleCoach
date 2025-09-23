import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun StepsCard() {
    val context = LocalContext.current
    val client = remember { HealthConnectClient.getOrCreate(context) }
    val scope = rememberCoroutineScope()

    val hcAvailable = remember { HealthConnectClient.isProviderAvailable(context) }

    val stepsPermission = remember { HealthPermission.getReadPermission(StepsRecord::class) }
    var hasPermission by remember { mutableStateOf(false) }
    var steps by remember { mutableStateOf<Long?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted: Set<String> ->
        hasPermission = stepsPermission in granted
        if (hasPermission) {
            scope.launch { loadTodaySteps(client, { steps = it }, { error = it }) }
        }
    }

    LaunchedEffect(Unit) {
        if (hcAvailable) {
            hasPermission = runCatching {
                stepsPermission in client.permissionController.getGrantedPermissions()
            }.getOrDefault(false)
            if (hasPermission) {
                loadTodaySteps(client, { steps = it }, { error = it })
            }
        }
    }

    // ... your card UI ...
    when {
        !hcAvailable -> {
            Text("Health Connect isn’t installed on this device.")
            // Optionally add a button to open HC in Play Store / Settings
        }
        !hasPermission -> {
            Text("Connect Health Connect to show steps.")
            Button(onClick = { permissionLauncher.launch(setOf(stepsPermission)) }) {
                Text("Connect")
            }
        }
        error != null -> Text("Error: $error")
        steps == null -> Text("Loading…")
        else -> Text("$steps steps")
    }
}

private suspend fun loadTodaySteps(
    client: HealthConnectClient,
    onResult: (Long) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val zone = ZoneId.systemDefault()
        val end = ZonedDateTime.now(zone)
        val start = end.toLocalDate().atStartOfDay(zone)
        val response = client.readRecords(
            ReadRecordsRequest(
                StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start.toInstant(), end.toInstant())
            )
        )
        onResult(response.records.sumOf { it.count })
    } catch (t: Throwable) {
        onError(t.message ?: "Unknown error")
    }
}
