package com.guarantify.data.repository

import com.guarantify.common.di.IoDispatcher
import com.guarantify.common.result.Result
import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.network.firebase.firestore.FirestoreWarrantyDataSource
import com.guarantify.domain.repository.SyncWarrantiesRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SyncWarrantiesRepositoryImpl @Inject constructor(
    private val firestoreWarrantyDataSource: FirestoreWarrantyDataSource,
    private val warrantyDao: WarrantyDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SyncWarrantiesRepository {

    override suspend fun syncWarranties(): Result<Unit> = withContext(ioDispatcher) {
        try {
            val unsyncedWarranties = warrantyDao.getUnsyncedWarranties()



            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.Error(e)
        }
    }

}