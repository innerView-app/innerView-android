package com.dev.innerview.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data class InnerViewDetail(val id: Int) : Route

    @Serializable
    data object InnerViewQuestion : Route

    @Serializable
    data object InterviewGroup : Route

    @Serializable
    data object Player : Route

    @Serializable
    data class Records(val id: Int) : Route

    @Serializable
    data object Filming : Route

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
    data object Edit : MainTabRoute

    @Serializable
    data object Profile : MainTabRoute
}