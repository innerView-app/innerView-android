package com.dev.innerview.feature.edit.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.dev.innerview.core.navigation.DEEP_LINK_BASE_PATH
import com.dev.innerview.core.navigation.MainTabRoute
import com.dev.innerview.core.navigation.Route
import com.dev.innerview.feature.edit.EditHomeScreen
import com.dev.innerview.feature.edit.EditScreen
import com.dev.innerview.feature.edit.PlayerScreen

fun NavController.navigateEditHome(navOptions: NavOptions) {
    navigate(MainTabRoute.EditHome, navOptions)
}

fun NavController.navigateEdit(innerProjectId: Int) {
    popBackStack<Route.Records>(inclusive = false)
    navigate(Route.Edit(innerProjectId))
}

fun NavController.navigatePlayer(innerProjectIds: List<Int>) {
    navigate(Route.Player(innerProjectIds))
}

fun NavGraphBuilder.editNavGraph(
    padding: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToEdit: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    composable<MainTabRoute.EditHome> {
        EditHomeScreen(
            padding = padding,
            onShowErrorSnackBar = onShowErrorSnackBar,
            navigateToEdit = navigateToEdit
        )
    }

    composable<Route.Edit>(
        deepLinks = listOf(navDeepLink<Route.Edit>(basePath = "$DEEP_LINK_BASE_PATH/edit"))
    ) {
        EditScreen(
            onShowErrorSnackBar = onShowErrorSnackBar,
            onBackClick = onBackClick
        )
    }

    composable<Route.Player>(
        deepLinks = listOf(navDeepLink<Route.Player>(basePath = "$DEEP_LINK_BASE_PATH/player"))
    ) {
        PlayerScreen(
            onShowErrorSnackBar = onShowErrorSnackBar,
            onBackClick = onBackClick
        )
    }
}
