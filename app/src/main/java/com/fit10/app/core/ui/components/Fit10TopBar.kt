package com.fit10.app.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.fit10.app.core.ui.theme.Fit10Theme

/**
 * Modern Fit10 top app bar — centre-aligned title, optional leading navigation icon and
 * trailing action slot.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Fit10TopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor             = MaterialTheme.colorScheme.background,
        titleContentColor          = MaterialTheme.colorScheme.onBackground,
        navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
        actionIconContentColor     = MaterialTheme.colorScheme.onBackground,
    ),
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text          = title,
                style         = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                maxLines      = 1,
                overflow      = TextOverflow.Ellipsis,
            )
        },
        modifier          = modifier,
        navigationIcon    = { navigationIcon?.invoke() },
        actions           = { actions() },
        scrollBehavior    = scrollBehavior,
        colors            = colors,
    )
}

/**
 * Standard back-navigation icon for use with [Fit10TopBar].
 */
@Composable
fun Fit10BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector        = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = "Navigate back",
            tint               = MaterialTheme.colorScheme.onSurface,
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFFF8FAF9, widthDp = 360)
@Composable
private fun Fit10TopBarPreview() {
    Fit10Theme {
        Fit10TopBar(
            title          = "Today's Workout",
            navigationIcon = { Fit10BackButton(onClick = {}) },
            actions        = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector        = Icons.Rounded.MoreVert,
                        contentDescription = "More options",
                    )
                }
            }
        )
    }
}
