package com.fit10.app.feature.reminders

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fit10.app.Fit10Application
import com.fit10.app.core.reminder.ReminderScheduler
import com.fit10.app.core.ui.components.BackBar
import com.fit10.app.core.ui.components.GroupedList
import com.fit10.app.core.ui.components.LargeTitle
import com.fit10.app.core.ui.components.ListRow
import com.fit10.app.core.ui.components.LocalUserName
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ReminderSettingsScreen(
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    onCancel: () -> Unit = onBack,
    onTestNotification: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = (context.applicationContext as Fit10Application).container.preferencesDataSource
    val scope = rememberCoroutineScope()
    val enabled by prefs.reminderEnabled.collectAsStateWithLifecycle(false)
    val time by prefs.reminderTime.collectAsStateWithLifecycle(20 to 30)
    val name = LocalUserName.current
    var picking by remember { mutableStateOf(false) }

    fun apply(on: Boolean, h: Int, m: Int) {
        scope.launch { prefs.setReminder(on, h, m) }
        if (on) ReminderScheduler.schedule(context, h, m) else ReminderScheduler.cancel(context)
    }

    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        apply(granted, time.first, time.second)
    }

    if (picking) {
        val pickerState = rememberTimePickerState(time.first, time.second)
        AlertDialog(
            onDismissRequest = { picking = false },
            confirmButton = {
                TextButton(onClick = { picking = false; apply(enabled, pickerState.hour, pickerState.minute) }) { Text("Set") }
            },
            dismissButton = { TextButton(onClick = { picking = false }) { Text("Cancel") } },
            text = { TimePicker(pickerState) }
        )
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding().navigationBarsPadding()) {
        BackBar(onBack)
        Column(Modifier.padding(horizontal = 20.dp)) {
            LargeTitle("Reminders", "One gentle nudge a day, at a time that suits you. Many moms pick nap time.")
            GroupedList {
                ListRow(
                    Icons.Rounded.Notifications, "Daily reminder",
                    trailing = {
                        Switch(checked = enabled, onCheckedChange = { on ->
                            if (on && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permission.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                apply(on, time.first, time.second)
                            }
                        })
                    }
                )
                ListRow(
                    Icons.Rounded.Schedule, "Time",
                    LocalTime.of(time.first, time.second).format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
                    onClick = { picking = true }
                )
                ListRow(Icons.Rounded.Send, "Send a test", showDivider = false, onClick = { ReminderScheduler.show(context, name) })
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "No guilt trips. If you miss a day, we'll just see you tomorrow.",
                fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}
