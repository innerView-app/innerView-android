package com.dev.innerview.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.dev.innerview.core.navigation.MainTabRoute
import com.dev.innerview.core.navigation.Route
import com.dev.innerview.feature.edit.navigation.navigateEdit
import com.dev.innerview.feature.edit.navigation.navigateEditWithArgs
import com.dev.innerview.feature.home.navigation.navigateHome
import com.dev.innerview.feature.home.navigation.navigateInnerViewDetail
import com.dev.innerview.feature.home.navigation.navigateInnerViewQuestion
import com.dev.innerview.feature.home.navigation.navigateInterviewGroup
import com.dev.innerview.feature.peek.navigation.navigatePeek
import com.dev.innerview.feature.peek.navigation.navigateProfile
import com.dev.innerview.feature.record.navigation.navigateFilming
import com.dev.innerview.feature.record.navigation.navigateRecord

internal class MainNavigator(
    val navController: NavHostController,
) {
    private val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val startDestination = MainTab.HOME.route

    val currentTab: MainTab?
        @Composable get() = MainTab.find { tab ->
            currentDestination?.hasRoute(tab::class) == true
        }

    fun navigate(tab: MainTab) {
        val navOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (tab) {
            MainTab.HOME -> navController.navigateHome(navOptions)
            MainTab.PEEK -> navController.navigatePeek(navOptions)
            MainTab.INTERVIEW_EDIT -> navController.navigateEdit(navOptions)
            MainTab.PROFILE -> navController.navigateProfile(navOptions)
        }
    }

    fun navigateInnerViewDetail(id: String) {
        navController.navigateInnerViewDetail(id)
    }

    fun navigateInterviewGroup() {
        navController.navigateInterviewGroup()
    }

    fun navigateInnerViewQuestion(id: String) {
        navController.navigateInnerViewQuestion(id)
    }

    fun navigateRecord(innerViewId: String, interviewGroupId: Int) {
        navController.navigateRecord(innerViewId, interviewGroupId)
    }

    fun navigateFilming(innerViewId: String, interviewGroupId: Int, question: String) {
        navController.navigateFilming(innerViewId, interviewGroupId, question)
    }

    fun navigateEditWithArgs(innerViewId: String, interviewGroupId: Int, question: String) {
        navController.navigateEditWithArgs(innerViewId, interviewGroupId, question)
    }

    private fun popBackStack() {
        navController.popBackStack()
    }

    fun popBackStackIfNotHome() {
        if (!isSameCurrentDestination<MainTabRoute.Home>()) {
            popBackStack()
        }
    }

    private inline fun <reified T : Route> isSameCurrentDestination(): Boolean {
        return navController.currentDestination?.hasRoute<T>() == true
    }

    @Composable
    fun shouldShowBottomBar() = MainTab.containWithoutEdit { route ->
        currentDestination?.hasRoute(route::class) == true
    }
}

@Composable
internal fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}
