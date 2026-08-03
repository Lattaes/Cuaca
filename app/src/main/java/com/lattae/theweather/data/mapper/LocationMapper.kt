package com.lattae.theweather.data.mapper

import com.lattae.theweather.data.local.LocationEntity
import com.lattae.theweather.domain.model.Location
import com.lattae.theweather.data.remote.dto.LocationResponse

@JvmName("toLocationFromResponse")
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

@JvmName("toLocationFromEntity")
fun List<LocationEntity>.toLocation(): List<Location> {
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

fun Location.toEntity(): LocationEntity {
    return LocationEntity(
        name = locationName ?: "",
        lat = lat,
        lon = lon,
        country = country ?: "",
        state = state ?: ""
    )
}
