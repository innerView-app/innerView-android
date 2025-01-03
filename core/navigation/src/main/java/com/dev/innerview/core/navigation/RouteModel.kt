package com.dev.innerview.core.navigation

import android.net.Uri
import kotlinx.serialization.Serializable

const val DEEP_LINK_BASE_PATH = "iv://innerview.com"

fun getDeepLinkOf(vararg path: String): Uri {
    return Uri.parse(listOf(DEEP_LINK_BASE_PATH, *path).joinToString("/"))
}

sealed interface Route {
    @Serializable
    data class InnerViewDetail(val id: String) : Route

    @Serializable
    data class InnerViewQuestion(val id: String) : Route

    @Serializable
    data class InterviewGroup(val innerViewId: String, val interviewGroupId: Int) : Route

    @Serializable
    data object Player : Route

    @Serializable
    data class Records(val innerViewId: String, val interviewGroupId: Int) : Route

    @Serializable
    data class Filming(val innerViewId: String, val interviewGroupId: Int, val question: String) :
        Route

    @Serializable
    data class Edit(val innerProjectId: Int) : Route

    @Serializable
    data object ProjectSelect : Route

    @Serializable
    data object InterviewSelect : Route

    @Serializable
    data object Setting : Route
}

sealed interface MainTabRoute : Route {
    @Serializable
    data object Home : MainTabRoute

    @Serializable
    data object Peek : MainTabRoute

    @Serializable
    data object EditHome : MainTabRoute

    @Serializable
    data object Profile : MainTabRoute
}