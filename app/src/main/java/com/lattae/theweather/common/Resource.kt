package com.lattae.theweather.common

sealed class Resource<out T> {
    data object Loading: Resource<Nothing>()
    data class Success<T>(val data: T): Resource<T>()
    data class Failure(val error: String): Resource<Nothing>()

    val isLoading get() = this is Loading
    val isSuccess get() = this is Success
    val isFailure get() = this is Failure

    val errorMessage get() = if (this.isFailure) (this as Failure).error else ""
}