package com.fit10.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fit10.app.core.feedback.LocalHapticsEnabled
import com.fit10.app.core.ui.theme.Fit10Theme
import com.fit10.app.core.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as Fit10Application).container
        setContent {
            val themeMode by container.preferencesDataSource.themeMode
                .collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
            val hapticsEnabled by container.preferencesDataSource.hapticsEnabled
                .collectAsStateWithLifecycle(initialValue = true)

            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT  -> false
                ThemeMode.DARK   -> true
            }
            // Keep status and navigation bar icons readable when the in-app theme overrides the system one.
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
                )
                onDispose {}
            }
            DisposableEffect(hapticsEnabled) {
                container.feedbackCoordinator.hapticsEnabled = hapticsEnabled
                onDispose {}
            }

            Fit10Theme(themeMode = themeMode) {
                CompositionLocalProvider(LocalHapticsEnabled provides hapticsEnabled) {
                    Fit10App()
                }
            }
        }
    }
}
