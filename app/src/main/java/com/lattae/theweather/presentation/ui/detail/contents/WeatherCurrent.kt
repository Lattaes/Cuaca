package com.lattae.theweather.presentation.ui.detail.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lattae.theweather.R
import com.lattae.theweather.domain.model.AirPollution
import com.lattae.theweather.domain.model.WeatherCurrent
import com.lattae.theweather.presentation.components.WeatherIcon
import com.lattae.theweather.presentation.theme.WeatherTextSecondary
import com.lattae.theweather.util.toFullDate

@Composable
fun WeatherCurrentContent(
    weatherCurrent: WeatherCurrent,
    airPollution: AirPollution,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "${weatherCurrent.datetime}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = WeatherTextSecondary
            )
            Text(
                text = "${weatherCurrent.temp}°",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Medium,
                color = WeatherTextSecondary
            )
            Text(
                text = "${weatherCurrent.weatherType}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = WeatherTextSecondary
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = stringResource(R.string.feels_like, weatherCurrent.tempMin, weatherCurrent.tempMax, weatherCurrent.feelsLike),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                color = WeatherTextSecondary
            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = stringResource(R.string.air_quality, airPollution.aqiLevel.score, airPollution.aqiDesc.toString()),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                color = WeatherTextSecondary
            )
        }
        WeatherIcon(
            icon = "${weatherCurrent.icon}",
            size = 130.dp,
            modifier = Modifier
        )
    }
}