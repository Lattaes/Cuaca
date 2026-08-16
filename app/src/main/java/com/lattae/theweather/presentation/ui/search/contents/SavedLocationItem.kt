package com.lattae.theweather.presentation.ui.search.contents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lattae.theweather.R
import com.lattae.theweather.domain.model.WeatherCurrent
import com.lattae.theweather.presentation.components.WeatherInfo
import com.lattae.theweather.presentation.theme.WeatherDivider
import kotlinx.coroutines.delay

@Composable
fun SavedLocationItem(
    location: WeatherCurrent,
    isCurrentLocation: Boolean,
    onItemClick: (WeatherCurrent) -> Unit,
    onDeleteLocation: (WeatherCurrent) -> Unit,
    modifier: Modifier = Modifier
) {

    var isDismissed by remember { mutableStateOf(false) }
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()

    LaunchedEffect(swipeToDismissBoxState.currentValue) {
        if (swipeToDismissBoxState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            isDismissed = true
            delay(300)
            onDeleteLocation(location)
        }
    }

    AnimatedVisibility(
        visible = !isDismissed,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically(animationSpec = tween(300)) +
                fadeOut(animationSpec = tween(300)),
        modifier = modifier
    ) {
        SwipeToDismissBox(
            state = swipeToDismissBoxState,
            enableDismissFromStartToEnd = false,
            enableDismissFromEndToStart = !isCurrentLocation,
            modifier = Modifier.fillMaxSize(),
            backgroundContent = {
                when (swipeToDismissBoxState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd -> {}
                    SwipeToDismissBoxValue.EndToStart -> {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = stringResource(R.string.ic_delete),
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    lerp(Color.LightGray, MaterialTheme.colorScheme.error, swipeToDismissBoxState.progress)
                                )
                                .wrapContentSize(Alignment.CenterEnd)
                                .padding(12.dp),
                            tint = MaterialTheme.colorScheme.onError
                        )
                    }
                    SwipeToDismissBoxValue.Settled -> {}
                }
            }
        ) {
            Column{
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
                    },
                    modifier = Modifier.clickable {
                        onItemClick(location)
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