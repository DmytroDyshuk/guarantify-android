package com.guarantify.data.repository

import androidx.sqlite.SQLiteException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.guarantify.common.di.IoDispatcher
import com.guarantify.common.result.Result
import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.mapper.toDto
import com.guarantify.data.mapper.toEntity
import com.guarantify.data.network.firebase.firestore.FirestoreWarrantyDataSource
import com.guarantify.domain.model.auth.AuthRequiredException
import com.guarantify.domain.model.sync.SyncError
import com.guarantify.domain.model.sync.SyncStatus
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
            pullRemoteChanges()
            pushLocalChanges()
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            val mappedError = when (e) {
                is FirebaseFirestoreException -> SyncError.NetworkError()
                is AuthRequiredException -> SyncError.AuthError()
                is SQLiteException -> SyncError.DatabaseError(e)
                else -> SyncError.UnknownError(e)
            }

            Result.Error(mappedError)
        }
    }

    private suspend fun pullRemoteChanges() {
        val lastLocalUpdate = warrantyDao.getLastUpdatedTimestamp() ?: 0L
        val remoteChanges =
            firestoreWarrantyDataSource.getWarrantiesUpdatedSince(lastLocalUpdate)

        remoteChanges.forEach { remoteDto ->
            val localEntity = warrantyDao.getWarrantyById(remoteDto.id)

            if (localEntity == null) {
                warrantyDao.createOrUpdateWarranty(
                    remoteDto.toEntity().copy(
                        syncStatus = SyncStatus.SYNCED
                    )
                )
            } else {
                if (remoteDto.updatedAt > localEntity.updatedAt) {
                    warrantyDao.createOrUpdateWarranty(
                        remoteDto.toEntity().copy(
                            syncStatus = SyncStatus.SYNCED
                        )
                    )
                }
            }
        }
    }

    private suspend fun pushLocalChanges() {
        val unsyncedWarranties = warrantyDao.getUnsyncedWarranties()
        if (unsyncedWarranties.isEmpty()) return

        val entitiesToUpload = unsyncedWarranties.filter {
            !it.isDeleted && it.syncStatus == SyncStatus.READY_TO_SYNC
        }

        val entitiesToDelete = unsyncedWarranties.filter {
            it.isDeleted
        }

        if (entitiesToUpload.isEmpty() && entitiesToDelete.isEmpty()) return

        firestoreWarrantyDataSource.pushChangesBatch(
            toUpload = entitiesToUpload.map { it.toDto() },
            toDelete = entitiesToDelete.map { it.id }
        )

        if (entitiesToUpload.isNotEmpty()) {
            val uploadedIds = entitiesToUpload.map { it.id }
            warrantyDao.updateSyncStatusForIds(uploadedIds, SyncStatus.SYNCED)
        }

        if (entitiesToDelete.isNotEmpty()) {
            warrantyDao.hardDeleteWarranties(entitiesToDelete)
        }
    }

}