package com.smfelix.movieapp.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smfelix.movieapp.data.MovieData
import com.smfelix.movieapp.service.TMDBApiService
import com.smfelix.movieapp.service.provideTMDBService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.system.measureTimeMillis

class MovieViewModel(private val category: String) : ViewModel() {

    private val apiService: TMDBApiService = provideTMDBService()
    val movieList = mutableStateOf(mutableStateListOf<MovieData>())
    val selectedMovies = mutableStateOf(mutableStateListOf<MovieData>())
    val isLoading = mutableStateOf(false)
    var selectedMovieId = mutableStateOf<Long?>(null)
    val favoriteMovies = mutableStateOf(mutableStateListOf<Long>()) // Holds IDs of favorite movies


    init {
        fetchMoviesIfNeeded()
    }

    fun fetchMoviesIfNeeded() {
        if (movieList.value.isNotEmpty()) return
        isLoading.value = true
        viewModelScope.launch {
            try {
                val timeTaken = measureTimeMillis {
                    val response = when (category) {
                        "popular" -> apiService.getPopularMovies()
                        "top_rated" -> apiService.getTopRatedMovies()
                        "now_playing" -> apiService.getNowPlayingMovies()
                        else -> throw IllegalArgumentException("Invalid category: $category")
                    }
                    movieList.value.clear()
                    movieList.value.addAll(response.results)
            }
                val delayTime = 1000 - timeTaken
                if (delayTime > 0) {
                    delay(delayTime)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteSelectedMovies() {
        val idsToRemove = selectedMovies.value.map { it.id }
        movieList.value.removeAll { movie -> idsToRemove.contains(movie.id) }
        selectedMovies.value.clear()

        // Ensure the selected ID is valid
        if (selectedMovieId.value != null && movieList.value.none { it.id == selectedMovieId.value }) {
            selectedMovieId.value = null
        }
    }

    fun duplicateMovie(id: Long) {
        val index = movieList.value.indexOfFirst { it.id == id }
        if (index != -1) {
            val originalMovie = movieList.value[index]
            val duplicatedMovie = originalMovie.copy(id = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE)
            movieList.value.add(index + 1, duplicatedMovie)
        }
    }

    fun toggleFavorite(movieId: Long) {
        if (favoriteMovies.value.contains(movieId)) {
            favoriteMovies.value.remove(movieId)
        } else {
            favoriteMovies.value.add(movieId)
        }
    }

    fun isFavorite(movieId: Long): Boolean {
        return favoriteMovies.value.contains(movieId)
    }
}
