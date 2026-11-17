package com.guarantify.domain.photo

import com.guarantify.common.result.Result

interface PhotoStorage {
    suspend fun copyPhoto(sourceUri: String): Result<String>
}