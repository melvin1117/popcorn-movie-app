package com.smfelix.movieapp.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.smfelix.movieapp.R

@Composable
fun RatingBar(
    rating: MutableState<Float>
) {
    Slider(
        value = rating.value,
        onValueChange = {},
        valueRange = 0f..10f,
        enabled = true,
        colors = SliderDefaults.colors(
            thumbColor = colorResource(id = R.color.accent),
            activeTrackColor = colorResource(id = R.color.accent),
            inactiveTrackColor = colorResource(id = R.color.lightGray)
        ),
        modifier = Modifier.height(6.dp)
    )
}