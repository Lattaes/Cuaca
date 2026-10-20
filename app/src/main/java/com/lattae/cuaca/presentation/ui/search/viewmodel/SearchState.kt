package com.lattae.cuaca.presentation.ui.search.viewmodel

import com.lattae.cuaca.common.Resource
import com.lattae.cuaca.domain.model.Location

data class SearchState (
    val searchQuery: String = "",
    val savedLocations: Resource<List<Location>> = Resource.Loading,
    val searchLocation: Resource<List<Location>> = Resource.Loading
)