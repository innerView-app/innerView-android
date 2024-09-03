package com.dev.innerview.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun InnerViewDropdownMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    DropdownMenu(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        content()
    }
}

@Composable
fun InnerViewDropdownMenuItem(
    text: String,
    style: TextStyle = MaterialTheme.typography.labelLarge,
    onClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                style = style
            )
        },
        onClick = {
            onClick()
            onDismissRequest()
        }
    )
}