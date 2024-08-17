package com.dev.innerview.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.innerview.core.designsystem.theme.InnerViewTheme

@Composable
fun InnerViewRadioButton(
    textString: String = "",
    selected: () -> Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .selectable(
                selected = selected(),
                onClick = { onClick() },
                role = Role.RadioButton
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = textString,
            style = MaterialTheme.typography.bodySmall,
        )
        RadioButton(
            selected = selected(),
            onClick = null,
            colors = RadioButtonColors(
                selectedColor = MaterialTheme.colorScheme.tertiary,
                unselectedColor = MaterialTheme.colorScheme.tertiary,
                disabledSelectedColor = MaterialTheme.colorScheme.tertiary,
                disabledUnselectedColor = MaterialTheme.colorScheme.tertiary
            )
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun InnerViewRadioButtonPreview() {
    InnerViewTheme {
        InnerViewRadioButton(
            textString = "1 year",
            selected = { true },
            onClick = {}
        )
    }
}
