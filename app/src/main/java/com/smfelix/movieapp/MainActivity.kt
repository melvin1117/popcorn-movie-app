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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smfelix.movieapp.ui.screens.MovieListScreen
import com.smfelix.movieapp.ui.screens.MovieViewPager
import com.smfelix.movieapp.ui.components.BottomNavBar
import com.smfelix.movieapp.ui.components.TopBar
import com.smfelix.movieapp.ui.screens.LoginScreen
import com.smfelix.movieapp.ui.screens.SignupScreen
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

        Scaffold(
            topBar = {
                if (currentRoute?.destination?.route != "login" &&
                    currentRoute?.destination?.route != "signup") {
                    TopBar(navController)
                }
            },
            bottomBar = {
                if (currentRoute?.destination?.route != "login" &&
                    currentRoute?.destination?.route != "signup") {
                    BottomNavBar(navController)
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "login", // Start with login screen
                modifier = Modifier
                    .padding(paddingValues)
                    .background(colorResource(id = R.color.bgGray))
            ) {
                composable("login") {
                    LoginScreen(navController)
                }
                composable("signup") {
                    SignupScreen(navController)
                }
                composable("movie_list/{category}") { backStackEntry ->
                    val category = backStackEntry.arguments?.getString("category") ?: "popular"
                    val viewModel = provideViewModel(category)

                    MovieListScreen(viewModel = viewModel) { movie ->
                        navController.navigate("movie_details/${category}/${movie.id}")
                    }
                }
                composable("movie_details/{category}/{selectedMovieId}") { backStackEntry ->
                    val category = backStackEntry.arguments?.getString("category") ?: "popular"
                    val movieId = backStackEntry.arguments?.getString("selectedMovieId")?.toLongOrNull() ?: 0L
                    val viewModel = provideViewModel(category)

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
