package com.lattae.theweather.presentation.ui.search

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices.PIXEL_7
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.lattae.theweather.R
import com.lattae.theweather.common.Resource
import com.lattae.theweather.domain.model.WeatherCurrent
import com.lattae.theweather.presentation.theme.WeatherTextSecondary
import com.lattae.theweather.presentation.ui.navigation.NavigationDestination
import com.lattae.theweather.presentation.ui.search.contents.CustomSearchBar
import com.lattae.theweather.presentation.ui.search.contents.SavedLocationItem
import com.lattae.theweather.presentation.ui.search.viewmodel.SearchEvent
import com.lattae.theweather.presentation.ui.search.viewmodel.SearchState


object SearchDestination : NavigationDestination {
    override val route = "search"
    override val titleRes = R.string.weather_search_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchState: SearchState,
    onEvent: (SearchEvent) -> Unit,
    currentLocation: WeatherCurrent,
    theme: Brush,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Double, Double, Boolean) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    val searchResult = remember(searchState.searchLocation) {
        if (searchState.searchLocation is Resource.Success) {
            searchState.searchLocation.data
        } else {
            emptyList()
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (!isSearchActive) {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.onPrimaryFixedVariant,
                        titleContentColor = WeatherTextSecondary
                    ),
                    title = {
                        Text(
                            text = stringResource(R.string.manage_locations),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.ic_back),
                            tint = WeatherTextSecondary,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clickable(
                                    enabled = true,
                                    onClick = onNavigateBack
                                )
                        )
                     },
                    scrollBehavior = scrollBehavior
                )
            }
        }
    ) { innerPadding ->
        val topPadding = (innerPadding.calculateTopPadding() - 28.dp).coerceAtLeast(0.dp)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding)
                .background(theme)
        ) {
            when (val savedLocations = searchState.savedLocations) {
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
                    CustomSearchBar(
                        query = searchState.searchQuery,
                        onQueryChange = {newQuery ->
                            onEvent(SearchEvent.OnQueryChange(newQuery))
                        },
                        onSearch = {},
                        searchResult = searchResult,
                        expanded = isSearchActive,
                        onExpandedChange = { isSearchActive = it },
                        expandedColor = theme,
                        onItemClick = { location ->
                            onEvent(SearchEvent.OnLocationClick(location))
                            isSearchActive = false
                            onNavigateToDetail(location.lat, location.lon, false)
                        }
                    )

                    if (!isSearchActive) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item (
                                key = "current_location_${currentLocation.locationName}"
                            ){
                                SavedLocationItem(
                                    location = currentLocation,
                                    isCurrentLocation = true,
                                    onItemClick = { location ->
                                        onNavigateToDetail(location.lat, location.lon, true)
                                    },
                                    onDeleteLocation = {}
                                )
                            }

                            items(
                                items = savedLocations.data,
                                key = { pair ->
                                    pair.first.locationName ?: "${pair.first.lat}${pair.first.lon}"
                                }
                            ) { pair ->
                                val domainLocation = pair.first
                                val weather = pair.second
                                SavedLocationItem(
                                    location = weather,
                                    isCurrentLocation = false,
                                    onItemClick = { location ->
                                        onNavigateToDetail(location.lat, location.lon, false)
                                    },
                                    onDeleteLocation = {
                                        onEvent(SearchEvent.OnDeleteLocation(domainLocation.lat, domainLocation.lon))
                                    }
                                )
                            }
                        }
                    }
                }
                is Resource.Failure -> {
                    Toast.makeText(
                        LocalContext.current,
                        savedLocations.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}


//@Preview (showBackground = true, showSystemUi = true, device = PIXEL_7)
//@Composable
//fun SearchScreenPreview() {
//    SearchScreen(
//        searchState = SearchState(),
//        onEvent = {},
//        onNavigateBack = {},
//        currentLocation = WeatherCurrent(
//            locationName = "Fuzhou",
//            icon = "",
//            tempMin = 25,
//            tempMax = 28
//        ),
//        theme = Brush.verticalGradient()
//    )
//}