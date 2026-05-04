package com.guarantify.domain.model

sealed class StorageError : Throwable() {
    class NetworkError : StorageError()
    class QuotaExceeded : StorageError()
    class Unauthorized : StorageError()
    class NotFound : StorageError()
    data class Unknown(val e: Throwable) : StorageError()
}