package com.lattae.cuaca.di

import com.lattae.cuaca.data.repository.WeatherRepository
import com.lattae.cuaca.domain.repository.IWeatherRepository
import com.lattae.cuaca.workers.CacheRepository
import com.lattae.cuaca.workers.ICacheWorkerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepository: WeatherRepository
    ): IWeatherRepository

    @Binds
    @Singleton
    abstract fun bindCacheWorkerRepository(
        cacheRepository: CacheRepository
    ): ICacheWorkerRepository
}