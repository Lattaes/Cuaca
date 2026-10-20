package com.lattae.cuaca.presentation.ui.detail.contents

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lattae.cuaca.R
import com.lattae.cuaca.presentation.theme.WeatherTextSecondary
import com.lattae.cuaca.presentation.ui.detail.viewmodel.DetailEvent


@Composable
fun WeatherHeader(
    location: String,
    isCurrentLocation: Boolean,
    detailEvent: DetailEvent,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .fillMaxWidth()
        .padding(top = 16.dp)
    )
    {
        AnimatedContent(
            targetState = detailEvent,
            label = "Header Transition"
        ) { state ->
            when (state) {
                is DetailEvent.HeaderDefault -> {
                    HeaderDefaultContent(
                        location = location,
                        isCurrentLocation = isCurrentLocation,
                        onMenuClick = state.onMenuClick
                    )
                }
                is DetailEvent.HeaderSearchResult -> {
                    HeaderSearchResultContent(
                        location = location,
                        isCurrentLocation = isCurrentLocation,
                        onBackClick = state.onBackClick,
                        onAddClick = state.onAddClick,
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderDefaultContent(
    location: String,
    isCurrentLocation: Boolean,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LocationTitle(
            location = location,
            isCurrentLocation = isCurrentLocation,
            textStyle = MaterialTheme.typography.headlineLarge,
            maxlines = 2
        )
        Icon(
            imageVector = Icons.Rounded.Menu,
            contentDescription = stringResource(R.string.ic_menu),
            tint = WeatherTextSecondary,
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .clip(CircleShape)
                .clickable(
                    enabled = true,
                    onClick = onMenuClick
                )
        )
    }

}

@Composable
fun HeaderSearchResultContent(
    location: String,
    isCurrentLocation: Boolean,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.ic_back),
            tint = WeatherTextSecondary,
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .clip(CircleShape)
                .clickable(
                    enabled = true,
                    onClick = onBackClick
                )
        )
        LocationTitle(
            location = location,
            isCurrentLocation = isCurrentLocation,
            textStyle = MaterialTheme.typography.headlineSmall,
            maxlines = 1,
            modifier = Modifier.weight(1f)
        )
        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(0.3f),
                contentColor = WeatherTextSecondary
            )
        ) {
            Text(
                text = stringResource(R.string.btn_add),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun LocationTitle(
    location: String,
    isCurrentLocation: Boolean,
    textStyle: TextStyle,
    maxlines: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isCurrentLocation) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = stringResource(R.string.ic_location),
                tint = WeatherTextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.size(4.dp))
        }
        Text(
            text = location,
            style = textStyle,
            color = WeatherTextSecondary,
            fontWeight = FontWeight.Medium,
            overflow = TextOverflow.Ellipsis,
            maxLines = maxlines
        )
    }
}