package com.smfelix.movieapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MovieViewModelFactory(
    private val category: String,
    private val existingViewModel: MovieViewModel? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            return (existingViewModel ?: MovieViewModel(category)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
