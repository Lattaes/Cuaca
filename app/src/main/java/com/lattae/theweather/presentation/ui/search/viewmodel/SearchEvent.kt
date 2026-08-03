package com.lattae.theweather.presentation.ui.search.viewmodel

import com.lattae.theweather.domain.model.Location

sealed interface SearchEvent {
    data object OnClearQuery : SearchEvent
    data class OnQueryChange(val newQuery: String) : SearchEvent
    data class OnDeleteLocation(val lat: Double, val lon: Double) : SearchEvent
    data class OnLocationClick(val location: Location) : SearchEvent
}