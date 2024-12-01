package com.smfelix.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smfelix.movieapp.data.MovieData
import com.smfelix.movieapp.data.User
import com.smfelix.movieapp.service.AuthService
import com.smfelix.movieapp.service.UserService
import com.smfelix.movieapp.service.provideTMDBService
import com.smfelix.movieapp.ui.components.BottomNavBar
import com.smfelix.movieapp.ui.components.DrawerContent
import com.smfelix.movieapp.ui.components.TopBar
import com.smfelix.movieapp.ui.screens.*
import com.smfelix.movieapp.ui.viewmodels.MovieViewModel
import com.smfelix.movieapp.ui.viewmodels.MovieViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val authService = remember { AuthService() }
        val userService = remember { UserService(authService) }
        val coroutineScope = rememberCoroutineScope()

        var user by remember { mutableStateOf<User?>(null) }

        // Fetch user data when the app starts
        LaunchedEffect(authService.getCurrentUser()) {
            authService.getCurrentUser()?.let { currentUser ->
                userService.fetchUser { fetchedUser ->
                    user = fetchedUser ?: User(
                        userId = currentUser.uid,
                        name = currentUser.email?.substringBefore("@") ?: "Guest",
                        email = currentUser.email ?: "",
                        profilePhotoUrl = null,
                        dateOfBirth = "",
                        gender = "",
                        mobileNumber = "",
                        shortBio = "",
                        favorites = emptyList<Long>()
                    )
                }
            }
        }

        // Re-fetch user data when drawer is opened
        LaunchedEffect(drawerState.isOpen) {
            if (drawerState.isOpen) {
                userService.fetchUser { updatedUser ->
                    if (updatedUser != null) user = updatedUser
                }
            }
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            scrimColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.32f),
            drawerContent = {
                user?.let { currentUser ->
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(LocalConfiguration.current.screenWidthDp.dp * 0.7f)
                    ) {
                        DrawerContent(
                            user = currentUser,
                            onProfileClick = {
                                coroutineScope.launch { drawerState.close() }
                                navController.navigate("user_profile")
                            },
                            onSignOut = {
                                authService.logoutUser {
                                    coroutineScope.launch { drawerState.close() }
                                    user = null
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    if (navController.currentBackStackEntryAsState()?.value?.destination?.route != "login" &&
                        navController.currentBackStackEntryAsState()?.value?.destination?.route != "signup"
                    ) {
                        TopBar(
                            navController = navController,
                            coroutineScope = coroutineScope,
                            drawerState = drawerState
                        )
                    }
                },
                bottomBar = {
                    if (navController.currentBackStackEntryAsState()?.value?.destination?.route?.startsWith("movie_list") == true ||
                        navController.currentBackStackEntryAsState()?.value?.destination?.route?.startsWith("movie_details") == true ||
                        navController.currentBackStackEntryAsState()?.value?.destination?.route?.startsWith("favorites") == true
                    ) {
                        BottomNavBar(navController)
                    }
                }
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = when {
                        authService.getCurrentUser() == null -> "login"
                        user?.name.isNullOrEmpty() -> "user_profile"
                        else -> "movie_list/popular"
                    },
                    modifier = Modifier
                        .padding(paddingValues)
                        .background(colorResource(id = R.color.bgGray))
                ) {
                    composable("login") {
                        LoginScreen(navController, authService) { loggedInUser ->
                            user = loggedInUser
                        }
                    }
                    composable("signup") {
                        SignupScreen(navController, authService) { newUser ->
                            user = newUser
                            navController.navigate("user_profile")
                        }
                    }
                    composable("user_profile") {
                        user?.let { currentUser ->
                            UserProfileScreen(navController, userService, currentUser) { updatedUser ->
                                user = updatedUser
                            }
                        }
                    }
                    composable("movie_list/{category}") { backStackEntry ->
                        val category = backStackEntry.arguments?.getString("category") ?: "popular"
                        val viewModel = provideViewModel(category)

                        MovieListScreen(
                            viewModel = viewModel,
                            favoriteMovieIds = (user?.favorites ?: emptyList()).toSet(),
                            onMovieClick = { movie -> navController.navigate("movie_details/${category}/${movie.id}") },
                            onFavoriteToggle = { movieId ->
                                val updatedFavorites = user?.favorites.orEmpty().toMutableList().apply {
                                    if (contains(movieId)) remove(movieId) else add(movieId)
                                }
                                userService.updateUserFavorites(user!!.userId, updatedFavorites) {
                                    user = user?.copy(favorites = updatedFavorites)
                                }
                            }
                        )
                    }
                    composable("movie_details/{category}/{selectedMovieId}") { backStackEntry ->
                        val category = backStackEntry.arguments?.getString("category") ?: "popular"
                        val movieId = backStackEntry.arguments?.getString("selectedMovieId")?.toLongOrNull() ?: 0L
                        val viewModel = provideViewModel(category)

                        MovieViewPager(
                            selectedMovieId = movieId,
                            viewModel = viewModel,
                            favorites = user?.favorites ?: emptyList(),
                            onFavoriteToggle = { movieId ->
                                val updatedFavorites = user?.favorites.orEmpty().toMutableList().apply {
                                    if (contains(movieId)) remove(movieId) else add(movieId)
                                }
                                userService.updateUserFavorites(user!!.userId, updatedFavorites) {
                                    user = user?.copy(favorites = updatedFavorites)
                                }
                            }
                        )
                    }
                    composable("favorites") {
                        FavoritesScreen(
                            favorites = user?.favorites ?: emptyList(),
                            fetchMovieDetails = { movieId, callback ->
                                fetchMovieDetails(movieId, callback)
                            },
                            onFavoriteToggle = { movieId ->
                                val updatedFavorites = user?.favorites.orEmpty().toMutableList().apply {
                                    if (contains(movieId)) remove(movieId) else add(movieId)
                                }
                                userService.updateUserFavorites(user!!.userId, updatedFavorites) {
                                    user = user?.copy(favorites = updatedFavorites)
                                }
                            }
                        )
                    }
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

    private fun fetchMovieDetails(movieId: Long, callback: (MovieData?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiService = provideTMDBService()
                val movie = apiService.getMovieDetails(movieId)
                callback(movie)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }
    }

}
