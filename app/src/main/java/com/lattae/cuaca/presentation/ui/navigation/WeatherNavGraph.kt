package com.lattae.cuaca.presentation.ui.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Brush
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lattae.cuaca.common.Resource
import com.lattae.cuaca.domain.model.Location
import com.lattae.cuaca.presentation.ui.detail.DetailDestination
import com.lattae.cuaca.presentation.ui.detail.DetailScreen
import com.lattae.cuaca.presentation.ui.detail.viewmodel.DetailViewModel
import com.lattae.cuaca.presentation.ui.search.SearchDestination
import com.lattae.cuaca.presentation.ui.search.SearchScreen
import com.lattae.cuaca.presentation.ui.search.viewmodel.SearchViewModel

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

            val currentLocation = when (val weather = detailState.currentLocationWeather) {
                is Resource.Success -> weather.data
                else -> Location()
            }
            SearchScreen(
                searchState = searchState,
                onEvent = searchViewModel::onEvent,
                currentLocation = currentLocation,
                theme = theme,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { lat, lon, isCurrentLoc ->
                    detailViewModel.loadDetailWeather(lat, lon, isCurrentLoc)
                    navController.popBackStack()
                }
            )
        }
    }
}