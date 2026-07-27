package com.lattae.theweather.presentation.navigation

import androidx.compose.material3.MaterialTheme
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
import com.lattae.theweather.common.Resource
import com.lattae.theweather.domain.model.WeatherCurrent
import com.lattae.theweather.presentation.theme.Clouds50
import com.lattae.theweather.presentation.theme.Clouds70
import com.lattae.theweather.presentation.ui.detail.DetailScreen
import com.lattae.theweather.presentation.ui.detail.viewmodel.DetailViewModel
import com.lattae.theweather.presentation.ui.search.SearchScreen
import com.lattae.theweather.presentation.ui.search.viewmodel.SearchViewModel

sealed class Screen(val route: String) {
    object Detail : Screen("detail")
    object Search : Screen("search")
}

@Composable
fun WeatherNavGraph(
    windowSizeClass: WindowWidthSizeClass,
    themeColor: Brush,
    detailViewModel: DetailViewModel,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Detail.route
    ) {
        composable(Screen.Detail.route) {
            DetailScreen(
                windowSizeClass = windowSizeClass,
                onNavigateToSearchScreen = {
                    navController.navigate(Screen.Search.route)
                },
                themeColor = themeColor,
                elementColor = MaterialTheme.colorScheme.surface,
                viewModel = detailViewModel
            )
        }
        composable(Screen.Search.route) {
            val searchViewModel: SearchViewModel = hiltViewModel()
            val searchState by searchViewModel.uiState.collectAsStateWithLifecycle()
            val detailState by detailViewModel.uiState.collectAsStateWithLifecycle()

            val currentLocation = when (val s = detailState.state) {
                is Resource.Success -> s.data.weatherCurrent ?: WeatherCurrent()
                else -> WeatherCurrent()
            }

            SearchScreen(
                state = searchState,
                currentLocation = currentLocation,
                savedLocation = emptyList(),
                appBarColor = Clouds50,
                theme = Clouds50,
                searchContainerColor = Clouds70,
                onBackClick = {
                    navController.popBackStack()
                },
                onDeleteLocation = {}
            )
        }
    }
}
