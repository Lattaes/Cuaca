package com.lattae.theweather.presentation.ui.detail

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lattae.theweather.R
import com.lattae.theweather.common.Resource
import com.lattae.theweather.domain.model.AirPollution
import com.lattae.theweather.domain.model.Weather
import com.lattae.theweather.domain.model.WeatherCurrent
import com.lattae.theweather.presentation.theme.WeatherTextSecondary
import com.lattae.theweather.presentation.ui.detail.contents.WeatherCondition
import com.lattae.theweather.presentation.ui.detail.contents.WeatherCurrentContent
import com.lattae.theweather.presentation.ui.detail.contents.WeatherDailyContent
import com.lattae.theweather.presentation.ui.detail.contents.WeatherHeader
import com.lattae.theweather.presentation.ui.detail.viewmodel.DetailEvent
import com.lattae.theweather.presentation.ui.detail.viewmodel.DetailViewModel
import com.lattae.theweather.presentation.ui.navigation.NavigationDestination

object DetailDestination : NavigationDestination {
    override val route = "detail"
    override val titleRes = R.string.weather_detail_title
}

@Composable
fun DetailScreen(
    windowSizeClass: WindowWidthSizeClass,
    onNavigateToSearchScreen: () -> Unit,
    themeColor: Brush,
    viewModel: DetailViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) viewModel.onPermissionGranted()
    }

    LaunchedEffect(Unit) {
        if (!uiState.hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeColor),
        contentAlignment = Alignment.Center
    ) {
        when (val weatherState = uiState.detailState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
                }
            }
            is Resource.Success -> {
                val weatherData = weatherState.data
                val showDefaultHeader = uiState.isCurrentLocation || uiState.isSavedLocation
                val detailEvent = if (showDefaultHeader) {
                    DetailEvent.HeaderDefault(
                        onMenuClick = onNavigateToSearchScreen
                    )
                } else {
                    DetailEvent.HeaderSearchResult(
                        onBackClick = { viewModel.loadDetailWeather() },
                        onAddClick = { viewModel.saveLocation() }
                    )
                }

                when (windowSizeClass) {
                    WindowWidthSizeClass.Compact -> {
                        WeatherDetailPortrait(
                            weather = weatherData,
                            detailEvent = detailEvent,
                        )
                    }
                    WindowWidthSizeClass.Medium -> {
                        WeatherDetailPortrait(
                            weather = weatherData,
                            detailEvent = detailEvent,
                        )
                    }

                    WindowWidthSizeClass.Expanded -> {
                        WeatherDetailLandscape(
                            weather = weatherData,
                            detailEvent = detailEvent,
                        )
                    }
                    else -> {
                        WeatherDetailPortrait(
                            weather = weatherData,
                            detailEvent = detailEvent
                        )
                    }
                }
            }
            is Resource.Failure -> {
                val context = LocalContext.current
                LaunchedEffect(weatherState.errorMessage) {
                    Toast.makeText(
                        context,
                        weatherState.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

@Composable
fun WeatherDetailPortrait(
    weather: Weather,
    detailEvent: DetailEvent,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WeatherHeader(
            location = weather.weatherCurrent?.locationName ?: "",
            detailEvent = detailEvent
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                WeatherCurrentContent(
                    weatherCurrent = weather.weatherCurrent ?: WeatherCurrent(),
                    airPollution = weather.airPollution ?: AirPollution()
                )
            }
            item {
                WeatherDailyContent(
                    weatherDaily = weather.weatherDaily ?: emptyList(),
                )
            }
            item {
                WeatherCondition(
                    weatherCurrent = weather.weatherCurrent ?: WeatherCurrent()
                )
            }
        }
    }
}

@Composable
fun WeatherDetailLandscape(
    weather: Weather,
    detailEvent: DetailEvent,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .widthIn(max = 1000.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeatherHeader(
                location = weather.weatherCurrent?.locationName ?: "",
                detailEvent = detailEvent,
            )
            WeatherCurrentContent(
                weatherCurrent = weather.weatherCurrent ?: WeatherCurrent(),
                airPollution = weather.airPollution ?: AirPollution()
            )
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .height(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WeatherDailyContent(
                    weatherDaily = weather.weatherDaily ?: emptyList(),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                WeatherCondition(
                    weatherCurrent = weather.weatherCurrent ?: WeatherCurrent(),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}