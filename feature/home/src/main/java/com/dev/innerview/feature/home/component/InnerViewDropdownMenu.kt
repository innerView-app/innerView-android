package com.dev.innerview.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.dev.innerview.feature.home.R
import com.dev.innerview.feature.home.model.InnerViewItemUiState

@Composable
fun InnerViewDropdownMenu(
    modifier: Modifier = Modifier,
    itemUiState: InnerViewItemUiState,
    onDeleteInnerView: (String) -> Unit,
    onDismissRequest: (String) -> Unit
) {
    DropdownMenu(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        expanded = itemUiState.isDropdownMenuVisible,
        onDismissRequest = {
            onDismissRequest(itemUiState.id)
        }
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.feature_home_innerview_delete),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.error
                    )
                )
            },
            onClick = {
                onDeleteInnerView(itemUiState.id)
                onDismissRequest(itemUiState.id)
            }
        )
    }
}