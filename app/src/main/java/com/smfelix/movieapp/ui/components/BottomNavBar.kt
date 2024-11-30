package com.smfelix.movieapp.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.smfelix.movieapp.R

@Composable
fun BottomNavBar(navController: NavController) {
    // Define items for the navigation bar
    val items = listOf(
        Triple("popular", "Popular", Icons.Filled.Movie),
        Triple("top_rated", "Top Rated", Icons.Filled.Star),
        Triple("now_playing", "Now Playing", Icons.Filled.PlayArrow)
    )

    // Get the current route and arguments
    val currentBackStackEntry = navController.currentBackStackEntryAsState()?.value
    val currentCategory = currentBackStackEntry?.arguments?.getString("category")

    // Render the bottom navigation bar
    NavigationBar(containerColor = colorResource(id = R.color.accent)) {
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                selected = currentCategory == route,
                onClick = {
                    if (currentCategory != route) {
                        navController.navigate("movie_list/$route") {
                            popUpTo("movie_list") { inclusive = false }
                        }
                    }
                },
                icon = { Icon(imageVector = icon, contentDescription = null) },
                label = { Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                colors = NavigationBarItemColors (
                    selectedIconColor = colorResource(id = R.color.star),
                    unselectedIconColor = colorResource(id = R.color.white),
                    selectedTextColor = colorResource(id = R.color.star),
                    unselectedTextColor = colorResource(id = R.color.white),
                    disabledIconColor = colorResource(id = R.color.lightGray),
                    disabledTextColor = colorResource(id = R.color.lightGray),
                    selectedIndicatorColor = colorResource(id = R.color.bgStar)
                ),
            )
        }
    }
}
