package com.example.pdmnoticias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pdmnoticias.presentation.screen.NewsScreen
import com.example.pdmnoticias.presentation.viewmodel.NewsViewModel
import com.example.pdmnoticias.data.api.RetrofitInstance
import com.example.pdmnoticias.data.repository.NewsRepositoryImpl
import com.example.pdmnoticias.domain.usecase.GetTopStoriesUseCase
import com.example.pdmnoticias.presentation.screen.NewsDetailsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitInstance.api
        val repository = NewsRepositoryImpl(apiService)
        val getTopStoriesUseCase = GetTopStoriesUseCase(repository)
        val viewModel = NewsViewModel(getTopStoriesUseCase)

        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "news") {
                composable("news") {
                    NewsScreen(viewModel, navController)
                }
                composable(
                    "details/{title}/{abstract}/{url}",
                    arguments = listOf(
                        navArgument("title") { type = NavType.StringType },
                        navArgument("abstract") { type = NavType.StringType },
                        navArgument("url") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val title = backStackEntry.arguments?.getString("title") ?: ""
                    val abstract = backStackEntry.arguments?.getString("abstract") ?: ""
                    val url = backStackEntry.arguments?.getString("url") ?: ""
                    NewsDetailsScreen(title, abstract, url)
                }
            }
        }
    }
}
