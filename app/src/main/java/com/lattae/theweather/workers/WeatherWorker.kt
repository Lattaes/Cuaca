package com.lattae.theweather.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lattae.theweather.common.Resource
import com.lattae.theweather.domain.location.ILocationManager
import com.lattae.theweather.domain.repository.IWeatherRepository
import com.lattae.theweather.util.WeatherNotificationManager
import com.lattae.theweather.util.WeatherType.Companion.from
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WeatherWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val locationManager: ILocationManager,
    private val repository: IWeatherRepository,
    private val notificationManager: WeatherNotificationManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val location = locationManager.getCurrentLocation()


        return try {
            val response = repository.getCurrentWeather(location?.latitude ?: 0.0,
                location?.longitude ?: 0.0
            )

            if (response is Resource.Success) {
                val weather = response.data
                val type = from(weather.weatherId)

                if (type.notifyWeather(weather.weatherId)) {
                    notificationManager.showWeatherNotification(type, weather.locationName ?: "Failed to detect location")
                }
            }

            return Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}