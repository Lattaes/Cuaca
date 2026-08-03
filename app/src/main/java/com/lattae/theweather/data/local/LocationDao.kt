package com.lattae.theweather.data.local

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Query("SELECT * from locations ORDER BY name ASC")
    fun getAllLocations(): Flow<List<LocationEntity>>

    @androidx.room.Upsert
    suspend fun upsertLocation(locationEntity: LocationEntity)

    @Query("DELETE from locations WHERE lat = :lat AND lon = :lon")
    suspend fun deleteLocation(lat: Double, lon: Double)
}