package com.dev.innerview.feature.main.component

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.dev.innerview.feature.edit.navigation.editNavGraph
import com.dev.innerview.feature.home.navigation.homeNavGraph
import com.dev.innerview.feature.main.MainNavigator
import com.dev.innerview.feature.peek.navigation.peekNavGraph
import com.dev.innerview.feature.peek.navigation.profileNavGraph
import com.dev.innerview.feature.record.navigation.recordNavGraph

@Composable
internal fun MainNavHost(
    navigator: MainNavigator,
    padding: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    NavHost(
        navController = navigator.navController,
        startDestination = navigator.startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        homeNavGraph(
            padding = padding,
            onShowErrorSnackBar = onShowErrorSnackBar,
            onBackClick = { navigator.popBackStackIfNotHome() },
            navigateToInnerViewDetail = { id -> navigator.navigateInnerViewDetail(id) },
            navigateToInnerViewQuestion = { id -> navigator.navigateInnerViewQuestion(id) },
            navigateToInterviewGroup = { navigator.navigateInterviewGroup() },
            navigateToRecord = { innerViewId, interviewGroupId ->
                navigator.navigateRecord(innerViewId, interviewGroupId)
            }
        )
        peekNavGraph(
            padding = padding,
            onShowErrorSnackBar = onShowErrorSnackBar
        )
        editNavGraph(
            onShowErrorSnackBar = onShowErrorSnackBar,
            onBackClick = { navigator.popBackStackIfNotHome() }
        )
        profileNavGraph(
            padding = padding,
            onShowErrorSnackBar = onShowErrorSnackBar
        )
        recordNavGraph(
            padding = padding,
            onBackClick = { navigator.popBackStackIfNotHome() },
            onShowErrorSnackBar = onShowErrorSnackBar,
            navigateToFilming = { innerViewId, interviewGroupId, question ->
                navigator.navigateFilming(
                    innerViewId,
                    interviewGroupId,
                    question
                )
            },
            navigateToEdit = { innerViewId, interviewGroupId, question ->
                navigator.navigateEditWithArgs(innerViewId, interviewGroupId, question)
            }
        )
    }
}