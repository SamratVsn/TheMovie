package com.example.themovieapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.themovieapp.ui.screen.MovieViewModel
import com.example.themovieapp.ui.utils.Constants

enum class MovieScreen(val title: String){
    Home(title = "home"),
    Search(title = "search"),
    Profile(title = "profile"),
    Details(title = "details"),
    Settings(title = "settings"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheMoviesApp(
    viewModel: MovieViewModel = viewModel(factory = MovieViewModel.Factory),
    navController: NavHostController = rememberNavController()
){
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = MovieScreen.valueOf(
        backStackEntry?.destination?.route ?: MovieScreen.Home.name
    )

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = { MovieBottomAppBar(
            navController = navController,
            currentScreen = currentScreen,
        ) }
    ){ innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MovieScreen.Home.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
            composable(route = MovieScreen.Home.name){

            }
        }
    }
}

@Composable
fun MovieBottomAppBar(
    navController: NavHostController,
    currentScreen: MovieScreen,
){
    NavigationBar(
        containerColor = Color(0xFF0F9D58)
    ) {
        Constants.BottomNavItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentScreen == navItem.route,
                onClick = {
                    navController.navigate(navItem.route)
                },
                icon = {
                    Icon(
                        painter = painterResource(navItem.icon),
                        contentDescription = navItem.label
                    )
                },
                label = {
                    Text(text = navItem.label)
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Blue,
                    unselectedIconColor = Color.White,
                    selectedTextColor = Color.Blue,
                    indicatorColor = Color(0xFF195334)
                )
            )
        }
    }
}
