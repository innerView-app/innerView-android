package com.dev.innerview.feature.edit.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dev.innerview.core.navigation.MainTabRoute
import com.dev.innerview.core.navigation.Route
import com.dev.innerview.feature.edit.EditRoute

fun NavController.navigateEdit(navOptions: NavOptions) {
    navigate(MainTabRoute.Edit, navOptions)
}

fun NavController.navigateEditWithArgs(
    innerViewId: String,
    interviewGroupId: Int,
    question: String
) {
    popBackStack<Route.Records>(inclusive = false)
    navigate(Route.EditWithArgs(innerViewId, interviewGroupId, question))
}

fun NavGraphBuilder.editNavGraph(
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    onBackClick: () -> Unit
) {
    composable<MainTabRoute.Edit> {
        EditRoute(
            innerViewId = null,
            interviewGroupId = null,
            question = null,
            onShowErrorSnackBar = onShowErrorSnackBar,
            onBackClick = onBackClick
        )
    }

    composable<Route.EditWithArgs> { navBackStackEntry ->
        val (innerViewId, interviewGroupId, question) = navBackStackEntry.toRoute<Route.EditWithArgs>()
        EditRoute(
            innerViewId = innerViewId,
            interviewGroupId = interviewGroupId,
            question = question,
            onShowErrorSnackBar = onShowErrorSnackBar,
            onBackClick = onBackClick
        )
    }
}
