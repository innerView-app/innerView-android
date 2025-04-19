package com.dev.innerview.feature.record.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
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
    navigateToEdit: (Int) -> Unit
) {

    composable<Route.Records> {
        RecordScreen(
            padding = padding,
            onBackClick = onBackClick,
            onShowErrorSnackBar = onShowErrorSnackBar,
            navigateToFilming = navigateToFilming,
            navigationToEdit = navigateToEdit
        )
    }

    composable<Route.Filming> {
        FilmingScreen(
            padding = padding,
            onBackClick = onBackClick,
            navigationToEdit = navigateToEdit,
            onShowErrorSnackBar = onShowErrorSnackBar
        )
    }
}