package com.smfelix.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.TopAppBar
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.IconButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smfelix.movieapp.ui.activities.MovieListScreen
import com.smfelix.movieapp.ui.activities.MovieViewPager
import com.smfelix.movieapp.ui.viewmodels.MovieViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MovieViewModel = viewModel()
            MovieAppNavGraph(viewModel)
        }
    }
}

@Composable
fun MovieAppNavGraph(viewModel: MovieViewModel) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            val currentBackStackEntry = navController.currentBackStackEntryAsState().value
            val showBackButton = currentBackStackEntry?.destination?.route?.startsWith("movie_details") == true

            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_round),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .padding(end = 8.dp)
                        )
                        Text("Popcorn", color = Color.White)
                    }
                },
                backgroundColor = colorResource(id = R.color.accent),
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = {
                            navController.navigateUp() // Go back to the previous screen
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(36.dp))
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "movie_list",
            modifier = Modifier
                .padding(paddingValues)
                .background(colorResource(id = R.color.bgGray))
        ) {
            composable("movie_list") {
                MovieListScreen(
                    onMovieClick = { selectedMovie ->
                        val selectedId = selectedMovie.id
                        navController.navigate("movie_details/$selectedId")
                    },
                    viewModel = viewModel
                )
            }
            composable("movie_details/{selectedId}") { backStackEntry ->
                val selectedId = backStackEntry.arguments?.getString("selectedId")?.toLongOrNull() ?: 0L
                MovieViewPager(selectedMovieId = selectedId, viewModel = viewModel)
            }
        }
    }
}
