package com.lattae.theweather.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.lattae.theweather.util.WeatherType

private val ClearColorPalette = lightColorScheme(
    onPrimaryFixedVariant = Clear50,
    secondary = Clear10,
    onSecondaryFixedVariant = Clear30,
    surface = Clear70
)

private val CloudsColorPalette = lightColorScheme(
    onPrimaryFixedVariant = Clouds50,
    secondary = Clouds10,
    onSecondaryFixedVariant = Clouds30,
    surface = Clouds70
)

private val RainColorPalette = lightColorScheme(
    onPrimaryFixedVariant = Rain50,
    secondary = Rain10,
    onSecondaryFixedVariant = Rain30,
    surface = Rain70
)

private val DrizzleColorPalette = lightColorScheme(
    onPrimaryFixedVariant = Drizzle50,
    secondary = Drizzle10,
    onSecondaryFixedVariant = Drizzle30,
    surface = Drizzle70
)

private val ThunderstormColorPalette = lightColorScheme(
    onPrimaryFixedVariant = Thunderstorm50,
    secondary = Thunderstorm10,
    onSecondaryFixedVariant = Thunderstorm30,
    surface = Thunderstorm70
)

private val SnowColorPalette = lightColorScheme(
    onPrimaryFixedVariant = Snow50,
    secondary = Snow10,
    onSecondaryFixedVariant = Snow30,
    surface = Snow70
)

private val AtmosphereColorPalette = lightColorScheme(
    onPrimaryFixedVariant = Atmosphere50,
    secondary = Atmosphere10,
    onSecondaryFixedVariant = Atmosphere30,
    surface = Atmosphere70
)

@Composable
fun TheWeatherTheme(
    mode: WeatherType, content: @Composable () -> Unit
) {
    val colorScheme = when (mode) {
        WeatherType.CLEAR -> ClearColorPalette
        WeatherType.CLOUDS -> CloudsColorPalette
        WeatherType.RAIN -> RainColorPalette
        WeatherType.DRIZZLE -> DrizzleColorPalette
        WeatherType.THUNDERSTORM -> ThunderstormColorPalette
        WeatherType.SNOW -> SnowColorPalette
        WeatherType.ATMOSPHERE -> AtmosphereColorPalette
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}