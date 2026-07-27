package com.lattae.theweather.presentation.ui.detail.viewmodel

import com.lattae.theweather.common.Resource
import com.lattae.theweather.domain.model.Weather

data class DetailState (
    val hasLocationPermission: Boolean = false,
    val state: Resource<Weather> = Resource.Loading
)