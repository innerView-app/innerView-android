package com.dev.innerview.feature.home.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.dev.innerview.core.navigation.DEEP_LINK_BASE_PATH
import com.dev.innerview.core.navigation.MainTabRoute
import com.dev.innerview.core.navigation.Route
import com.dev.innerview.feature.home.HomeRoute
import com.dev.innerview.feature.home.InnerViewDetailScreen
import com.dev.innerview.feature.home.InnerViewQuestionScreen
import com.dev.innerview.feature.home.InterviewGroupScreen

fun NavController.navigateHome(navOptions: NavOptions) {
    navigate(MainTabRoute.Home, navOptions)
}

fun NavController.navigateInnerViewDetail(id: String) {
    navigate(Route.InnerViewDetail(id))
}

fun NavController.navigateInterviewGroup() {
    navigate(Route.InterviewGroup)
}

fun NavController.navigateInnerViewQuestion(id: String) {
    navigate(Route.InnerViewQuestion(id))
}

fun NavGraphBuilder.homeNavGraph(
    padding: PaddingValues,
    onBackClick: () -> Unit,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    onShowToast: (text: String) -> Unit,
    navigateToInnerViewDetail: (String) -> Unit,
    navigateToInnerViewQuestion: (String) -> Unit,
    navigateToInterviewGroup: () -> Unit,
    navigateToRecord: (String, Int, String) -> Unit
) {
    composable<MainTabRoute.Home> {
        HomeRoute(
            padding = padding,
            onShowErrorSnackBar = onShowErrorSnackBar,
            navigateToInnerViewDetail = navigateToInnerViewDetail
        )
    }

    composable<Route.InnerViewDetail>(
        deepLinks = listOf(
            navDeepLink<Route.InnerViewDetail>(basePath = DEEP_LINK_BASE_PATH)
        )
    ) { navBackStackEntry ->
        val (id) = navBackStackEntry.toRoute<Route.InnerViewDetail>()
        InnerViewDetailScreen(
            innerViewId = id,
            onBackClick = onBackClick,
            onShowToast = onShowToast,
            navigateToInterviewGroup = navigateToInterviewGroup,
            navigateToInnerViewQuestion = navigateToInnerViewQuestion,
            navigateToRecord = navigateToRecord
        )
    }

    composable<Route.InterviewGroup> {
        InterviewGroupScreen(
            onBackClick = onBackClick
        )
    }

    composable<Route.InnerViewQuestion> { navBackStackEntry ->
        val (id) = navBackStackEntry.toRoute<Route.InnerViewQuestion>()
        InnerViewQuestionScreen(
            innerViewId = id,
            onBackClick = onBackClick
        )
    }
}
