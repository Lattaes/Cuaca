package com.lattae.theweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lattae.theweather.common.Resource
import com.lattae.theweather.presentation.theme.TheWeatherTheme
import com.lattae.theweather.presentation.ui.detail.viewmodel.DetailViewModel
import com.lattae.theweather.presentation.ui.navigation.WeatherNavGraph
import com.lattae.theweather.util.WeatherType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val viewModel: DetailViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val weatherTheme = when (val state = uiState.state) {
                is Resource.Success -> {
                    val weather = state.data.weatherCurrent?.weatherType.toString()
                    WeatherType.from(weather)
                }
                else -> {
                    WeatherType.CLEAR
                }
            }

            TheWeatherTheme(weatherTheme) {
                val containerSize = LocalWindowInfo.current.containerSize
                val screenHeight = containerSize.height.toFloat()

                val gradientBackground = Brush.verticalGradient(
                    0.0f to MaterialTheme.colorScheme.onPrimaryFixedVariant,
                    0.7f to MaterialTheme.colorScheme.onSecondaryFixedVariant,
                    1.0f to MaterialTheme.colorScheme.secondary,
                    startY = 0.0f,
                    endY = screenHeight
                )

                val layoutDirection = LocalLayoutDirection.current

                Surface(
                    modifier = Modifier
                        .padding(
                            start = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateStartPadding(layoutDirection),
                            end = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateEndPadding(layoutDirection)
                        )
                ) {
                    val windowSize = calculateWindowSizeClass(this)

                    WeatherNavGraph(
                        windowSizeClass = windowSize.widthSizeClass,
                        theme = gradientBackground,
                        detailViewModel = viewModel
                    )
                }
            }
        }
    }
}
