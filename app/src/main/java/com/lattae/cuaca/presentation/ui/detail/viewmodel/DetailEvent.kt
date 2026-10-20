package com.lattae.cuaca.presentation.ui.detail.viewmodel

sealed interface DetailEvent {
    data class HeaderDefault(
        val onMenuClick: () -> Unit
    ): DetailEvent

    data class HeaderSearchResult(
        val onBackClick: () -> Unit,
        val onAddClick: () -> Unit
    ): DetailEvent
}