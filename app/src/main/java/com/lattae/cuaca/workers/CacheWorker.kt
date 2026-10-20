package com.lattae.cuaca.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lattae.cuaca.domain.usecase.WeatherUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class CacheWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val weatherUseCases: WeatherUseCases,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                weatherUseCases.updateSavedLocationWeather()
                Result.success()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Result.retry()
            }
        }
    }

    companion object {
        const val WORK_NAME = "WeatherSyncWorkerTask"
    }
}