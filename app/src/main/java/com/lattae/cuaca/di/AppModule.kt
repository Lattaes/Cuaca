package com.lattae.cuaca.di

import android.content.Context
import com.lattae.cuaca.data.remote.api.WeatherApiService
import com.lattae.cuaca.domain.usecase.GetSavedLocationsUseCase
import com.lattae.cuaca.domain.usecase.GetWeatherDetailUseCase
import com.lattae.cuaca.domain.usecase.UpdateSavedLocationWeatherUseCase
import com.lattae.cuaca.domain.usecase.WeatherUseCases
import com.lattae.cuaca.util.INetworkObserver
import com.lattae.cuaca.util.NetworkObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val apiKeyInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val originalUrl = originalRequest.url

            val url = originalUrl.newBuilder()
                .addQueryParameter("appid", com.lattae.cuaca.BuildConfig.WEATHER_API)
                .addQueryParameter("units", "metric")
                .addQueryParameter("lang", "en")
                .build()

            val requestBuilder = originalRequest.newBuilder().url(url)
            chain.proceed(requestBuilder.build())
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(apiKeyInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNetworkObserver(
        @ApplicationContext context: Context
    ) : INetworkObserver = NetworkObserver(context)

    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherUseCases(
        getWeatherDetailUseCase: GetWeatherDetailUseCase,
        getSavedLocations: GetSavedLocationsUseCase,
        updateSavedLocationWeather: UpdateSavedLocationWeatherUseCase
    ): WeatherUseCases {
        return WeatherUseCases(
            getWeatherDetail = getWeatherDetailUseCase,
            getSavedLocations = getSavedLocations,
            updateSavedLocationWeather = updateSavedLocationWeather
        )
    }
}