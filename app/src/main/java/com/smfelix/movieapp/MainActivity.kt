package com.smfelix.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smfelix.movieapp.ui.activities.MovieListScreen
import com.smfelix.movieapp.ui.activities.MovieViewPager
import com.smfelix.movieapp.ui.components.BottomNavBar
import com.smfelix.movieapp.ui.components.TopBar
import com.smfelix.movieapp.ui.viewmodels.MovieViewModel
import com.smfelix.movieapp.ui.viewmodels.MovieViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieApp()
        }
    }

    @Composable
    fun MovieApp() {
        val navController = rememberNavController()
        val currentRoute by navController.currentBackStackEntryAsState()

        // Determine if TopBar and BottomNavBar should be displayed
        val showBars = currentRoute?.destination?.route?.startsWith("movie_list") == true ||
                currentRoute?.destination?.route?.startsWith("movie_details") == true

        Scaffold(
            topBar = {
                if (showBars) {
                    TopBar(navController)
                }
            },
            bottomBar = {
                if (showBars) {
                    BottomNavBar(navController)
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "movie_list/popular",
                modifier = Modifier
                    .padding(paddingValues)
                    .background(colorResource(id = R.color.bgGray))
            ) {
                composable("movie_list/{category}") { backStackEntry ->
                    val category = backStackEntry.arguments?.getString("category") ?: "popular"
                    val viewModel = provideViewModel(category)

                    // Pass the shared ViewModel to the MovieListScreen
                    MovieListScreen(viewModel = viewModel) { movie ->
                        navController.navigate("movie_details/${category}/${movie.id}")
                    }
                }
                composable("movie_details/{category}/{selectedMovieId}") { backStackEntry ->
                    val category = backStackEntry.arguments?.getString("category") ?: "popular"
                    val movieId = backStackEntry.arguments?.getString("selectedMovieId")?.toLongOrNull() ?: 0L
                    val viewModel = provideViewModel(category)

                    // Pass the shared ViewModel to the MovieViewPager
                    MovieViewPager(selectedMovieId = movieId, viewModel = viewModel)
                }
            }
        }
    }

    private fun provideViewModel(category: String): MovieViewModel {
        val app = applicationContext as MyApplication
        val viewModelStoreOwner = app.getViewModelStoreOwner(category)
        val factory = MovieViewModelFactory(category)
        return ViewModelProvider(viewModelStoreOwner, factory)[MovieViewModel::class.java]
    }
}
