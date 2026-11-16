package com.guarantify.domain.repository

import com.guarantify.common.result.Result

interface LocalDataRepository {
    suspend fun clearLocalData(): Result<Unit>
}