package com.dev.innerview.feature.main

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.dev.innerview.feature.main.component.MainBottomBar
import com.dev.innerview.feature.main.component.MainNavHost
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

private var toast: Toast? = null

private fun showToast(context: Context, text: String) {
    toast?.cancel()
    toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
    toast?.show()
}

@Composable
internal fun MainScreen(
    navigator: MainNavigator = rememberMainNavigator()
) {
    val snackBarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val onShowErrorSnackBar: (throwable: Throwable?) -> Unit = { throwable ->
        coroutineScope.launch {

            val unknownErrorMessage =
                context.getString(R.string.feature_main_error_message_unknown)

            snackBarHostState.showSnackbar(
                when (throwable) {  // throwable 타입 별 snackBar 처리
                    is IllegalArgumentException -> throwable.message ?: unknownErrorMessage
                    else -> unknownErrorMessage
                }
            )
        }
    }
    val onShowToast:(text:String) -> Unit = { text -> showToast(context, text) }

    MainScreenContent(
        navigator = navigator,
        onShowErrorSnackBar = onShowErrorSnackBar,
        snackBarHostState = snackBarHostState,
        onShowToast = onShowToast
    )
}

@Composable
private fun MainScreenContent(
    modifier: Modifier = Modifier,
    navigator: MainNavigator,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    onShowToast: (text: String) -> Unit,
    snackBarHostState: SnackbarHostState,
) {
    Scaffold(
        modifier = modifier,
        content = { padding ->
            MainNavHost(
                navigator = navigator,
                padding = padding,
                onShowErrorSnackBar = onShowErrorSnackBar,
                onShowToast = onShowToast
            )
        },
        bottomBar = {
            MainBottomBar(
                visible = navigator.shouldShowBottomBar(),
                tabs = MainTab.entries.toPersistentList(),
                currentTab = navigator.currentTab,
                onTabSelected = { navigator.navigate(it) }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    )
}