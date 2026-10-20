package com.lattae.cuaca.presentation.ui.detail

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lattae.cuaca.R
import com.lattae.cuaca.common.Resource
import com.lattae.cuaca.domain.model.AirCondition
import com.lattae.cuaca.domain.model.WeatherCurrent
import com.lattae.cuaca.domain.model.WeatherDaily
import com.lattae.cuaca.domain.model.WeatherHourly
import com.lattae.cuaca.presentation.ui.detail.contents.WeatherCondition
import com.lattae.cuaca.presentation.ui.detail.contents.WeatherCurrentContent
import com.lattae.cuaca.presentation.ui.detail.contents.WeatherDailyContent
import com.lattae.cuaca.presentation.ui.detail.contents.WeatherHeader
import com.lattae.cuaca.presentation.ui.detail.contents.WeatherHourlyContent
import com.lattae.cuaca.presentation.ui.detail.contents.WeatherSunSlider
import com.lattae.cuaca.presentation.ui.detail.viewmodel.DetailEvent
import com.lattae.cuaca.presentation.ui.detail.viewmodel.DetailViewModel
import com.lattae.cuaca.presentation.ui.navigation.NavigationDestination

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
    val context = LocalContext.current

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

    LaunchedEffect(uiState.offlineErrorMessage) {
        uiState.offlineErrorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                )
            }
            is Resource.Success -> {

                val weatherData = weatherState.data
                val current = weatherData.weatherCurrent ?: WeatherCurrent()
                val hourly = weatherData.weatherHourly.orEmpty()
                val daily = weatherData.weatherDaily.orEmpty()
                val airCondition = weatherData.airCondition ?: AirCondition()

                val showDefaultHeader = uiState.isCurrentLocation || uiState.isSavedLocation
                val detailEvent = if (showDefaultHeader) {
                    DetailEvent.HeaderDefault(
                        onMenuClick = onNavigateToSearchScreen
                    )
                } else {
                    DetailEvent.HeaderSearchResult(
                        onBackClick = onNavigateToSearchScreen,
                        onAddClick = { viewModel.saveLocation() }
                    )
                }

                when (windowSizeClass) {
                    WindowWidthSizeClass.Compact -> {
                        WeatherDetailPortrait(
                            weatherCurrent = current,
                            weatherHourly = hourly,
                            weatherDaily = daily,
                            airCondition = airCondition,
                            detailEvent = detailEvent,
                            isCurrentLocation = uiState.isCurrentLocation,
                        )
                    }
                    WindowWidthSizeClass.Medium -> {
                        WeatherDetailPortrait(
                            weatherCurrent = current,
                            weatherHourly = hourly,
                            weatherDaily = daily,
                            airCondition = airCondition,
                            detailEvent = detailEvent,
                            isCurrentLocation = uiState.isCurrentLocation,
                        )
                    }

                    WindowWidthSizeClass.Expanded -> {
                        WeatherDetailLandscape(
                            weatherCurrent = current,
                            weatherHourly = hourly,
                            weatherDaily = daily,
                            airCondition = airCondition,
                            detailEvent = detailEvent,
                            isCurrentLocation = uiState.isCurrentLocation,
                        )
                    }
                    else -> {
                        WeatherDetailPortrait(
                            weatherCurrent = current,
                            weatherHourly = hourly,
                            weatherDaily = daily,
                            airCondition = airCondition,
                            detailEvent = detailEvent,
                            isCurrentLocation = uiState.isCurrentLocation,
                        )
                    }
                }
            }
            is Resource.Failure -> {
                LaunchedEffect(weatherState.errorMessage) {
                    Toast.makeText(
                        context,
                        weatherState.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        AnimatedVisibility(
            visible = uiState.isRefreshing && uiState.detailState is Resource.Success,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp),
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it }
        ) {
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = MaterialTheme.colorScheme.surface.copy(0.5f),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.update_data),
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherDetailPortrait(
    weatherCurrent: WeatherCurrent,
    weatherHourly: List<WeatherHourly>,
    weatherDaily: List<WeatherDaily>,
    airCondition: AirCondition,
    detailEvent: DetailEvent,
    isCurrentLocation: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 16.dp)
    ) {
        WeatherHeader(
            location = weatherCurrent.locationName ?: "",
            isCurrentLocation = isCurrentLocation,
            detailEvent = detailEvent
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                WeatherCurrentContent(
                    datetime = weatherCurrent.fullDate ?: "",
                    temp = weatherCurrent.temp,
                    weatherType = weatherCurrent.weatherType ?: "",
                    tempMin = weatherCurrent.tempMin,
                    tempMax = weatherCurrent.tempMax,
                    feelsLike = weatherCurrent.feelsLike,
                    icon = weatherCurrent.icon ?: "",
                    airCondition = airCondition
                )
            }
            item { WeatherHourlyContent(weatherHourly = weatherHourly) }
            item { WeatherDailyContent(weatherDaily = weatherDaily) }
            item {
                WeatherCondition(
                    rainVolume = weatherCurrent.rainVolume,
                    windSpeed = weatherCurrent.windSpeed,
                    visibility = weatherCurrent.visibility,
                    clouds = weatherCurrent.clouds,
                    humidity = weatherCurrent.humidity,
                    airPressure = weatherCurrent.airPressure
                )
            }
            item {
                WeatherSunSlider(
                    sunrise = weatherCurrent.sunrise ?: "",
                    sunset = weatherCurrent.sunset ?: "",
                    sliderProgress = weatherCurrent.sunProgress,
                    isNightMode = weatherCurrent.isNightMode
                )
            }
        }
    }
}

@Composable
fun WeatherDetailLandscape(
    weatherCurrent: WeatherCurrent,
    weatherHourly: List<WeatherHourly>,
    weatherDaily: List<WeatherDaily>,
    airCondition: AirCondition,
    detailEvent: DetailEvent,
    isCurrentLocation: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 1000.dp)
                .padding(top = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                WeatherHeader(
                    location = weatherCurrent.locationName ?: "",
                    isCurrentLocation = isCurrentLocation,
                    detailEvent = detailEvent,
                )
            }
            item {
                WeatherCurrentContent(
                    datetime = weatherCurrent.fullDate ?: "",
                    temp = weatherCurrent.temp,
                    weatherType = weatherCurrent.weatherType ?: "",
                    tempMin = weatherCurrent.tempMin,
                    tempMax = weatherCurrent.tempMax,
                    feelsLike = weatherCurrent.feelsLike,
                    icon = weatherCurrent.icon ?: "",
                    airCondition = airCondition
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        WeatherHourlyContent(weatherHourly = weatherHourly)
                        WeatherDailyContent(
                            weatherDaily = weatherDaily
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        WeatherCondition(
                            rainVolume = weatherCurrent.rainVolume,
                            windSpeed = weatherCurrent.windSpeed,
                            visibility = weatherCurrent.visibility,
                            clouds = weatherCurrent.clouds,
                            humidity = weatherCurrent.humidity,
                            airPressure = weatherCurrent.airPressure
                        )
                        WeatherSunSlider(
                            sunrise = weatherCurrent.sunrise ?: "",
                            sunset = weatherCurrent.sunset ?: "",
                            sliderProgress = weatherCurrent.sunProgress,
                            isNightMode = weatherCurrent.isNightMode
                        )
                    }
                }
            }
        }
    }
}