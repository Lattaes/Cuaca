package com.lattae.cuaca.data.mapper

import com.lattae.cuaca.data.local.LocationEntity
import com.lattae.cuaca.domain.model.Location
import com.lattae.cuaca.data.remote.dto.LocationResponse
import com.lattae.cuaca.domain.model.WeatherCurrent

@JvmName("toLocationDomainFromResponse")
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

@JvmName("toLocationDomainFromEntity")
fun List<LocationEntity>.toLocation(): List<Location> {
    return this.map {
        Location(
            id = it.locationId,
            locationName = it.locationName,
            isCurrentLocation = it.isCurrentLocation,
            isSavedLocation = it.isSaved,
            lat = it.lat,
            lon = it.lon,
            icon = it.weatherCurrent?.icon,
            tempMin = it.weatherCurrent?.tempMin ?: 0,
            tempMax = it.weatherCurrent?.tempMax ?: 0,
            country = it.country
        )
    }
}

fun LocationEntity.toLocation(): Location {
    return Location(
        id = this.locationId,
        locationName = this.locationName,
        isCurrentLocation = this.isCurrentLocation,
        isSavedLocation = this.isSaved,
        lat = this.lat,
        lon = this.lon,
        icon = this.weatherCurrent?.icon,
        tempMin = this.weatherCurrent?.tempMin ?: 0,
        tempMax = this.weatherCurrent?.tempMax ?: 0,
        country = this.country,
    )
}

@JvmName("toLocationDomainFromWeatherCurrentDomain")
fun WeatherCurrent.toLocation(
    id: Int = this.locationId,
    isCurrentLocation: Boolean = false,
    isSavedLocation: Boolean = false,
    state: String? = null
): Location {
    return Location(
        id = id,
        locationName = this.locationName,
        isCurrentLocation = isCurrentLocation,
        isSavedLocation = isSavedLocation,
        lat = this.lat,
        lon = this.lon,
        icon = this.icon,
        tempMin = this.tempMin,
        tempMax = this.tempMax,
        country = this.country,
        state = state
    )
}