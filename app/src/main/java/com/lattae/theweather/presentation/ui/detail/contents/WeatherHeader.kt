package com.lattae.theweather.presentation.ui.detail.contents

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lattae.theweather.R
import com.lattae.theweather.presentation.theme.WeatherTextSecondary
import com.lattae.theweather.presentation.ui.detail.viewmodel.DetailEvent


@Composable
fun WeatherHeader(
    location: String,
    detailEvent: DetailEvent,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth())
    {
        AnimatedContent(
            targetState = detailEvent,
            label = "Header Transition"
        ) { state ->
            when (state) {
                is DetailEvent.HeaderDefault -> {
                    HeaderDefaultContent(
                        location = location,
                        onMenuClick = state.onMenuClick
                    )
                }
                is DetailEvent.HeaderSearchResult -> {
                    HeaderSearchResultContent(
                        location = location,
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
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LocationTitle(
            location = location,
            textStyle = MaterialTheme.typography.headlineMedium,
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
            textStyle = MaterialTheme.typography.headlineSmall,
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
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = stringResource(R.string.ic_location),
            tint = WeatherTextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = location,
            style = textStyle,
            color = WeatherTextSecondary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}