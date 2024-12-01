package com.smfelix.movieapp.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.smfelix.movieapp.R
import com.smfelix.movieapp.data.MovieData
import com.smfelix.movieapp.ui.components.MovieCard
import com.smfelix.movieapp.ui.viewmodels.MovieViewModel
import kotlinx.coroutines.launch

@Composable
fun MovieListScreen(
    viewModel: MovieViewModel,
    onMovieClick: (MovieData) -> Unit,
    onFavoriteToggle: (Long) -> Unit,
    favoriteMovieIds: Set<Long>
) {
    val movieList = viewModel.movieList.value
    val selectedMovies = viewModel.selectedMovies.value
    val isLoading by viewModel.isLoading
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val fadeInSpec = spring(
        stiffness = Spring.StiffnessMedium,
        visibilityThreshold = 0.01f
    )
    val fadeOutSpec = spring(
        stiffness = Spring.StiffnessMedium,
        visibilityThreshold = 0.01f
    )
    val placementSpec = spring(
        stiffness = Spring.StiffnessMedium,
        visibilityThreshold = IntOffset.VisibilityThreshold
    )
    if (isLoading) {
        // Show loading indicator
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.accent)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading Movies...",
                    color = Color.White
                )
            }
        }
    } else {
        Column {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        selectedMovies.clear()
                        selectedMovies.addAll(movieList)
                    },
                    colors = ButtonDefaults.buttonColors(
                        colorResource(id = R.color.accent),
                        contentColor = Color.White
                    )
                ) {
                    Text("Select All")
                }
                Button(
                    onClick = {
                        selectedMovies.clear()
                    },
                    colors = ButtonDefaults.buttonColors(
                        colorResource(id = R.color.accent),
                        contentColor = Color.White
                    )
                ) {
                    Text("Clear All")
                }
                Button(
                    onClick = {
                        if (selectedMovies.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Oops! You need to select some movies first before deleting.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            coroutineScope.launch {
                                viewModel.deleteSelectedMovies()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        colorResource(id = R.color.accent),
                        contentColor = Color.White
                    )
                ) {
                    Text("Delete")
                }
            }

            val state = rememberLazyListState()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp, start = 16.dp, end = 16.dp, bottom = 0.dp),
                state = state
            ) {
                itemsIndexed(items = movieList, key = { _, item -> item.id }) { _, movie ->
                    val isChecked = selectedMovies.contains(movie)
                    val isFavorite = favoriteMovieIds.contains(movie.id)

                    MovieCard(
                        movie = movie,
                        isChecked = isChecked,
                        showCheckbox = true,
                        onCheckedChange = {
                            if (isChecked) {
                                selectedMovies.remove(movie)
                            } else {
                                selectedMovies.add(movie)
                            }
                        },
                        onMovieClick = {
                            viewModel.selectedMovieId.value = movie.id
                            onMovieClick(movie)
                        },
                        onLongPress = {
                            coroutineScope.launch {
                                viewModel.duplicateMovie(movie.id)
                            }
                        },
                        modifier = Modifier
                            .animateItem(
                                fadeInSpec = fadeInSpec,
                                placementSpec = placementSpec,
                                fadeOutSpec = fadeOutSpec
                            ),
                        onFavoriteToggle = {
                            onFavoriteToggle(movie.id)
                        },
                        isFavorite = isFavorite
                    )
                }
            }

            LaunchedEffect(Unit) {
                Snapshot.withoutReadObservation {
                    state.requestScrollToItem(
                        index = state.firstVisibleItemIndex,
                        scrollOffset = state.firstVisibleItemScrollOffset
                    )
                }
            }
        }
    }
}
