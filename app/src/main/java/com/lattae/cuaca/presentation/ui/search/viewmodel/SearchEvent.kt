package com.lattae.cuaca.presentation.ui.search.viewmodel

import com.lattae.cuaca.domain.model.Location

sealed interface SearchEvent {
    data object OnClearQuery : SearchEvent
    data class OnQueryChange(val newQuery: String) : SearchEvent
    data class OnDeleteLocation(val locationId: Int) : SearchEvent
    data class OnLocationClick(val location: Location) : SearchEvent
}