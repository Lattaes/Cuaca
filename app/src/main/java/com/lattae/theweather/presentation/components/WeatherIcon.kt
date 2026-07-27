package com.lattae.theweather.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import com.lattae.theweather.R

@Composable
fun WeatherIcon(
    icon: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = "https://openweathermap.org/img/wn/${icon}@2x.png",
        contentDescription = stringResource(R.string.ic_weather),
        contentScale = ContentScale.Fit,
        modifier = modifier.size(size)
    )
}