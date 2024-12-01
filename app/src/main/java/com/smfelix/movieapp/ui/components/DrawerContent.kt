package com.smfelix.movieapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.smfelix.movieapp.R
import com.smfelix.movieapp.data.User

@Composable
fun DrawerContent(
    user: User,
    onProfileClick: () -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.bgGray))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // User Info Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(
                    color = colorResource(id = R.color.bgGrayOverlay),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp)
                .clickable { onProfileClick() }
        ) {
            // Profile Picture or Initials
            if (user.profilePhotoUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(user.profilePhotoUrl),
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                val initials = if (user.name.isNullOrEmpty()) "🍿" else user.name
                    .split(" ")
                    .mapNotNull { it.firstOrNull()?.toString() }
                    .joinToString("")
                    .uppercase()
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    colorResource(id = R.color.bgStar),
                                    colorResource(id = R.color.star)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.headlineLarge,
                        color = colorResource(id = R.color.black),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name
            Text(
                text = if (user.name.isNullOrEmpty()) "Guest" else user.name,
                style = MaterialTheme.typography.headlineMedium,
                color = colorResource(id = R.color.black)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Short Bio
            Text(
                text = if (user.shortBio.isNullOrEmpty()) "No bio available" else user.shortBio,
                style = MaterialTheme.typography.bodyLarge,
                color = colorResource(id = R.color.white),
                modifier = Modifier.padding(horizontal = 8.dp),
                maxLines = 2
            )
        }

        // Logout Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onSignOut() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp, // Built-in Material Icon for logout
                contentDescription = "Logout Icon",
                tint = colorResource(id = R.color.accent),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Logout",
                style = MaterialTheme.typography.bodyLarge,
                color = colorResource(id = R.color.accent)
            )
        }
    }
}
