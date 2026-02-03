package com.guarantify.domain.model.auth

import com.guarantify.domain.model.UserData

data class AuthResult(
    val data: UserData?,
    val errorMessage: String?
)