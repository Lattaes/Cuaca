package com.lattae.theweather.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.lattae.theweather.domain.location.ILocationManager
import com.lattae.theweather.util.hasPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
    ): ILocationManager {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? {

        if (!context.hasPermission()) {
            throw ILocationManager.LocationException("Missing location permission")
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            throw ILocationManager.LocationException("GPS & Network disabled")
        }

        return suspendCancellableCoroutine { coroutine ->
            val cancellationTokenSource = CancellationTokenSource()

            fusedLocationProviderClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                coroutine.resume(location)
            }.addOnFailureListener { exception ->
                coroutine.resume(null)
            }

            coroutine.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
    }

}