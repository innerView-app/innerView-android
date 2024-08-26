package com.dev.innerview.feature.home.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
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

fun NavController.navigateInnerViewQuestion() {
    navigate(Route.InnerViewQuestion)
}

fun NavGraphBuilder.homeNavGraph(
    padding: PaddingValues,
    onBackClick: () -> Unit,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToInnerViewDetail: (String) -> Unit,
    navigateToInnerViewQuestion: () -> Unit,
    navigateToInterviewGroup: () -> Unit,
    navigateToRecord: (String) -> Unit
) {
    composable<MainTabRoute.Home> {
        HomeRoute(
            padding = padding,
            onShowErrorSnackBar = onShowErrorSnackBar,
            navigateToInnerViewDetail = navigateToInnerViewDetail
        )
    }

    composable<Route.InnerViewDetail> { navBackStackEntry ->
        val id = navBackStackEntry.toRoute<Route.InnerViewDetail>().id
        InnerViewDetailScreen(
            id = id,
            onBackClick = onBackClick,
            navigateToInterviewGroup = navigateToInterviewGroup,
            navigateToInnerViewQuestion = navigateToInnerViewQuestion,
            navigateToRecord = { navigateToRecord(it) }
        )
    }

    composable<Route.InterviewGroup> {
        InterviewGroupScreen(
            onBackClick = onBackClick
        )
    }

    composable<Route.InnerViewQuestion> {
        InnerViewQuestionScreen(
            onBackClick = onBackClick
        )
    }
}
