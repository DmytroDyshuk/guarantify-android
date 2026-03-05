package com.guarantify.data.repository

import android.database.sqlite.SQLiteException
import androidx.core.net.toUri
import com.guarantify.common.di.IoDispatcher
import com.guarantify.common.result.Result
import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.mapper.toDomain
import com.guarantify.data.mapper.toEntityWithGeneratedIdIfNeeded
import com.guarantify.data.network.firebase.storage.WarrantyPhotoStorage
import com.guarantify.domain.model.DatabaseError
import com.guarantify.domain.model.StorageError
import com.guarantify.domain.model.SyncStatus
import com.guarantify.domain.model.Warranty
import com.guarantify.domain.repository.WarrantiesRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WarrantiesRepositoryImpl @Inject constructor(
    private val warrantyDao: WarrantyDao,
    private val warrantyPhotoStorage: WarrantyPhotoStorage,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WarrantiesRepository {
    override val latestWarranties: Flow<List<Warranty>>
        get() = warrantyDao.getAllWarranties()
            .map { entities -> entities.map { it.toDomain() } }
            .catch { e ->
                emit(emptyList())
            }

    override suspend fun getWarranty(warrantyId: String): Result<Warranty> =
        withContext(ioDispatcher) {
            try {
                val warranty = warrantyDao.getWarrantyById(warrantyId)?.toDomain()
                    ?: return@withContext Result.Error(DatabaseError.NotFound())
                Result.Success(warranty)
            } catch (e: SQLiteException) {
                Result.Error(e)
            }
        }

    override suspend fun createOrUpdateWarranty(warranty: Warranty): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val oldWarranty = warrantyDao.getWarrantyById(warranty.id)

                val photoUri = warranty.localPhotoUri?.takeUnless { it.isBlank() }

                val isPhotoChanged = oldWarranty?.localPhotoUri != photoUri

                val syncStatus = if (photoUri != null && isPhotoChanged) {
                    SyncStatus.PENDING
                } else {
                    SyncStatus.READY_TO_SYNC
                }

                val warrantyEntity = warranty.toEntityWithGeneratedIdIfNeeded().copy(
                    syncStatus = syncStatus,
                    remotePhotoUrl = if (isPhotoChanged) null else oldWarranty?.remotePhotoUrl
                )

                warrantyDao.createOrUpdateWarranty(warrantyEntity)

                if (photoUri != null && isPhotoChanged) {
                    startImageUploadChain(warrantyEntity.id, photoUri)
                }

                Result.Success(Unit)
            } catch (e: SQLiteException) {
                Result.Error(e)
            }
        }

    override suspend fun deleteWarranty(warranty: Warranty) {
        withContext(ioDispatcher) {
            val now = System.currentTimeMillis()
            val warrantyEntity = warranty.toEntityWithGeneratedIdIfNeeded()

            warrantyDao.markWarrantyAsDeleted(
                id = warrantyEntity.id,
                updatedAt = now,
                syncStatus = SyncStatus.PENDING
            )
        }
    }

    override suspend fun uploadWarrantyPhoto(warrantyId: String, photoUri: String): Result<String> =
        withContext(ioDispatcher) {
            try {
                warrantyPhotoStorage.uploadImage(warrantyId, photoUri.toUri())
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Result.Error(StorageError.Unknown(e))
            }
        }

    override suspend fun updateRemoteUrlPhotoLocaly(warrantyId: String, photoUrl: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val warranty = warrantyDao.getWarrantyById(warrantyId)
                    ?: return@withContext Result.Error(DatabaseError.NotFound())

                val updatedWarranty = warranty.copy(
                    remotePhotoUrl = photoUrl,
                    syncStatus = SyncStatus.READY_TO_SYNC,
                    updatedAt = System.currentTimeMillis()
                )

                warrantyDao.createOrUpdateWarranty(updatedWarranty)

                Result.Success(Unit)
            } catch (e: SQLiteException) {
                Result.Error(e)
            }
        }

    private fun startImageUploadChain(warrantyId: String, photoUri: String) {
        //TODO: start work chain compress and upload image + update SyncStatus to READY_TO_SYNC
    }

}