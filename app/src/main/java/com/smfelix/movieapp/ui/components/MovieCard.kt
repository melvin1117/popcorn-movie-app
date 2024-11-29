package com.smfelix.movieapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.smfelix.movieapp.R
import com.smfelix.movieapp.data.MovieData
import java.util.Locale

@Composable
fun MovieCard(
    movie: MovieData,
    isChecked: Boolean,
    onCheckedChange: () -> Unit,
    onMovieClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glowBorder = if (movie.vote_average >= 8.0) colorResource(id = R.color.star) else Color.Transparent

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onMovieClick() },
                    onLongPress = { onLongPress() }
                )
            },
        colors = CardColors(
            containerColor = colorResource(id = R.color.bgDarkGray),
            contentColor = Color.White,
            disabledContentColor = Color.Unspecified,
            disabledContainerColor = Color.Unspecified,
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, glowBorder)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w185${movie.poster_path}"),
                contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .width(75.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Movie Details
            Column(modifier = Modifier.weight(1f)) {
                // Movie Title
                Text(
                    text = movie.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = if (movie.vote_average >= 8.0) {
                        TextStyle(
                            color = Color.White,
                            shadow = Shadow(
                                color = colorResource(id = R.color.star),
                                blurRadius = 10f
                            )
                        )
                    } else {
                        TextStyle(color = Color.White)
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = movie.overview,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Star",
                        tint = colorResource(id = R.color.star),
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = String.format(Locale.getDefault(), "%.1f/10", movie.vote_average),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "|",
                        color = Color.Gray,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "${movie.vote_count} votes",
                        color = Color.Gray,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Checkbox(
                checked = isChecked,
                onCheckedChange = { onCheckedChange() },
                colors = CheckboxDefaults.colors(
                    checkedColor = colorResource(id = R.color.accent),
                    uncheckedColor = colorResource(id = R.color.lightGray),
                    checkmarkColor = Color.White
                )
            )
        }
    }
}
