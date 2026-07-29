package com.lattae.theweather.data.mapper

import com.lattae.theweather.domain.model.Location
import com.lattae.weatherapp.data.remote.dto.LocationResponse

fun List<LocationResponse>.toLocation(): List<Location> {
    return this.map {
        Location(
            locationName = it.name,
            lat = it.lat,
            lon = it.lon,
            country = it.country,
            state = it.state
        )
    }
}
