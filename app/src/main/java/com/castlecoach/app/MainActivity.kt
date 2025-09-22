package com.castlecoach.app

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        com.castlecoach.app.plan.DailyScheduler.scheduleToday(this)

        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
private fun HydrationChip(targetMl: Int, drankMl: Int, onAdd: (Int) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(onClick = { onAdd(250) }, label = { Text("+250 ml") })
        AssistChip(onClick = { onAdd(500) }, label = { Text("+500 ml") })
        Text("Hydration: $drankMl / $targetMl ml", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun StopwatchCard() {
    var running by remember { mutableStateOf(false) }
    var base by remember { mutableStateOf(SystemClock.elapsedRealtime()) }
    var elapsedMs by remember { mutableStateOf(0L) }

    LaunchedEffect(running) {
        while (running) {
            elapsedMs = SystemClock.elapsedRealtime() - base
            delay(50)
        }
    }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Stopwatch", style = MaterialTheme.typography.titleMedium)
            Text(
                "%02d:%02d.%02d".format(
                    (elapsedMs / 1000) / 60,
                    (elapsedMs / 1000) % 60,
                    (elapsedMs % 1000) / 10
                )
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    base = SystemClock.elapsedRealtime()
                    running = true
                }) { Text("Start") }
                Button(onClick = { running = false }) { Text("Pause") }
                Button(onClick = {
                    running = false
                    elapsedMs = 0L
                }) { Text("Reset") }
            }
        }
    }
}

@Composable
private fun HomeScreen() {
    var note by remember { mutableStateOf("") }
    var waterGoal by remember { mutableStateOf(2500) }
    var waterDrank by remember { mutableStateOf(0) }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Quick note", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("How did training go?") }
                )
                Text(
                    "Tip: Short, brisk hill sprints and ruck-walks are great right now.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Hydration", style = MaterialTheme.typography.titleMedium)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = waterGoal.toString(),
                        onValueChange = { v -> v.toIntOrNull()?.let { waterGoal = it } },
                        label = { Text("Daily goal (ml)") },
                        modifier = Modifier.width(160.dp)
                    )
                    Button(onClick = { waterDrank = 0 }) { Text("Reset") }
                }
                HydrationChip(targetMl = waterGoal, drankMl = waterDrank) { add ->
                    waterDrank = (waterDrank + add).coerceAtMost(waterGoal)
                }
            }
        }

        StopwatchCard()
    }
}
