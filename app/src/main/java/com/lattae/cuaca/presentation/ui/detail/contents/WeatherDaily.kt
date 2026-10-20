package com.lattae.cuaca.presentation.ui.detail.contents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lattae.cuaca.domain.model.WeatherDaily
import com.lattae.cuaca.presentation.components.WeatherInfo
import com.lattae.cuaca.presentation.theme.WeatherDivider


@Composable
fun WeatherDailyContent(
    weatherDaily: List<WeatherDaily>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(0.2f))
            .padding(12.dp, 8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        weatherDaily.forEachIndexed { index, day ->
            WeatherInfo(
                leadingTitle = "${day.datetime}",
                icon = "${day.icon}",
                tempMin = "${day.tempMin}",
                tempMax = "${day.tempMax}",
                fontStyle = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                isCurrentLocation = false,
                modifier = Modifier
                    .padding(vertical = 2.dp)
            )
            if (index < weatherDaily.size -1) {
                HorizontalDivider(
                    color = WeatherDivider,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}
