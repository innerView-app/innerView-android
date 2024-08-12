package com.dev.innerview.feature.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.innerview.core.designsystem.component.InnerViewAppBarIcon
import com.dev.innerview.core.designsystem.component.InnerViewCard
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.component.appBarSize
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun HomeRoute(
    padding: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    onInnerViewClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        viewModel.errorFlow.collectLatest { throwable -> onShowErrorSnackBar(throwable) }
    }

    HomeScreen(
        padding = padding,
        onInnerViewClick = onInnerViewClick
    )
}

@Composable
private fun HomeScreen(
    padding: PaddingValues,
    onInnerViewClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        InnerViewTopAppBar(
            titleString = "innerView",
            navigationType = TopAppBarNavigationType.None,
            actionButtons = {
                InnerViewAppBarIcon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    navigationIconContentDescription = null
                )
            }
        )
        Column(
            modifier = Modifier
                .padding(top = appBarSize)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(Paddings.large)
        ) {
            InnerViewCard(
                modifier = Modifier
                    .height(120.dp)
                    .clickable { onInnerViewClick("Billie Eilish") }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Billie Eilish",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(
                                start = Paddings.large,
                                end = Paddings.large,
                                top = Paddings.large
                            )
                    )

                    Text(
                        text = "2017.10.18 ~ D+2443",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(
                                start = Paddings.large,
                                end = Paddings.large,
                                bottom = Paddings.large
                            )
                    )

                    Text(
                        text = "1 year",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(
                                start = Paddings.large,
                                end = Paddings.large,
                                bottom = Paddings.large
                            )
                    )
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun HomeScreenPreview() {
    InnerViewTheme {
        HomeScreen(padding = PaddingValues(), onInnerViewClick = {})
    }
}
