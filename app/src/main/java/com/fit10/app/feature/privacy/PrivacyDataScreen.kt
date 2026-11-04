package com.fit10.app.feature.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.core.ui.components.BackBar
import com.fit10.app.core.ui.components.GroupedList
import com.fit10.app.core.ui.components.LargeTitle
import com.fit10.app.core.ui.components.ListRow
import com.fit10.app.core.ui.components.SectionLabel

@Composable
fun PrivacyDataScreen(
    onBack: () -> Unit = {},
    onDownloadData: () -> Unit = {},
    onManagePermissions: () -> Unit = {},
    onDeleteAccount: () -> Unit = {}
) {
    var confirmDelete by remember { mutableStateOf(false) }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete all your data?") },
            text = { Text("Your profile, check-ins and workout history will be erased from this phone. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = { confirmDelete = false; onDeleteAccount() }) {
                    Text("Delete everything", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } }
        )
    }
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding().navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        BackBar(onBack)
        Column(Modifier.padding(horizontal = 20.dp)) {
            LargeTitle("Privacy", "Your data stays on this phone. No account, no ads, no tracking.")
            SectionLabel("What we store")
            listOf(
                "Your name and recovery answers",
                "Daily sleep check-ins",
                "Workouts you finish and how they felt",
            ).forEach {
                Text("• $it", fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(vertical = 3.dp, horizontal = 4.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Everything is encrypted on this device and never sent to our servers.",
                fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 4.dp)
            )
            SectionLabel("Your data")
            GroupedList {
                ListRow(Icons.Rounded.IosShare, "Export my data", onClick = onDownloadData)
                ListRow(Icons.Rounded.Delete, "Delete all my data", showDivider = false, onClick = { confirmDelete = true })
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
