package com.lattae.cuaca.presentation.ui.detail.contents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lattae.cuaca.domain.model.WeatherHourly
import com.lattae.cuaca.presentation.components.WeatherIcon
import com.lattae.cuaca.presentation.theme.WeatherTextSecondary


@Composable
fun WeatherHourlyContent(
    modifier: Modifier = Modifier,
    weatherHourly: List<WeatherHourly>
) {
    LazyRow (
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(0.2f)),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        items(
            items = weatherHourly
        ) { hourly ->
            WeatherHourlyItem(
                time = "${hourly.datetime}",
                icon = "${hourly.icon}",
                temp = "${hourly.temp}"
            )
        }
    }
}

@Composable
fun WeatherHourlyItem(
    time: String,
    icon: String,
    temp: String,
) {
    Column(
        modifier = Modifier
            .padding(12.dp, 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.labelMedium,
            color = WeatherTextSecondary
        )
        WeatherIcon(
            icon = icon,
            size = 36.dp
        )
        Text(
            text = "$temp°",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = WeatherTextSecondary
        )
    }
}