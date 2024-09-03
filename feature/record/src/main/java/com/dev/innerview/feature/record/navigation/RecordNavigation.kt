package com.dev.innerview.feature.record.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dev.innerview.core.navigation.Route
import com.dev.innerview.feature.record.FilmingScreen
import com.dev.innerview.feature.record.RecordScreen

fun NavController.navigateRecord(innerViewId: String, interviewGroupId: Int) {
    navigate(Route.Records(innerViewId, interviewGroupId))
}

fun NavController.navigateFilming(innerViewId: String, interviewGroupId: Int, question: String) {
    navigate(Route.Filming(innerViewId, interviewGroupId, question))
}

fun NavGraphBuilder.recordNavGraph(
    padding: PaddingValues,
    onBackClick: () -> Unit,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToFilming: (String, Int, String) -> Unit,
    navigateToEdit: (String, Int, String) -> Unit
) {

    composable<Route.Records> { navBackStackEntry ->
        val (innerViewId, interviewGroupId) = navBackStackEntry.toRoute<Route.Records>()
        RecordScreen(
            innerViewId = innerViewId,
            interviewGroupId = interviewGroupId,
            padding = padding,
            onBackClick = onBackClick,
            onShowErrorSnackBar = onShowErrorSnackBar,
            navigateToFilming = navigateToFilming
        )
    }

    composable<Route.Filming> { navBackStackEntry ->
        val (innerViewId, interviewGroupId, question) = navBackStackEntry.toRoute<Route.Filming>()
        FilmingScreen(
            innerViewId = innerViewId,
            interviewGroupId = interviewGroupId,
            question = question,
            padding = padding,
            onBackClick = onBackClick,
            navigationToEdit = navigateToEdit,
            onShowErrorSnackBar = onShowErrorSnackBar
        )
    }
}