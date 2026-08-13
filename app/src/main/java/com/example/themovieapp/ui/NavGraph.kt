package com.example.themovieapp.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.themovieapp.R
import com.example.themovieapp.ui.detail.DetailScreen
import com.example.themovieapp.ui.home.HomeScreen
import com.example.themovieapp.ui.profile.ProfileScreen
import com.example.themovieapp.ui.search.SearchScreen
import com.example.themovieapp.ui.settings.SettingsScreen
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

//routes throughout the app
object Routes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val SETTINGS = "settings"
    const val PROFILE = "profile"
    const val DETAIL = "detail/{movieId}"
    //details of specific movie
    fun detail(movieId: Int) = "detail/$movieId"
}

//destination model for bottom nav bar
private data class BottomDest(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int,
)

//destinations to navigate through
private val bottomDestinations = listOf(
    BottomDest(Routes.HOME, "Home", R.drawable.home),
    BottomDest(Routes.SEARCH, "Search", R.drawable.search),
    BottomDest(Routes.SETTINGS, "Settings", R.drawable.settings),
    BottomDest(Routes.PROFILE, "Profile", R.drawable.profile)
)

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.HOME

    val showBottomBar = bottomDestinations.any { it.route == currentRoute }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                FloatingBottomBar(
                    destinations = bottomDestinations,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onMovieClick = { movieId -> navController.navigate(Routes.detail(movieId)) }
                )
            }
            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) {
                DetailScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.SEARCH) {
                SearchScreen(
                    onMovieClick = { movieId -> navController.navigate(Routes.detail(movieId)) }
                )
            }
            composable(Routes.SETTINGS) { SettingsScreen() }
            composable(Routes.PROFILE) { ProfileScreen() }
        }
    }
}

@Composable
private fun FloatingBottomBar(
    destinations: List<BottomDest>,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .navigationBarsPadding()
            .height(64.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            destinations.forEach { dest ->
                val selected = currentRoute == dest.route
                val indicatorColor by animateColorAsState(
                    targetValue = if (selected)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        Color.Transparent,
                    label = "navIndicator"
                )
                val contentColor = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onNavigate(dest.route) }
                        .padding(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(indicatorColor)
                            .padding(horizontal = 18.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            painter = painterResource(dest.icon),
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = dest.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor
                    )
                }
            }
        }
    }
}