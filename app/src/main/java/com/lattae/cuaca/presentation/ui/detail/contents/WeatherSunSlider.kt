package com.lattae.cuaca.presentation.ui.detail.contents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lattae.cuaca.R
import com.lattae.cuaca.presentation.theme.WeatherTextSecondary


@Composable
fun WeatherSunSlider(
    sunrise: String,
    sunset: String,
    sliderProgress: Float,
    isNightMode: Boolean,
    modifier: Modifier = Modifier
) {
    val startIcon = if (isNightMode) R.drawable.ic_sunset else R.drawable.ic_sunset
    val startDesc = if (isNightMode) stringResource(R.string.sunset) else stringResource(R.string.sunrise)
    val startTitle = if (isNightMode) sunset else sunrise

    val endIcon = if (isNightMode) R.drawable.ic_sunrise else R.drawable.ic_sunset
    val endDesc = if (isNightMode) stringResource(R.string.sunrise) else stringResource(R.string.sunset)
    val endTitle = if (isNightMode) sunrise else sunset

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(0.2f))
            .padding(4.dp, 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SunIcon(
                icon = startIcon,
                desc = startDesc
            )
            SunIcon(
                icon = endIcon,
                desc = endDesc,
            )
        }
        SunSlider(
            progress = sliderProgress,
            isNightMode = isNightMode,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = startTitle,
                style = MaterialTheme.typography.titleMedium,
                color = WeatherTextSecondary
            )
            Text(
                text = endTitle,
                style = MaterialTheme.typography.titleMedium,
                color = WeatherTextSecondary
            )
        }
    }
}


@Composable
fun SunIcon(
    icon: Int,
    desc: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = desc,
            tint = WeatherTextSecondary,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = desc,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Light,
            color = WeatherTextSecondary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SunSlider(
    progress: Float,
    isNightMode: Boolean,
    modifier: Modifier = Modifier
) {
    Slider(
        value = progress,
        onValueChange = { },
        enabled = false,
        valueRange = 0f..1f,
        modifier = modifier.fillMaxWidth(),
        thumb = {
            Icon(
                imageVector = if (isNightMode) Icons.Rounded.NightsStay else Icons.Rounded.WbSunny,
                contentDescription = stringResource(R.string.ic_sun),
                tint = WeatherTextSecondary,
                modifier = Modifier.size(24.dp)
            )
        },
        track = { sliderState ->
            SliderDefaults.Track(
                sliderState = sliderState,
                modifier = Modifier.height(6.dp),
                colors = SliderDefaults.colors(
                    activeTrackColor = WeatherTextSecondary.copy(0.3f),
                    inactiveTrackColor = WeatherTextSecondary
                ),
                drawStopIndicator = null
            )
        }
    )
}