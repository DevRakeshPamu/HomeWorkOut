package com.fit10.app.feature.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.core.ui.components.BackBar
import com.fit10.app.core.ui.components.SectionLabel
import com.fit10.app.core.ui.figure.ExerciseDemo
import com.fit10.app.data.seed.ExerciseCatalog

@Composable
fun ExerciseDetailScreen(
    exerciseId: String = "push_ups",
    onBack: () -> Unit = {},
    onReplace: () -> Unit = {},
    onGotIt: () -> Unit = {}
) {
    val exercise = remember(exerciseId) { ExerciseCatalog.ALL.find { it.id == exerciseId } ?: ExerciseCatalog.ALL.first() }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding().navigationBarsPadding()) {
        BackBar(onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            ExerciseDemo(exercise.id, Modifier.fillMaxWidth().aspectRatio(1.2f).clip(RoundedCornerShape(24.dp)))
            Spacer(Modifier.height(20.dp))
            Text(exercise.name, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            if (exercise.targetMuscles.isNotEmpty()) {
                Text(exercise.targetMuscles.joinToString(" · "), fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(12.dp))
            Text(exercise.description, fontSize = 15.sp, lineHeight = 22.sp, color = MaterialTheme.colorScheme.onBackground)

            if (exercise.instructions.isNotEmpty()) {
                SectionLabel("How to do it")
                exercise.instructions.forEachIndexed { i, step ->
                    Row(Modifier.padding(vertical = 6.dp)) {
                        Box(
                            Modifier.size(26.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${i + 1}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(step, fontSize = 15.sp, lineHeight = 22.sp, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
            if (exercise.tips.isNotEmpty()) {
                SectionLabel("Good to know")
                exercise.tips.forEach { tip ->
                    Text("• $tip", fontSize = 15.sp, lineHeight = 22.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 3.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
        }
        Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            OutlinedButton(onClick = onReplace, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text("Show gentler options", fontSize = 16.sp)
            }
        }
    }
}
