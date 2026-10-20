package com.lattae.cuaca.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.lattae.cuaca.util.WeatherType

private fun createWeatherColorScheme(
    primary: androidx.compose.ui.graphics.Color,
    secondary: androidx.compose.ui.graphics.Color,
    secondaryVariant: androidx.compose.ui.graphics.Color,
    surface: androidx.compose.ui.graphics.Color
) = lightColorScheme(
    onPrimaryFixedVariant = primary,
    secondary = secondary,
    onSecondaryFixedVariant = secondaryVariant,
    surface = surface
)

private val ClearColorPalette = createWeatherColorScheme(
    Clear50,
    Clear10,
    Clear30,
    Clear70)
private val CloudsColorPalette = createWeatherColorScheme(
    Clouds50,
    Clouds10,
    Clouds30,
    Clouds70
)
private val RainColorPalette = createWeatherColorScheme(
    Rain50,
    Rain10,
    Rain30,
    Rain70
)
private val DrizzleColorPalette = createWeatherColorScheme(
    Drizzle50,
    Drizzle10,
    Drizzle30,
    Drizzle70
)
private val ThunderstormColorPalette = createWeatherColorScheme(
    Thunderstorm50,
    Thunderstorm10,
    Thunderstorm30,
    Thunderstorm70
)
private val SnowColorPalette = createWeatherColorScheme(
    Snow50,
    Snow10,
    Snow30,
    Snow70
)
private val AtmosphereColorPalette = createWeatherColorScheme(
    Atmosphere50,
    Atmosphere10,
    Atmosphere30,
    Atmosphere70
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