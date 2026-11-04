package com.fit10.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight

val LocalUserName = staticCompositionLocalOf { "" }

@Composable
fun UserAvatar(modifier: Modifier = Modifier, name: String = LocalUserName.current) {
    val initial = name.trim().firstOrNull()?.uppercaseChar()
    BoxWithConstraints(
        modifier = modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (initial != null) {
            val fontSize = with(LocalDensity.current) { (maxWidth * 0.42f).toSp() }
            Text(initial.toString(), fontSize = fontSize, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
        } else {
            Icon(Icons.Rounded.Person, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.fillMaxSize(0.55f))
        }
    }
}
