package com.dev.innerview.feature.main.component

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.dev.innerview.feature.edit.navigation.editNavGraph
import com.dev.innerview.feature.home.navigation.homeNavGraph
import com.dev.innerview.feature.main.MainNavigator
import com.dev.innerview.feature.peek.navigation.peekNavGraph
import com.dev.innerview.feature.peek.navigation.profileNavGraph

@Composable
internal fun MainNavHost(
    navigator: MainNavigator,
    padding: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    NavHost(
        navController = navigator.navController,
        startDestination = navigator.startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        homeNavGraph(
            padding = padding,
            onShowErrorSnackBar = onShowErrorSnackBar,
            onInnerViewClick = { navigator.navigateInnerViewDetail(it) },
            onBackClick = { navigator.popBackStackIfNotHome() },
        )
        peekNavGraph(
            padding = padding,
            onShowErrorSnackBar = onShowErrorSnackBar
        )
        editNavGraph(
            padding = padding,
            onShowErrorSnackBar = onShowErrorSnackBar,
            onBackClick = { navigator.popBackStackIfNotHome() }
        )
        profileNavGraph(
            padding = padding,
            onShowErrorSnackBar = onShowErrorSnackBar
        )
    }
}