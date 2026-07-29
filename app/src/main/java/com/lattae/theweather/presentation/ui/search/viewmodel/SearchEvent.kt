package com.lattae.theweather.presentation.ui.search.viewmodel

sealed interface SearchEvent {
    data class OnQueryChange(val newQuery: String): SearchEvent
    data object OnClearQuery: SearchEvent
}