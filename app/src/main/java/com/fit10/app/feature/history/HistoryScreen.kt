package com.fit10.app.feature.history

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fit10.app.core.ui.components.IconTile
import com.fit10.app.core.ui.components.LargeTitle
import com.fit10.app.core.ui.components.PressableCard
import com.fit10.app.core.ui.components.SectionLabel
import com.fit10.app.domain.model.WorkoutResult
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onExploreWorkouts: () -> Unit = {},
    onPreviewWorkout: (String) -> Unit = {}
) {
    val history by viewModel.history.collectAsStateWithLifecycle()
    val zone = ZoneId.systemDefault()
    val doneDays = history.recentResults.map { Instant.ofEpochMilli(it.completedAtEpochMs).atZone(zone).toLocalDate() }.toSet()

    LazyColumn(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { LargeTitle("Progress", "Every session counts, however small.") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("Sessions", history.totalWorkoutsCompleted, Modifier.weight(1f))
                StatTile("Minutes", history.totalMinutesWorkedOut, Modifier.weight(1f))
                StatTile("Best streak", history.bestStreakDays, Modifier.weight(1f))
            }
        }
        item { WeekStrip(doneDays) }
        if (history.recentResults.isEmpty()) {
            item { EmptyState(onExploreWorkouts) }
        } else {
            item { SectionLabel("Recent") }
            items(history.recentResults, key = { it.sessionId }) { result ->
                ResultRow(result, Modifier.animateItem()) { onPreviewWorkout(result.workoutId) }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun StatTile(label: String, value: Int, modifier: Modifier) {
    val shown by animateIntAsState(value, tween(800), label = "stat")
    PressableCard(modifier) {
        Column(Modifier.padding(vertical = 16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$shown", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun WeekStrip(doneDays: Set<LocalDate>) {
    val today = LocalDate.now()
    val monday = today.with(DayOfWeek.MONDAY)
    PressableCard {
        Column(Modifier.padding(16.dp)) {
            Text("This week", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                (0..6).forEach { i ->
                    val day = monday.plusDays(i.toLong())
                    val done = day in doneDays
                    val isToday = day == today
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            day.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                            fontSize = 12.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(6.dp))
                        Box(
                            Modifier.size(32.dp).clip(CircleShape)
                                .background(if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (done) Icon(Icons.Rounded.Check, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultRow(result: WorkoutResult, modifier: Modifier, onClick: () -> Unit) {
    val date = Instant.ofEpochMilli(result.completedAtEpochMs).atZone(ZoneId.systemDefault()).toLocalDate()
    val label = when (date) {
        LocalDate.now() -> "Today"
        LocalDate.now().minusDays(1) -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("EEE, d MMM"))
    }
    PressableCard(modifier, onClick = onClick) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            IconTile(Icons.Rounded.Check, 40.dp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(result.workoutTitle, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text("$label · ${(result.durationSeconds + 30) / 60} min", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun EmptyState(onExplore: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        IconTile(Icons.Rounded.Spa, 64.dp)
        Spacer(Modifier.height(16.dp))
        Text("Your sessions will show up here", fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text("Start with 10 minutes today.", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))
        Button(onClick = onExplore) { Text("Find a workout") }
    }
}
