package com.dev.innerview.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object InnerViewDetail : Route

    @Serializable
    data object InnerViewQuestion : Route

    @Serializable
    data object InterViewGroup : Route

    @Serializable
    data object Player : Route

    @Serializable
    data object Record : Route

    @Serializable
    data object Camera : Route

    @Serializable
    data object InterViewSelect : Route

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