package com.lattae.theweather.presentation.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices.PIXEL_7
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.lattae.theweather.R
import com.lattae.theweather.domain.model.WeatherCurrent
import com.lattae.theweather.presentation.components.WeatherInfo
import com.lattae.theweather.presentation.theme.Clouds50
import com.lattae.theweather.presentation.theme.Clouds70
import com.lattae.theweather.presentation.theme.WeatherDivider
import com.lattae.theweather.presentation.theme.WeatherTextSecondary
import com.lattae.theweather.presentation.ui.search.contents.CustomSearchBar
import com.lattae.theweather.presentation.ui.search.contents.SavedLocationItem
import com.lattae.theweather.presentation.ui.search.viewmodel.SearchState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchState,
    currentLocation: WeatherCurrent,
    savedLocation: List<WeatherCurrent>,
    appBarColor: Color,
    theme: Color,
    searchContainerColor: Color,
    onBackClick: () -> Unit,
    onDeleteLocation: (WeatherCurrent) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    val searchTextFieldState = remember { TextFieldState("") }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = theme,
        topBar = {
            if (!isSearchActive) {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = appBarColor,
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
                                    onClick = onBackClick
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
        ) {
            CustomSearchBar(
                textFieldState = searchTextFieldState,
                onSearch = {},
                searchResult = listOf(),
                expanded = isSearchActive,
                onExpandedChange = { isSearchActive = it },
                expandedColor = theme,
                containerColor = searchContainerColor
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
        onBackClick = {},
        currentLocation = WeatherCurrent(
            locationName = "Fuzhou",
            icon = "",
            tempMin = 25,
            tempMax = 28
        ),
        savedLocation = locationList,
        theme = Clouds50,
        appBarColor =Clouds50,
        searchContainerColor = Clouds70,
        onDeleteLocation = { location ->
//            locationList = locationList.filterNot { it.locationName == location.locationName }

        }
    )
}