package com.smfelix.movieapp.data

import com.smfelix.movieapp.utils.getGenreNames

data class MovieData(
    val id: Long,
    val title: String,
    val overview: String,
    val poster_path: String,
    val backdrop_path: String,
    val release_date: String,
    val original_language: String,
    val vote_average: Double,
    val vote_count: Int,
    val genre_ids: List<Int>
) {
    val genreNames: List<String>
        get() = getGenreNames(genre_ids)
}

data class MovieResponse(
    val results: List<MovieData>
)
