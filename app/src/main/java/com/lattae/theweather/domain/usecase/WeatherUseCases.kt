package com.lattae.theweather.domain.usecase

import com.lattae.theweather.common.Resource
import com.lattae.theweather.data.repository.WeatherRepository
import com.lattae.theweather.domain.model.AirPollution
import com.lattae.theweather.domain.model.Location
import com.lattae.theweather.domain.model.WeatherCurrent
import com.lattae.theweather.domain.model.WeatherDaily
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Resource<WeatherCurrent> {
        return weatherRepository.getCurrentWeather(lat, lon)
    }
}

class GetDailyForecastWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Resource<List<WeatherDaily>> {
        return weatherRepository.getDailyForecastWeather(lat, lon)
    }
}

class GetAirPollutionUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Resource<AirPollution> {
        return weatherRepository.getAirPollution(lat, lon)
    }
}

class GetSearchLocation @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(locationName: String): Resource<List<Location>> {
        return weatherRepository.getSearchLocation(locationName)
    }
}