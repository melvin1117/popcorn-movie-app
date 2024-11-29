package com.smfelix.movieapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp

@Composable
fun StarRatingBar(
    rating: MutableState<Float>,
    modifier: Modifier = Modifier,
    starCount: Int = 5,
    starSize: Float = 24f,
    activeColor: Color = Color.Yellow,
    inactiveColor: Color = Color.Gray,
    isEditable: Boolean = false
) {
    Row(modifier = modifier) {
        for (i in 0 until starCount) {
            Box(
                modifier = Modifier
                    .size(starSize.dp)
                    .clickable(enabled = isEditable) {
                        if (isEditable) {
                            rating.value = (i + 1).toFloat()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = "Empty Star",
                    tint = inactiveColor,
                    modifier = Modifier.size(starSize.dp)
                )

                val filledPortion = (rating.value - i).coerceIn(0f, 1f)
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Filled Star",
                    tint = activeColor,
                    modifier = Modifier
                        .size(starSize.dp)
                        .drawWithContent {
                            clipRect(right = size.width * filledPortion) {
                                this@drawWithContent.drawContent()
                            }
                        }
                )
            }
        }
    }
}
