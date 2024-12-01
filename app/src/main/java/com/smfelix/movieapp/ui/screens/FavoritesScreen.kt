package com.smfelix.movieapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.smfelix.movieapp.data.MovieData
import com.smfelix.movieapp.service.TMDBApiService
import com.smfelix.movieapp.ui.components.MovieCard
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    favorites: List<Long>,
    fetchMovieDetails: (Long, (MovieData?) -> Unit) -> Unit,
    onFavoriteToggle: (Long) -> Unit
) {
    val favoriteMovies = remember { mutableStateListOf<MovieData>() }

    // Fetch movie details for all favorite IDs
    LaunchedEffect(favorites) {
        favoriteMovies.clear()
        favorites.forEach { movieId ->
            fetchMovieDetails(movieId) { movie ->
                if (movie != null) {
                    favoriteMovies.add(movie)
                }
            }
        }
    }

    if (favoriteMovies.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No favorite movies added", color = Color.White)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(favoriteMovies, key = { it.id }) { movie ->
                MovieCard(
                    movie = movie,
                    isChecked = false,
                    onCheckedChange = { },
                    onMovieClick = { },
                    onLongPress = { },
                    modifier = Modifier.fillMaxWidth(),
                    showCheckbox = false,
                    isFavorite = favorites.contains(movie.id),
                    onFavoriteToggle = { onFavoriteToggle(movie.id) }
                )
            }
        }
    }
}
