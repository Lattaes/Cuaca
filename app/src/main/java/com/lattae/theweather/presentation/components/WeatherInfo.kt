package com.lattae.theweather.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lattae.theweather.R
import com.lattae.theweather.presentation.theme.WeatherCardBg
import com.lattae.theweather.presentation.theme.WeatherTextSecondary

@Composable
fun WeatherInfo(
    leadingTitle: String,
    icon: String,
    tempMin: String,
    tempMax: String,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: TextStyle,
    isCurrentLocation: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isCurrentLocation) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = stringResource(R.string.ic_location),
                    tint = MaterialTheme.colorScheme.onPrimary.copy(0.45f),
                    modifier = Modifier
                        .size(18.dp)
                )
                Spacer(Modifier.size(4.dp))
            }
            Text(
                text = leadingTitle,
                color = WeatherTextSecondary,
                style = fontStyle,
                fontWeight = fontWeight,
                modifier = Modifier.weight(1f)
            )
        }
        WeatherIcon(
            icon = icon,
            size = 32.dp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${tempMin}° / ${tempMax}°",
            color = WeatherTextSecondary,
            style = fontStyle,
            fontWeight = fontWeight,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}