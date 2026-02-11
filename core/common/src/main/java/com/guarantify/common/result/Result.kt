package com.guarantify.common.result

sealed interface Result<out T> {
    data class Success<out T>(val data: T): Result<T>
    data class Error(val errorMessage: String?): Result<Nothing>
    data object Loading : Result<Nothing>
}