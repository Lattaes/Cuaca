package com.lattae.cuaca.common

import coil.network.HttpException
import kotlinx.coroutines.CancellationException
import okio.IOException

sealed class Resource<out T> {
    data object Loading: Resource<Nothing>()
    data class Success<T>(val data: T): Resource<T>()
    data class Failure(val error: String): Resource<Nothing>()

    val isLoading get() = this is Loading
    val isSuccess get() = this is Success
    val isFailure get() = this is Failure

    val errorMessage get() = if (this.isFailure) (this as Failure).error else ""
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
    return try {
        Resource.Success(apiCall())
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        Resource.Failure("Server Error (${e.hashCode()}): ${e.message}")
    } catch (e: IOException) {
        Resource.Failure("No Internet Connection. ${e.message}")
    } catch (e: Exception) {
        Resource.Failure(e.message ?: "Unknown error")
    }
}