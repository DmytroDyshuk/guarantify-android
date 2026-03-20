package com.guarantify.domain.model.sync

sealed class SyncError : Throwable() {
    class NetworkError : SyncError()
    class AuthError : SyncError()
    data class DatabaseError(val throwable: Throwable) : SyncError()
    data class UnknownError(val throwable: Throwable) : SyncError()
}