package com.fit10.app.feature.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.core.ui.components.BackBar
import com.fit10.app.core.ui.components.IconTile
import com.fit10.app.core.ui.components.PressableCard
import com.fit10.app.data.seed.ExerciseCatalog

private const val FREE_SWAPS = 2

@Composable
fun ExerciseReplacementScreen(
    exerciseId: String = "push_ups",
    isPro: Boolean = false,
    onBack: () -> Unit = {},
    onSelectVariation: (String) -> Unit = {},
    onKeepOriginal: () -> Unit = {},
    onNavigateToPaywall: () -> Unit = {}
) {
    val original = remember(exerciseId) { ExerciseCatalog.ALL.find { it.id == exerciseId } ?: ExerciseCatalog.ALL.first() }
    val options = remember(original.id) {
        ExerciseCatalog.ALL
            .filter { it.id != original.id && it.focusArea == original.focusArea && it.impactLevel.ordinal <= original.impactLevel.ordinal }
            .sortedBy { it.impactLevel.ordinal }
            .take(6)
    }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding().navigationBarsPadding()) {
        BackBar(onBack)
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text("Gentler options", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text("Instead of ${original.name}. Same goal, easier on your joints and core.",
                    fontSize = 15.sp, lineHeight = 22.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
            }
            if (options.isEmpty()) {
                item {
                    Text("This is already the gentlest version of this move.", fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 24.dp))
                }
            }
            items(options.withIndex().toList(), key = { it.value.id }) { (index, option) ->
                val locked = !isPro && index >= FREE_SWAPS
                PressableCard(onClick = { if (locked) onNavigateToPaywall() else onSelectVariation(option.id) }) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconTile(Icons.Rounded.Spa, 44.dp)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(option.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            Text(option.description, fontSize = 13.sp, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (locked) Icon(Icons.Rounded.Lock, "Pro", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
        TextButton(onClick = onKeepOriginal, modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp)) {
            Text("Keep ${original.name}")
        }
    }
}
