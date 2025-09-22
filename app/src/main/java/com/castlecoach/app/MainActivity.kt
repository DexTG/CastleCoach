package com.castlecoach.app
}
}


@Composable
private fun TimeField(label: String, value: Int, onChange: (Int)->Unit) {
var text by remember { mutableStateOf(value.toString().padStart(2,'0')) }
OutlinedTextField(
value = text,
onValueChange = {
text = it.take(2).filter { c -> c.isDigit() }
onChange(text.toIntOrNull() ?: 0)
},
label = { Text(label) },
modifier = Modifier.width(100.dp)
)
}


@Composable
fun TimersScreen(onBack: ()->Unit) {
var stopwatchRunning by remember { mutableStateOf(false) }
var stopwatchBase by remember { mutableStateOf(0L) }
val chronometer = remember { androidx.compose.runtime.mutableStateOf(SystemClock.elapsedRealtime()) }


var timerSeconds by remember { mutableStateOf(60) }
var timerRemaining by remember { mutableStateOf(60) }
var timerRunning by remember { mutableStateOf(false) }


LaunchedEffect(timerRunning) {
while (timerRunning && timerRemaining > 0) {
kotlinx.coroutines.delay(1000)
timerRemaining -= 1
}
if (timerRunning) timerRunning = false
}


Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
Button(onClick = onBack) { Text("Back") }
Text("Timers & Stopwatch", style = MaterialTheme.typography.titleLarge)
}


// Stopwatch
ElevatedCard(Modifier.fillMaxWidth()) {
Column(Modifier.padding(12.dp)) {
Text("Stopwatch", style = MaterialTheme.typography.titleMedium)
val elapsed = if (stopwatchRunning) (SystemClock.elapsedRealtime() - stopwatchBase)/1000 else (chronometer.value - stopwatchBase)/1000
Text("${elapsed}s", style = MaterialTheme.typography.headlineMedium)
Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
Button(onClick = {
stopwatchBase = SystemClock.elapsedRealtime()
stopwatchRunning = true
}) { Text("Start") }
Button(onClick = { stopwatchRunning = false; chronometer.value = SystemClock.elapsedRealtime() }) { Text("Stop") }
Button(onClick = { stopwatchRunning = false; stopwatchBase = SystemClock.elapsedRealtime(); chronometer.value = stopwatchBase }) { Text("Reset") }
}
}
}


// Timer
ElevatedCard(Modifier.fillMaxWidth()) {
Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
Text("Countdown Timer", style = MaterialTheme.typography.titleMedium)
OutlinedTextField(value = timerSeconds.toString(), onValueChange = {
timerSeconds = it.filter(Char::isDigit).take(4).toIntOrNull() ?: 0
}, label = { Text("Seconds") })
Text("Remaining: ${timerRemaining}s", style = MaterialTheme.typography.headlineSmall)
Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
Button(onClick = { timerRemaining = timerSeconds; timerRunning = true }) { Text("Start") }
Button(onClick = { timerRunning = false }) { Text("Pause") }
Button(onClick = { timerRunning = false; timerRemaining = timerSeconds }) { Text("Reset") }
}
Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
AssistChip(onClick = { timerSeconds = 20; timerRemaining = 20 }, label = { Text("20s hang") })
AssistChip(onClick = { timerSeconds = 60; timerRemaining = 60 }, label = { Text("60s plank") })
AssistChip(onClick = { timerSeconds = 90; timerRemaining = 90 }, label = { Text("90s carry") })
}
}
}
}
}
