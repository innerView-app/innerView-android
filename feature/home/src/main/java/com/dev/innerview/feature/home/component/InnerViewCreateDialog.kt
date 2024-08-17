package com.dev.innerview.feature.home.component

import android.content.res.Configuration
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dev.innerview.core.designsystem.component.InnerViewDialog
import com.dev.innerview.core.designsystem.component.InnerViewDialogTextField
import com.dev.innerview.core.designsystem.component.InnerViewRadioButton
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.feature.home.R
import com.dev.innerview.feature.home.model.HomeUiState

@Composable
fun InnerViewCreateDialog(
    homeUiState: HomeUiState,
    maxInnerViewTitleLength: Int,
    onTitleChange: (String) -> Unit,
    onSelectType: (InnerViewType) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit
) {
    InnerViewDialog(
        titleText = stringResource(id = R.string.feature_home_innerview_create),
        contentText = stringResource(id = R.string.feature_home_innerview_type_description),
        confirmText = stringResource(id = R.string.feature_home_dialog_confirm),
        dismissText = stringResource(id = R.string.feature_home_dialog_dismiss),
        onDismissRequest = { onDismissRequest() },
        onConfirmRequest = { onConfirmRequest() }
    ) {
        InnerViewDialogTextField(
            value = { homeUiState.dialogInnerViewTitle },
            onValueChange = { onTitleChange(it) },
            placeholderText = stringResource(id = R.string.feature_home_innerview_create_placeholder),
            labelText = stringResource(id = R.string.feature_home_innerview_create_label),
            supportingText = "${homeUiState.dialogInnerViewTitle.length}/$maxInnerViewTitleLength"
        )
        InnerViewType.entries.forEachIndexed { i, type ->
            val typeText = when (type) {
                InnerViewType.YEAR -> stringResource(id = R.string.feature_home_innerview_type_year)
                InnerViewType.MONTH -> stringResource(id = R.string.feature_home_innerview_type_month)
                InnerViewType.WEEK -> stringResource(id = R.string.feature_home_innerview_type_week)
                InnerViewType.DAY -> stringResource(id = R.string.feature_home_innerview_type_day)
            }
            InnerViewRadioButton(
                textString = typeText,
                selected = { homeUiState.dialogSelectedType == type },
                onClick = { onSelectType(type) }
            )
            if (InnerViewType.entries.size - 1 > i) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun InnerViewCreateDialogPreview() {
    InnerViewTheme {
        InnerViewCreateDialog(
            homeUiState = HomeUiState(),
            maxInnerViewTitleLength = 40,
            onTitleChange = {},
            onSelectType = {},
            onDismissRequest = {},
            onConfirmRequest = {}
        )
    }
}