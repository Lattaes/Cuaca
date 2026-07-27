package com.lattae.theweather.presentation.ui.search.viewmodel

import com.lattae.theweather.common.Resource
import com.lattae.theweather.domain.model.Location

data class SearchState (
    val searchLocation: Resource<List<Location>> = Resource.Loading
)