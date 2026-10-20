package com.lattae.cuaca.presentation.ui.detail.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lattae.cuaca.R
import com.lattae.cuaca.common.Resource
import com.lattae.cuaca.data.mapper.toLocation
import com.lattae.cuaca.domain.location.ILocationManager
import com.lattae.cuaca.domain.model.Location
import com.lattae.cuaca.domain.repository.IWeatherRepository
import com.lattae.cuaca.domain.usecase.WeatherUseCases
import com.lattae.cuaca.util.NetworkObserver
import com.lattae.cuaca.util.generateId
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class DetailViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val geocoder: Geocoder,
    private val locationManager: ILocationManager,
    private val weatherRepository: IWeatherRepository,
    private val weatherUseCases: WeatherUseCases,
    private val networkObserver: NetworkObserver
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailState())
    val uiState: StateFlow<DetailState> = _uiState.asStateFlow()

    private var currentLat: Double? = null
    private var currentLon: Double? = null
    private var isCurrentLocation: Boolean? = null

    init {
        observeNetworkConnectivity()
    }

    private fun observeNetworkConnectivity() {
        viewModelScope.launch {
            networkObserver.isConnected
                .drop(1)
                .collect { isConnected ->
                    if (isConnected) loadDetailWeather(currentLat, currentLon, isCurrentLocation)
                }
        }
    }

    fun onPermissionGranted() {
        _uiState.update { it.copy(hasLocationPermission = true) }
        loadDetailWeather()
    }

    fun loadDetailWeather(
        latitude: Double? = currentLat,
        longitude: Double? = currentLon,
        isCurrentLoc: Boolean? = isCurrentLocation) {

        currentLat = latitude
        currentLon = longitude
        isCurrentLocation = isCurrentLoc

        viewModelScope.launch {
            val isCurrentLocation = isCurrentLoc ?: (latitude == null && longitude == null)
            val coordinates = getCoordinates(latitude, longitude)
            val (lat, lon) = coordinates ?: run {
                _uiState.update {
                    if (it.detailState is Resource.Success) {
                        it.copy(
                            offlineErrorMessage = context.getString(R.string.get_location_failed),
                            isRefreshing = false
                        )
                    } else {
                        it.copy(
                            detailState = Resource.Failure(context.getString(R.string.get_location_failed)),
                            isRefreshing = false
                        )
                    }
                }
                return@launch
            }

            weatherUseCases.getWeatherDetail(lat, lon, isCurrentLocation).collect { result ->
                when(result) {
                    is Resource.Success -> {
                        val weather = result.data

                        val locationId = generateId(lat, lon)
                        val savedLocation = weatherRepository.getLocationById(
                            weather.weatherCurrent?.locationId ?: locationId
                        )
                        val isSavedLocation = savedLocation != null

                        val address = if (isCurrentLocation) {
                            withContext(Dispatchers.IO) {
                                try {
                                    withTimeoutOrNull(3000L) {
                                        getLocationAddress(
                                            weather.weatherCurrent?.lat ?: lat,
                                            weather.weatherCurrent?.lon ?: lon
                                        )
                                    }
                                } catch (e: Exception) {
                                    if (e is CancellationException) throw e
                                    null
                                }
                            }
                        } else null

                        val addressName = if (isCurrentLocation) {
                            address?.subLocality ?: address?.adminArea ?: weather.weatherCurrent?.locationName
                        } else weather.weatherCurrent?.locationName

                        val updatedWeather = weather.copy(
                            weatherCurrent = weather.weatherCurrent?.copy(
                                locationName = addressName
                            )
                        )

                        _uiState.update { state ->
                            // For sharing state in search screen
                            val updatedCurrentWeather = if (isCurrentLocation && updatedWeather.weatherCurrent != null) {
                                updatedWeather.weatherCurrent.let { curr -> Resource.Success(curr.toLocation()) }
                            } else state.currentLocationWeather

                            state.copy(
                                detailState = Resource.Success(updatedWeather),
                                currentLocationWeather = updatedCurrentWeather,
                                isCurrentLocation = isCurrentLocation,
                                isSavedLocation = isSavedLocation,
                                isRefreshing = false,
                                offlineErrorMessage = null
                            )
                        }
                    }
                    is Resource.Failure -> {
                        val error = result.errorMessage
                        _uiState.update { state ->
                            if (state.detailState is Resource.Success) {
                                state.copy(
                                    offlineErrorMessage = error.ifEmpty { context.getString(R.string.update_weather_failed) },
                                    isRefreshing = false
                                )
                            } else {
                                state.copy(
                                    detailState = Resource.Failure(error),
                                    isRefreshing = false
                                )
                            }
                        }
                    }
                    is Resource.Loading -> {
                        _uiState.update { state ->
                            state.copy(isRefreshing = true)
                        }
                    }
                }
            }
        }
    }

    fun saveLocation() {
        viewModelScope.launch {
            try {
                val state = _uiState.value.detailState
                if (state is Resource.Success) {
                    val weather = state.data
                    val location = Location(
                        id = weather.weatherCurrent?.locationId ?: 0,
                        locationName = weather.weatherCurrent?.locationName,
                        isSavedLocation = true,
                        lat = weather.weatherCurrent?.lat ?: 0.0,
                        lon = weather.weatherCurrent?.lon ?: 0.0,
                        country = weather.weatherCurrent?.country,
                    )
                    weatherRepository.saveLocation(weather, location)
                    _uiState.update { it.copy(isSavedLocation = true) }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("DetailViewModel", context.getString(R.string.save_location_failed), e)
                _uiState.update {
                    it.copy(offlineErrorMessage = context.getString(R.string.save_location_failed))
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun getLocationAddress(lat: Double, lon: Double): Address? {
        if (!Geocoder.isPresent()) return null
        return suspendCancellableCoroutine { continuation ->
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(lat, lon, 1) { address ->
                        if (continuation.isActive) {
                            if (address.isNotEmpty()) {
                                continuation.resume(address[0])
                            } else {
                                continuation.resume(null)
                            }
                        }
                    }
                } else {
                    val address = geocoder.getFromLocation(lat, lon, 1)
                    if (continuation.isActive) {
                        if (!address.isNullOrEmpty()) {
                            continuation.resume(address[0])
                        } else {
                            continuation.resume(null)
                        }
                    }
                }
            } catch (_: Exception) {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
        }
    }

    suspend fun getCoordinates(lat: Double?, lon: Double?): Pair<Double, Double>? {
        if (lat != null && lon != null) {
            return Pair(lat, lon)
        }

        return try {
            val locationCoord = locationManager.getCurrentLocation() ?: return null
            Pair(locationCoord.latitude, locationCoord.longitude)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e("DetailViewModel", context.getString(R.string.getting_current_loc_failed), e)
            null
        }
    }
}