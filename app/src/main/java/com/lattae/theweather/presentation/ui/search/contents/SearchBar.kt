package com.lattae.theweather.presentation.ui.search.contents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.lattae.theweather.R
import com.lattae.theweather.presentation.theme.Clouds70
import com.lattae.theweather.presentation.theme.WeatherCardBg
import com.lattae.theweather.presentation.theme.WeatherTextPrimary
import com.lattae.theweather.presentation.theme.WeatherTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSearchBar(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    searchResult: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    expandedColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
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
                .semantics{ traversalIndex = 0f},
            colors = SearchBarDefaults.colors(
                containerColor = expandedColor,
                dividerColor = WeatherCardBg
            ),
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = { textFieldState.edit { replace(0, length, it) }},
                    onSearch = {
                        onSearch(textFieldState.text.toString())
                        onExpandedChange(false)
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
                                imageVector = Icons.Filled.Search,
                                contentDescription = stringResource(R.string.search_location),
                                tint = WeatherTextSecondary
                            )
                        }
                    },
                    colors = SearchBarDefaults.inputFieldColors(
                        focusedContainerColor = containerColor,
                        unfocusedContainerColor = containerColor.copy(0.6f),
                        focusedTextColor = WeatherTextPrimary,
                        unfocusedTextColor = WeatherTextPrimary
                    )
                )
            },
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ){
            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
                searchResult.forEach { result ->
                    ListItem(
                        headlineContent = { Text(result) },
                        modifier = Modifier
                            .clickable{
                                textFieldState.edit { replace(0, length, result) }
                                onExpandedChange(false)
                            }
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}