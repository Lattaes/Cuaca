package com.lattae.theweather.presentation.ui.detail.contents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices.PIXEL_7
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lattae.theweather.domain.model.WeatherDaily
import com.lattae.theweather.presentation.components.WeatherInfo
import com.lattae.theweather.presentation.theme.WeatherDivider
import com.lattae.theweather.util.toDayName
import java.time.LocalDate


@Composable
fun WeatherDailyContent(
    weatherDaily: List<WeatherDaily>,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor.copy(0.2f))
            .padding(12.dp, 8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        weatherDaily.forEachIndexed { index, day ->
            WeatherInfo(
                leadingTitle = day.datetime.toDayName(),
                icon = "${day.icon}",
                tempMin = "${day.tempMin}",
                tempMax = "${day.tempMax}",
                fontStyle = MaterialTheme.typography.bodyMedium,
                isCurrentLocation = false,
                modifier = Modifier.padding(vertical = 2.dp)
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


@Preview(device = PIXEL_7, showSystemUi = true)
@Composable
fun WeatherDailyComponentPreview() {
    val weatherDaily = listOf(
        WeatherDaily(datetime = LocalDate.now(), icon = "", tempMin = 25, tempMax = 28),
        WeatherDaily(datetime = LocalDate.now().plusDays(1), icon = "", tempMin = 24, tempMax = 27),
        WeatherDaily(datetime = LocalDate.now().plusDays(2), icon = "", tempMin = 26, tempMax = 29),
        WeatherDaily(datetime = LocalDate.now().plusDays(3), icon = "", tempMin = 26, tempMax = 29),
        WeatherDaily(datetime = LocalDate.now().plusDays(4), icon = "", tempMin = 26, tempMax = 29)
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .statusBarsPadding()
    ) {
        WeatherDailyContent(
            weatherDaily = weatherDaily,
            backgroundColor = Color.Gray,
        )
    }
}
