package com.dev.innerview.feature.peek.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dev.innerview.feature.peek.ProfileRoute
import com.dev.innerview.core.navigation.MainTabRoute

fun NavController.navigateProfile(navOptions: NavOptions) {
    navigate(MainTabRoute.Profile, navOptions)
}

fun NavGraphBuilder.profileNavGraph(
    padding: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit
) {
    composable<MainTabRoute.Profile> {
        ProfileRoute(padding, onShowErrorSnackBar)
    }
}
