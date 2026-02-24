package com.guarantify.domain.model

sealed class FirebaseStorageError : Throwable() {
    class NetworkError : FirebaseStorageError()
    class QuotaExceeded : FirebaseStorageError()
    class Unauthorized : FirebaseStorageError()
    class NotFound : FirebaseStorageError()
    data class Unknown(val e: Throwable) : FirebaseStorageError()
}