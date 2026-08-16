package com.lattae.theweather.presentation.ui.detail.viewmodel

import com.lattae.theweather.common.Resource
import com.lattae.theweather.domain.model.Weather
import com.lattae.theweather.domain.model.WeatherCurrent

data class DetailState (
    val hasLocationPermission: Boolean = false,
    val detailState: Resource<Weather> = Resource.Loading,
    val currentLocationWeather: Resource<WeatherCurrent> = Resource.Loading,
    val isCurrentLocation: Boolean = true,
    val isSavedLocation: Boolean = false,
    val sunProgress: Float = 0f,
    val isNightMode: Boolean = false
)