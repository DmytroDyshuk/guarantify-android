package com.guarantify.domain.model

data class AuthResult(
    val data: UserData?,
    val errorMessage: String?
)