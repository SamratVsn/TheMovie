package com.example.themovieapp.ui.utils

import com.example.themovieapp.R
import com.example.themovieapp.model.BottomNavItem

object Constants {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Home",
            icon = R.drawable.home,
            route = "home"
        ),
        BottomNavItem(
            label = "search",
            icon = R.drawable.search,
            route = "search"
        ),
        BottomNavItem(
            label = "settings",
            icon = R.drawable.settings,
            route = "settings"
        ),
        BottomNavItem(
            label = "profile",
            icon = R.drawable.profile,
            route = "profile"
        )
    )
}