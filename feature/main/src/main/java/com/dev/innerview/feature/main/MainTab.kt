package com.dev.innerview.feature.main

import androidx.compose.runtime.Composable
import com.dev.innerview.core.navigation.MainTabRoute
import com.dev.innerview.core.navigation.Route

internal enum class MainTab(
    val iconResId: Int,
    internal val contentDescription: String,
    val route: MainTabRoute,
) {
    HOME(
        iconResId = R.drawable.ic_mic,
        contentDescription = "이너뷰",
        MainTabRoute.Home,
    ),
    PEEK(
        iconResId = R.drawable.ic_peek,
        contentDescription = "PEEK",
        MainTabRoute.Peek
    ),
    INTERVIEW_EDIT(
        iconResId = R.drawable.ic_edit,
        contentDescription = "편집",
        MainTabRoute.EditHome,
    ),
    PROFILE(
        iconResId = R.drawable.ic_profile,
        contentDescription = "프로필",
        MainTabRoute.Profile,
    );

    companion object {
        @Composable
        fun find(predicate: @Composable (MainTabRoute) -> Boolean): MainTab? {
            return entries.find { predicate(it.route) }
        }

        @Composable
        fun containWithoutEdit(predicate: @Composable (Route) -> Boolean): Boolean {
            return entries.map { it.route }.filter { it != MainTabRoute.EditHome }.any { predicate(it) }
        }
    }
}