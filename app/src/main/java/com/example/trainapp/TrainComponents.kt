package com.example.trainapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Minimalist Pixel Board Palette ---
val BoardBlack = Color(0xFF000000)
val BoardOrange = Color(0xFFFF9800)
val BoardGreen = Color(0xFF4CAF50)
val BoardRed = Color(0xFFF44336)
val BoardDivider = Color(0xFF222222)

@Composable
fun TrainCard(train: TrainSchedule) {
    // Clean up car type naming
    val cleanedTrainType = when {
        train.trainType.contains("自強(3000)") -> "自強3000"
        train.trainType.contains("自強") -> "自強"
        train.trainType.contains("區間快") -> "區間快"
        train.trainType.contains("區間") -> "區間車"
        train.trainType.contains("太魯閣") -> "太魯閣"
        train.trainType.contains("普悠瑪") -> "普悠瑪"
        else -> train.trainType.split("(")[0].trim()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BoardBlack)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time
            Text(
                text = train.depTime,
                color = BoardOrange,
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(65.dp)
            )

            // Train Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = cleanedTrainType,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = train.trainNo,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = train.destination,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            // Status
            val isDelay = train.delayMinutes > 0
            Text(
                text = if (isDelay) {
                    stringResource(R.string.status_delay, train.delayMinutes)
                } else {
                    stringResource(R.string.status_on_time)
                },
                color = if (isDelay) BoardRed else BoardGreen,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(80.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
        // Minimalist Divider
        HorizontalDivider(color = BoardDivider, thickness = 1.dp)
    }
}

@Composable
fun TrainListScreen(stationId: String, modifier: Modifier = Modifier) {
    var trains by remember { mutableStateOf(listOf<TrainSchedule>()) }
    val loadingText = stringResource(R.string.loading)

    LaunchedEffect(stationId) {
        TrainRepository.fetchTrains(stationId) { newData ->
            trains = newData
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BoardBlack)
    ) {
        // Simple Header Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("TIME", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.width(65.dp))
            Text("TRAIN / DEST", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.weight(1f))
            Text("STATUS", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.width(80.dp), textAlign = androidx.compose.ui.text.style.TextAlign.End)
        }
        
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            if (trains.isEmpty()) {
                item {
                    Text(
                        text = loadingText,
                        color = BoardOrange,
                        modifier = Modifier.padding(16.dp),
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                items(trains) { train ->
                    TrainCard(train = train)
                }
            }
        }
    }
}
