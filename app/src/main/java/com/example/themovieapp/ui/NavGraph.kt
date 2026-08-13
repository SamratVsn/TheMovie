package com.example.themovieapp.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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

    val showBottombar = currentRoute in bottomDestinations.map { it.route }

    Scaffold(
        bottomBar ={
            if(showBottombar) {
                NavigationBar {
                    bottomDestinations.forEach { dest ->
                        NavigationBarItem(
                            selected = currentRoute == dest.route,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(dest.icon),
                                    contentDescription = dest.label
                                )
                            },
                            label = { Text(dest.label) },
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ){
            composable(Routes.HOME){
                HomeScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(Routes.detail(movieId))
                    }
                )
            }

            composable(
                route = Routes.DETAIL,
                arguments = listOf(
                    navArgument("movieId") { type = NavType.IntType }
                )
            ){
                DetailScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.SEARCH){
                SearchScreen(
                    onMovieClick ={ movieId ->
                        navController.navigate(Routes.detail(movieId))
                    }
                )
            }

            composable(Routes.SETTINGS){
                SettingsScreen()
            }

            composable(Routes.PROFILE) {
                ProfileScreen()
            }
        }
    }
}