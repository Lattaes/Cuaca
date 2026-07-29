package com.lattae.theweather.presentation.ui.search.contents

import android.graphics.pdf.models.ListItem
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lattae.theweather.R
import com.lattae.theweather.domain.model.WeatherCurrent
import com.lattae.theweather.presentation.components.WeatherInfo
import com.lattae.theweather.presentation.theme.WeatherDivider
import com.lattae.theweather.presentation.theme.WeatherTextSecondary

@Composable
fun SavedLocationItem(
    location: WeatherCurrent,
    isCurrentLocation: Boolean,
    onDeleteLocation: (WeatherCurrent) -> Unit,
    modifier: Modifier = Modifier
) {

    val swipeToDeleteState = rememberSwipeToDismissBoxState()

    LaunchedEffect(swipeToDeleteState.currentValue) {
        if (swipeToDeleteState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDeleteLocation(location)
        }
    }

    Box(modifier = modifier.animateContentSize()) {
        SwipeToDismissBox(
            state = swipeToDeleteState,
            enableDismissFromEndToStart = !isCurrentLocation,
            enableDismissFromStartToEnd = false,
            backgroundContent = {
                val isSwiping = swipeToDeleteState.targetValue == SwipeToDismissBoxValue.EndToStart
                BackgroundSwipeContainer(
                    containerColor = if (isSwiping) MaterialTheme.colorScheme.error else Color.Transparent,
                    iconColor = if (isSwiping) MaterialTheme.colorScheme.onError else Color.Transparent
                )
            }
        ) {

            Column {
                ListItem(
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    headlineContent = {
                        WeatherInfo(
                            leadingTitle = "${location.locationName}",
                            icon = "${location.icon}",
                            tempMin = "${location.tempMin}",
                            tempMax = "${location.tempMax}",
                            fontStyle = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            isCurrentLocation = isCurrentLocation
                        )
                    }
                )
                HorizontalDivider(
                    color = WeatherDivider,
                    thickness = 0.75.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun BackgroundSwipeContainer(
    containerColor: Color,
    iconColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(containerColor)
            .padding(end = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Rounded.Delete,
            contentDescription = stringResource(R.string.ic_delete),
            tint = iconColor
        )
    }
}

