package com.guarantify.data.repository

import android.content.Context
import com.guarantify.common.di.IoDispatcher
import com.guarantify.common.result.Result
import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.workmanager.WarrantyWork
import com.guarantify.domain.model.DatabaseError
import com.guarantify.domain.preferences.SyncPreferencesManager
import com.guarantify.domain.repository.LocalDataRepository
import com.guarantify.domain.sync.SyncScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LocalDataRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val syncScheduler: SyncScheduler,
    private val warrantyDao: WarrantyDao,
    private val syncPreferencesManager: SyncPreferencesManager,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocalDataRepository {

    override suspend fun clearLocalData(): Result<Unit> = withContext(ioDispatcher) {
        try {
            syncScheduler.cancelAllWork()

            warrantyDao.deleteAllWarranties()
            syncPreferencesManager.clear()
            deleteCompressedPhotos()

            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.Error(DatabaseError.Unknown(e))
        }
    }

    private fun deleteCompressedPhotos() {
        context.cacheDir
            ?.listFiles { file -> file.isFile && file.name.startsWith(WarrantyWork.COMPRESSED_PHOTO_PREFIX) }
            ?.forEach { it.delete() }
    }

}