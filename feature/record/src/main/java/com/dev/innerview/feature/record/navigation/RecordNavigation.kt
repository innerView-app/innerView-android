package com.dev.innerview.feature.record.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dev.innerview.core.navigation.Route
import com.dev.innerview.feature.record.RecordScreen

fun NavController.navigateRecord(id: String) {
    navigate(Route.Records(id))
}

fun NavGraphBuilder.recordNavGraph(
    padding: PaddingValues,
    onBackClick: () -> Unit,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
) {

    composable<Route.Records> { navBackStackEntry ->
        val id = navBackStackEntry.toRoute<Route.Records>().id
        RecordScreen(
            id = id,
            padding = padding,
            onBackClick = onBackClick,
            onShowErrorSnackBar = onShowErrorSnackBar
        )
    }
}