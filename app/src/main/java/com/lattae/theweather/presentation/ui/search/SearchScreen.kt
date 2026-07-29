package com.lattae.theweather.presentation.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
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
    state: SearchState,
    onEvent: (SearchEvent) -> Unit,
    currentLocation: WeatherCurrent,
    savedLocation: List<WeatherCurrent>,
    theme: Brush,
    onNavigateBack: () -> Unit,
    onDeleteLocation: (WeatherCurrent) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    val searchResult = remember(state.searchLocation) {
        if (state.searchLocation is Resource.Success) {
            state.searchLocation.data
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
            CustomSearchBar(
                query = state.searchQuery,
                onQueryChange = {newQuery ->
                    onEvent(SearchEvent.OnQueryChange(newQuery))
                },
                onSearch = {},
                searchResult = searchResult,
                expanded = isSearchActive,
                onExpandedChange = { isSearchActive = it },
                expandedColor = theme
            )

            if (!isSearchActive) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    item (
                        key = "current_location_${currentLocation.locationName}"
                    ){
                        SavedLocationItem(
                            location = currentLocation,
                            isCurrentLocation = true,
                            onDeleteLocation = {}
                        )
                    }

                    items(
                        items = savedLocation,
                        key = { location -> location.locationName ?: "" }
                    ) { location ->
                        SavedLocationItem(
                            location = location,
                            isCurrentLocation = false,
                            onDeleteLocation = onDeleteLocation
                        )
                    }
                }
            }
        }
    }
}


@Preview (showBackground = true, showSystemUi = true, device = PIXEL_7)
@Composable
fun SearchScreenPreview() {
    var locationList by remember {
        mutableStateOf(
            listOf(
                WeatherCurrent(locationName = "Seoul", icon = "01d", tempMin = 15, tempMax = 25),
                WeatherCurrent(locationName = "Hangzhou", icon = "02d", tempMin = 18, tempMax = 28),
                WeatherCurrent(locationName = "Beijing", icon = "", tempMin = 25, tempMax = 28
                ),
                WeatherCurrent(locationName = "Chongqing", icon = "", tempMin = 25, tempMax = 28
                ),
            )
        )
    }
    SearchScreen(
        state = SearchState(),
        onEvent = {},
        onNavigateBack = {},
        currentLocation = WeatherCurrent(
            locationName = "Fuzhou",
            icon = "",
            tempMin = 25,
            tempMax = 28
        ),
        savedLocation = locationList,
        theme = Brush.verticalGradient(),
        onDeleteLocation = { location ->
//            locationList = locationList.filterNot { it.locationName == location.locationName }

        }
    )
}