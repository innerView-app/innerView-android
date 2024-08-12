package com.dev.innerview.feature.peek.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dev.innerview.feature.peek.PeekRoute
import com.dev.innerview.core.navigation.MainTabRoute

fun NavController.navigatePeek(navOptions: NavOptions) {
    navigate(MainTabRoute.Peek, navOptions)
}

fun NavGraphBuilder.peekNavGraph(
    padding: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit
) {
    composable<MainTabRoute.Peek> {
        PeekRoute(padding, onShowErrorSnackBar)
    }
}
