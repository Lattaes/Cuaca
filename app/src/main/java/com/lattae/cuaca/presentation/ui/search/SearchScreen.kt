package com.lattae.cuaca.presentation.ui.search

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.lattae.cuaca.R
import com.lattae.cuaca.common.Resource
import com.lattae.cuaca.domain.model.Location
import com.lattae.cuaca.presentation.theme.WeatherTextSecondary
import com.lattae.cuaca.presentation.ui.navigation.NavigationDestination
import com.lattae.cuaca.presentation.ui.search.contents.CustomSearchBar
import com.lattae.cuaca.presentation.ui.search.contents.SavedLocationItem
import com.lattae.cuaca.presentation.ui.search.viewmodel.SearchEvent
import com.lattae.cuaca.presentation.ui.search.viewmodel.SearchState


object SearchDestination : NavigationDestination {
    override val route = "search"
    override val titleRes = R.string.weather_search_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchState: SearchState,
    onEvent: (SearchEvent) -> Unit,
    currentLocation: Location,
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
                        scrolledContainerColor = MaterialTheme.colorScheme.onPrimaryFixedVariant,
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
                    scrollBehavior = scrollBehavior,
                )
            }
        }
    ) { innerPadding ->
        val topPadding = (innerPadding.calculateTopPadding() - 28.dp).coerceAtLeast(0.dp)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(theme),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = topPadding)
                    .background(theme)
            ) {
                when (val savedLocations = searchState.savedLocations) {
                    is Resource.Success, is Resource.Loading -> {
                        CustomSearchBar(
                            query = searchState.searchQuery,
                            onQueryChange = {newQuery ->
                                onEvent(SearchEvent.OnQueryChange(newQuery))
                            },
                            onSearch = {},
                            searchResult = searchResult,
                            expanded = isSearchActive,
                            onExpandedChange = { isSearchActive = it },
                            onClearQuery = { onEvent(SearchEvent.OnClearQuery) },
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
                                    key = "current_location_${currentLocation.lat}_${currentLocation.lon}"
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
                                val locationList = if (savedLocations is Resource.Success) savedLocations.data else emptyList()

                                items(
                                    items = locationList,
                                    key = { weather ->
                                        "${weather.locationName}_${weather.lat}_${weather.lon}"
                                    }
                                ) { weather ->
                                    SavedLocationItem(
                                        location = weather,
                                        isCurrentLocation = false,
                                        onItemClick = { location ->
                                            onNavigateToDetail(location.lat, location.lon, false)
                                        },
                                        onDeleteLocation = { location ->
                                            onEvent(SearchEvent.OnDeleteLocation(location.id))
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
            AnimatedVisibility(
                visible = searchState.savedLocations is Resource.Loading,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 8.dp),
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it }
            ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
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
}