package com.lattae.cuaca.presentation.ui.detail.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WindPower
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lattae.cuaca.R
import com.lattae.cuaca.presentation.theme.WeatherTextSecondary


private data class ConditionUiItem(
    val icon: ImageVector,
    val label: String,
    val value: String,
    val unit: String
)

@Composable
fun WeatherCondition(
    rainVolume: Double,
    windSpeed: Double,
    visibility: Int,
    clouds: Int,
    humidity: Int,
    airPressure: Int,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        ConditionUiItem(
            Icons.Filled.WaterDrop, stringResource(R.string.rain),
            "$rainVolume", "mm/h"
        ),
        ConditionUiItem(
            Icons.Filled.WindPower, stringResource(R.string.win_speed),
            "$windSpeed", "m/s"
        ),
        ConditionUiItem(
            Icons.Filled.Visibility, stringResource(R.string.visibility),
            "$visibility", "km"
        ),
        ConditionUiItem(
            Icons.Filled.Cloud, stringResource(R.string.clouds),
            "$clouds", "%"
        ),
        ConditionUiItem(
            Icons.Filled.Percent, stringResource(R.string.humidity),
            "$humidity", "%"
        ),
        ConditionUiItem(
            Icons.Filled.Air, stringResource(R.string.air_pressure),
            "$airPressure", "hPa"
        )
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    CardWeatherCondition(
                        icon = item.icon,
                        label = item.label,
                        value = item.value,
                        unit = item.unit,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun CardWeatherCondition(
    icon: ImageVector,
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(0.2f),
            contentColor = WeatherTextSecondary
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp, 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = WeatherTextSecondary
            )
            Text(
                text = "$value $unit",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = WeatherTextSecondary
            )
        }
    }
}