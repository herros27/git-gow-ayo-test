package com.kemas.gitgowayo_test.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kemas.gitgowayo_test.presentation.detail.TvShowDetailScreen
import com.kemas.gitgowayo_test.presentation.list.TvShowListScreen

sealed class Screen(val route: String){
    data object List : Screen("tv_show_list")
    data object Detail : Screen("tv_show_detail/{showId}"){
        fun createRoute(showId: Int) = "tv_show_detail/$showId"
    }
}

@Composable
fun TvShowNavGraph(
    navController: NavHostController
){
    NavHost(
        navController = navController,
        startDestination = Screen.List.route
    ){
        composable(route = Screen.List.route){
            TvShowListScreen(
                onShowClick = {showId ->
                    navController.navigate(Screen.Detail.createRoute(showId))
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("showId"){
                    type = NavType.IntType
                }
            )
        ){
            TvShowDetailScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
    }
}