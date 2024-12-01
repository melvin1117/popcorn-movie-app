package com.smfelix.movieapp.data

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val profilePhotoUrl: String? = null,
    val dateOfBirth: String? = "",
    val gender: String? = "",
    val mobileNumber: String? = "",
    val shortBio: String? = "",
    val favorites: List<Long> = emptyList()
)
