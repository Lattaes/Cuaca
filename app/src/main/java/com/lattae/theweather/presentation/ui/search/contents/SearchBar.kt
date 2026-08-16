package com.lattae.theweather.presentation.ui.search.contents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.lattae.theweather.R
import com.lattae.theweather.domain.model.Location
import com.lattae.theweather.presentation.theme.WeatherCardBg
import com.lattae.theweather.presentation.theme.WeatherTextPrimary
import com.lattae.theweather.presentation.theme.WeatherTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onItemClick: (Location) -> Unit,
    searchResult: List<Location>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    expandedColor: Brush,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .semantics{ isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.TopCenter)
                .then(
                    if (expanded) {
                        Modifier.background(expandedColor)
                    } else {
                        Modifier
                    }
                )
                .semantics{ traversalIndex = 0f},
            colors = SearchBarDefaults.colors(
                containerColor = Color.Transparent,
                dividerColor = WeatherCardBg
            ),
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = {
                        onSearch(it)
                        focusManager.clearFocus()
                    },
                    expanded = expanded,
                    onExpandedChange = onExpandedChange,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_location),
                            color = WeatherTextSecondary
                        )
                    },
                    leadingIcon = {
                        if (expanded) {
                            IconButton(onClick = { onExpandedChange(false) }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.ic_back),
                                    tint = WeatherTextSecondary
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = stringResource(R.string.search_location),
                                tint = WeatherTextSecondary
                            )
                        }
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onClearQuery() }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = stringResource(R.string.ic_close),
                                    tint = WeatherTextSecondary
                                )
                            }
                        }
                    },
                    colors = SearchBarDefaults.inputFieldColors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface.copy(0.7f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(0.2f),
                        focusedTextColor = WeatherTextPrimary,
                        unfocusedTextColor = WeatherTextPrimary
                    ),
                )
            },
            expanded = expanded,
            onExpandedChange = onExpandedChange,
        ){
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(searchResult) { location ->
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        ),
                        headlineContent = {
                            Text(
                                text = "${location.locationName}",
                                color = WeatherTextPrimary
                            )
                        },
                        supportingContent = {
                            Text(
                                text = "${location.state}, ${location.country}",
                                color = WeatherTextPrimary
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = stringResource(R.string.ic_location),
                                tint = MaterialTheme.colorScheme.onPrimary.copy(0.5f)
                            )
                        },
                        modifier = Modifier
                            .clickable {
                                location.locationName?.let { onQueryChange(it) }
                                onExpandedChange(false)
                                onSearch(location.locationName ?: "")
                                onItemClick(location)
                            }
                            .fillMaxWidth()
                    )
                    HorizontalDivider(color = WeatherCardBg.copy(alpha = 0.3f))
                }
            }
        }
    }
}
