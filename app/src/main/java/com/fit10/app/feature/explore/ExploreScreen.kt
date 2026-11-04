package com.fit10.app.feature.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AccessibilityNew
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fit10.app.core.ui.components.IconTile
import com.fit10.app.core.ui.components.LargeTitle
import com.fit10.app.core.ui.components.PillRow
import com.fit10.app.core.ui.components.PressableCard
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.IntensityPreference
import com.fit10.app.domain.model.Workout

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onSelectWorkout: (workoutId: String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val filters = listOf<Pair<FocusArea?, String>>(
        null to "All",
        FocusArea.CORE to "Core",
        FocusArea.MOBILITY to "Mobility",
        FocusArea.LOWER_BODY to "Lower body",
        FocusArea.UPPER_BODY to "Upper body",
        FocusArea.FULL_BODY to "Full body",
    )
    LazyColumn(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { LargeTitle("Workouts", "10 minutes. No equipment. Gentle on your body.") }
        item {
            PillRow(filters, state.selectedFilter, viewModel::setFilter)
            Spacer(Modifier.height(8.dp))
        }
        items(state.filteredWorkouts, key = { it.id }) { workout ->
            WorkoutRow(workout, Modifier.animateItem()) { onSelectWorkout(workout.id) }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun WorkoutRow(workout: Workout, modifier: Modifier, onClick: () -> Unit) {
    PressableCard(modifier, onClick = onClick) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconTile(iconFor(workout.focusArea))
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(workout.title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(2.dp))
                Text(
                    "${workout.estimatedDurationMinutes} min · ${workout.exercises.size} moves · ${intensityLabel(workout.intensity)}",
                    fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(22.dp))
        }
    }
}

private fun iconFor(area: FocusArea) = when (area) {
    FocusArea.CORE -> Icons.Rounded.Spa
    FocusArea.MOBILITY -> Icons.Rounded.SelfImprovement
    FocusArea.LOWER_BODY -> Icons.Rounded.DirectionsWalk
    FocusArea.UPPER_BODY -> Icons.Rounded.FitnessCenter
    FocusArea.FULL_BODY -> Icons.Rounded.AccessibilityNew
}

private fun intensityLabel(i: IntensityPreference) = when (i) {
    IntensityPreference.EASY -> "Gentle"
    IntensityPreference.MODERATE -> "Steady"
    IntensityPreference.CHALLENGING -> "Stronger"
}
