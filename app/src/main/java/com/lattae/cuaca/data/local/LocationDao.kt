package com.lattae.cuaca.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Query("SELECT * from locations WHERE is_saved = 1 ORDER BY location_name ASC")
    fun getAllLocations(): Flow<List<LocationEntity>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(locationEntity: LocationEntity)

    @Query("SELECT * FROM locations WHERE location_id = :locationId")
    suspend fun getLocationById(locationId: Int): LocationEntity?

    @Query("SELECT * FROM locations WHERE is_current_location = 1 LIMIT 1")
    suspend fun getCurrentLocationCache(): LocationEntity?

    @Query("UPDATE locations SET is_current_location = 0 WHERE is_current_location = 1 AND is_saved = 1")
    suspend fun unmarkSavedCurrentLocation()

    @Query("DELETE from locations WHERE location_id = :locationId")
    suspend fun deleteLocation(locationId: Int)

    @Query("DELETE from locations WHERE is_current_location = 1 AND is_saved = 0")
    suspend fun deletePreviousUnsavedCurrentLocation()
}