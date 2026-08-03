package com.lattae.theweather.presentation.ui.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lattae.theweather.common.Resource
import com.lattae.theweather.domain.model.WeatherCurrent
import com.lattae.theweather.presentation.theme.Clouds50
import com.lattae.theweather.presentation.ui.detail.DetailDestination
import com.lattae.theweather.presentation.ui.detail.DetailScreen
import com.lattae.theweather.presentation.ui.detail.viewmodel.DetailViewModel
import com.lattae.theweather.presentation.ui.search.SearchDestination
import com.lattae.theweather.presentation.ui.search.SearchScreen
import com.lattae.theweather.presentation.ui.search.viewmodel.SearchViewModel

@Composable
fun WeatherNavGraph(
    windowSizeClass: WindowWidthSizeClass,
    navController: NavHostController = rememberNavController(),
    theme: Brush,
    detailViewModel: DetailViewModel
) {

    NavHost(
        navController = navController,
        startDestination = DetailDestination.route
    ) {
        composable(route = DetailDestination.route) {
            DetailScreen(
                windowSizeClass = windowSizeClass,
                onNavigateToSearchScreen = {
                    navController.navigate(SearchDestination.route)
                },
                themeColor = theme,
                viewModel = detailViewModel
            )
        }
        composable(route = SearchDestination.route) {
            val searchViewModel: SearchViewModel = hiltViewModel()
            val searchState by searchViewModel.uiState.collectAsStateWithLifecycle()
            val detailState by detailViewModel.uiState.collectAsStateWithLifecycle()

            val currentLocation = when (val weather = detailState.detailState) {
                is Resource.Success -> weather.data.weatherCurrent ?: WeatherCurrent()
                else -> WeatherCurrent()
            }
            SearchScreen(
                searchState = searchState,
                onEvent = searchViewModel::onEvent,
                currentLocation = currentLocation,
                theme = theme,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { lat, lon ->
                    detailViewModel.loadDetailWeather(lat, lon)
                    navController.popBackStack()
                }
            )
        }
    }
}