package com.example.themovieapp.model

import androidx.annotation.DrawableRes
import com.example.themovieapp.ui.MovieScreen

data class BottomNavItem(
    val label: String,
    @DrawableRes val icon: Int,
    val route: MovieScreen,
)
