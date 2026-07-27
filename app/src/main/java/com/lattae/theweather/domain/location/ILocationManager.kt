package com.lattae.theweather.domain.location

import android.location.Location

interface ILocationManager {
    suspend fun getCurrentLocation(): Location?

    class LocationException(message: String): Exception(message)
}