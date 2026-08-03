package com.lattae.theweather.presentation.ui.search.viewmodel

import com.lattae.theweather.common.Resource
import com.lattae.theweather.domain.model.Location
import com.lattae.theweather.domain.model.WeatherCurrent

data class SearchState (
    val searchQuery: String = "",
    val savedLocations: Resource<List<Pair<Location, WeatherCurrent>>> = Resource.Loading,
    val searchLocation: Resource<List<Location>> = Resource.Loading
)