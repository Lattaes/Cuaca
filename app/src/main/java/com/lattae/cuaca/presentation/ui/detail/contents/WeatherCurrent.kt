package com.lattae.cuaca.presentation.ui.detail.contents

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
import com.lattae.cuaca.R
import com.lattae.cuaca.domain.model.AirCondition
import com.lattae.cuaca.presentation.components.WeatherIcon
import com.lattae.cuaca.presentation.theme.WeatherTextSecondary

@Composable
fun WeatherCurrentContent(
    datetime: String,
    temp: Int,
    weatherType: String,
    tempMin: Int,
    tempMax: Int,
    feelsLike: Int,
    icon: String,
    airCondition: AirCondition,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = datetime,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = WeatherTextSecondary
            )
            Text(
                text = "$temp°",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Medium,
                color = WeatherTextSecondary
            )
            Text(
                text = weatherType,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = WeatherTextSecondary
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = stringResource(R.string.feels_like, tempMin, tempMax, feelsLike),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                color = WeatherTextSecondary
            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = stringResource(R.string.air_quality, airCondition.aqiLevel.score, airCondition.aqiDesc.toString()),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                color = WeatherTextSecondary
            )
        }
        WeatherIcon(
            icon = icon,
            size = 130.dp,
            modifier = Modifier
        )
    }
}