package com.guarantify.domain.repository

import com.guarantify.common.result.Result
import com.guarantify.domain.model.sync.SyncStatus
import com.guarantify.domain.model.Warranty

interface SyncWarrantiesRepository {

    suspend fun syncWarranties(): Result<Unit>

}