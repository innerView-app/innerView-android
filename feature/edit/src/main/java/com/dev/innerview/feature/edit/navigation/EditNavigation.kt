package com.dev.innerview.feature.edit.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dev.innerview.core.navigation.MainTabRoute
import com.dev.innerview.core.navigation.Route
import com.dev.innerview.feature.edit.EditHomeScreen
import com.dev.innerview.feature.edit.EditRoute

fun NavController.navigateEditHome(navOptions: NavOptions) {
    navigate(MainTabRoute.EditHome, navOptions)
}

fun NavController.navigateEdit(innerProjectId: Int) {
    popBackStack<Route.Records>(inclusive = false)
    navigate(Route.Edit(innerProjectId))
}

fun NavGraphBuilder.editNavGraph(
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    onBackClick: () -> Unit
) {
    composable<MainTabRoute.EditHome> {
        EditHomeScreen(
            onShowErrorSnackBar = onShowErrorSnackBar,
            onBackClick = onBackClick
        )
    }

    composable<Route.Edit> { navBackStackEntry ->
        val (innerProjectId) = navBackStackEntry.toRoute<Route.Edit>()
        EditRoute(
            innerProjectId = innerProjectId,
            onShowErrorSnackBar = onShowErrorSnackBar,
            onBackClick = onBackClick
        )
    }
}
