package com.dev.innerview.feature.edit.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dev.innerview.feature.edit.EditRoute
import com.dev.innerview.core.navigation.MainTabRoute

fun NavController.navigateEdit(navOptions: NavOptions) {
    navigate(MainTabRoute.Edit, navOptions)
}

fun NavGraphBuilder.editNavGraph(
    padding: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit
) {
    composable<MainTabRoute.Edit> {
        EditRoute(padding, onShowErrorSnackBar)
    }
}
