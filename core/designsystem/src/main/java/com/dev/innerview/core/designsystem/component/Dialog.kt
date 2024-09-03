package com.dev.innerview.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings

@Composable
fun InnerViewDialog(
    titleText: String = "",
    contentText: String = "",
    confirmText: String = "",
    dismissText: String = "",
    onDismissRequest: () -> Unit = {},
    onConfirmRequest: () -> Unit = {},
    content: (@Composable ColumnScope.() -> Unit)? = null
) {
    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Paddings.xlarge),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(Paddings.extra),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = titleText,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = contentText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        lineHeight = 18.sp
                    )
                )
                Spacer(modifier = Modifier.size(16.dp))
                content?.let { it() }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .padding(end = Paddings.large)
                            .size(width = 48.dp, height = 32.dp)
                            .clickable { onDismissRequest() }
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = dismissText,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(width = 48.dp, height = 32.dp)
                            .clickable { onConfirmRequest() }
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = confirmText,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun InnerViewDialogPreview() {
    InnerViewTheme {
        InnerViewDialog(
            titleText = "이너뷰 생성",
            contentText = "1 year : 1년에 한 번 같은 질문으로 인터뷰\n" +
                    "1 month : 1달에 한 번 같은 질문으로 인터뷰\n" +
                    "1 week: 1주일에 한 번 같은 질문으로 인터뷰\n" +
                    "매일 : 매일 다른 질문으로 인터뷰",
            confirmText = "생성",
            dismissText = "취소"
        ) {
            InnerViewDialogTextField(
                value = { "" },
                onValueChange = { },
                placeholderText = "이너뷰 제목을 입력하세요.",
                labelText = "innerView title",
                supportingText = "0/50"
            )
            InnerViewRadioButton(
                selected = true,
                onClick = {}
            ) {
                Text(
                    text = "1 year",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            HorizontalDivider()
            InnerViewRadioButton(
                selected = false,
                onClick = {}
            ) {
                Text(
                    text = "1 month",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            HorizontalDivider()
            InnerViewRadioButton(
                selected = false,
                onClick = {}
            ) {
                Text(
                    text = "1 week",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            HorizontalDivider()
            InnerViewRadioButton(
                selected = false,
                onClick = {}
            ) {
                Text(
                    text = "매일",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
