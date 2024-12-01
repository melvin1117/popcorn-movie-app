package com.smfelix.movieapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.smfelix.movieapp.R
import com.smfelix.movieapp.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    coroutineScope: CoroutineScope,
    drawerState: DrawerState // Pass DrawerState here
) {
    val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route
    val showBackButton = currentRoute?.startsWith("user_profile") == true || currentRoute?.startsWith("movie_details") == true

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
                Text("Popcorn", color = Color.White,
                    modifier = Modifier.clickable { navController.navigate("movie_list/popular") },)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(id = R.color.accent),
            titleContentColor = colorResource(id = R.color.white)
        ),
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(36.dp))
            }
        },
        actions = {
            IconButton(onClick = {
                coroutineScope.launch {
                    drawerState.open() // Open the drawer when user icon is clicked
                }
            }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "User Profile",
                    tint = Color.White
                )
            }
        }
    )
}
