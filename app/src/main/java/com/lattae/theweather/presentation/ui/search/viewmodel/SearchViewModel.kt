package com.lattae.theweather.presentation.ui.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lattae.theweather.common.Resource
import com.lattae.theweather.domain.usecase.GetSearchLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getSearchLocation: GetSearchLocation
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchState())
    val uiState: StateFlow<SearchState> = _uiState.asStateFlow()

    init {
        observeSearchQuery()
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnQueryChange -> {
                _uiState.update { it.copy(searchQuery = event.newQuery) }
            }

            SearchEvent.OnClearQuery -> {
                _uiState.update { it.copy(searchQuery = "") }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _uiState.map { it.searchQuery }
                .distinctUntilChanged()
                .debounce(500L)
                .collectLatest { query ->
                    if (query.isNotBlank()) {
                        getSearch(query)
                    } else {
                        _uiState.update { it.copy(searchLocation = Resource.Success(emptyList())) }
                    }
                }
        }
    }

    private suspend fun getSearch(query: String) {
        _uiState.update { it.copy(searchLocation = Resource.Loading) }

        val searchResult = getSearchLocation(query)
        _uiState.update { it.copy(searchLocation = searchResult) }
    }
}
