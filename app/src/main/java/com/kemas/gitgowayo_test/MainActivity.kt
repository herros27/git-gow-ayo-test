package com.kemas.gitgowayo_test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.kemas.gitgowayo_test.presentation.navigation.TvShowNavGraph
import com.kemas.gitgowayo_test.ui.theme.GitgowayotestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GitgowayotestTheme {
               val navController = rememberNavController()
                TvShowNavGraph(navController)
            }
        }
    }
}