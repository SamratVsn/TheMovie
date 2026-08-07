package com.example.themovieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.themovieapp.ui.TheMoviesApp
import com.example.themovieapp.ui.theme.TheMovieAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheMovieAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TheMoviesApp()
                }
            }
        }
    }
}
