package com.lattae.cuaca.presentation.ui.detail.viewmodel

import com.lattae.cuaca.common.Resource
import com.lattae.cuaca.domain.model.Location
import com.lattae.cuaca.domain.model.Weather

data class DetailState (
    val hasLocationPermission: Boolean = false,
    val detailState: Resource<Weather> = Resource.Loading,
    val currentLocationWeather: Resource<Location> = Resource.Loading,
    val isCurrentLocation: Boolean = true,
    val isSavedLocation: Boolean = false,
    val isRefreshing: Boolean = false,
    val offlineErrorMessage: String? = null
)