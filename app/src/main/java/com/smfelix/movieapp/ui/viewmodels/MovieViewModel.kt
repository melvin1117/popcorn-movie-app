package com.smfelix.movieapp.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smfelix.movieapp.data.MovieData
import com.smfelix.movieapp.service.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.system.measureTimeMillis

class MovieViewModel : ViewModel() {
    var movieList = mutableStateOf(mutableStateListOf<MovieData>())
    val selectedMovies = mutableStateOf(mutableStateListOf<MovieData>())
    val isLoading = mutableStateOf(true)
    var selectedMovieId = mutableStateOf<Long?>(null)

    init {
        fetchMovies()
    }

    private fun fetchMovies() {
        viewModelScope.launch {
            try {
                val timeTaken = measureTimeMillis {
                    val response = RetrofitInstance.api.getPopularMovies()
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
}
