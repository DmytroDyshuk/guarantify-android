package com.guarantify.domain.model

sealed class DatabaseError : Throwable() {
    class NotFound : DatabaseError()
    data class Unknown(val e: Throwable) : DatabaseError()
}