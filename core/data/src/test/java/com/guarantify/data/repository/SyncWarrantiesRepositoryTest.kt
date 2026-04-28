package com.guarantify.data.repository

import android.database.sqlite.SQLiteException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.guarantify.common.result.Result
import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.database.entity.WarrantyEntity
import com.guarantify.data.mapper.toDto
import com.guarantify.data.mapper.toEntity
import com.guarantify.data.network.dto.WarrantyDto
import com.guarantify.data.network.firebase.firestore.FirestoreWarrantyDataSource
import com.guarantify.domain.model.auth.AuthRequiredException
import com.guarantify.domain.model.sync.SyncError
import com.guarantify.domain.model.sync.SyncStatus
import com.guarantify.domain.preferences.SyncPreferencesManager
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SyncWarrantiesRepositoryTest {
    val testDispatcher = UnconfinedTestDispatcher()

    @MockK(relaxed = true)
    private lateinit var warrantyDao: WarrantyDao

    @MockK(relaxed = true)
    private lateinit var firestoreWarrantyDataSource: FirestoreWarrantyDataSource

    @MockK(relaxed = true)
    private lateinit var syncPreferencesManager: SyncPreferencesManager

    private lateinit var repository: SyncWarrantiesRepositoryImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        repository = SyncWarrantiesRepositoryImpl(
            warrantyDao = warrantyDao,
            firestoreWarrantyDataSource = firestoreWarrantyDataSource,
            syncPreferencesManager = syncPreferencesManager,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun syncWarranties_should_return_result_success_when_sync_succeed() = runTest {
        val listWarrantiesDto = listOf(
            WarrantyDto(
                id = "1",
                updatedAt = 1200L,
                currency = "USD",
                purchaseDate = "2024-01-01",
                expirationDate = "2026-01-01"
            )
        )
        val localWarranty = WarrantyEntity(
            id = "1",
            userId = "1",
            updatedAt = 900L,
            purchaseDate = LocalDate.of(2024, 1, 1),
            expirationDate = LocalDate.of(2026, 1, 1),
            currency = "USD",
            title = "Test"
        )

        coEvery { syncPreferencesManager.getLastSyncTimestamp() } returns 1000L
        coEvery {
            firestoreWarrantyDataSource.getWarrantiesUpdatedSince(1000L)
        } returns listWarrantiesDto
        coEvery { warrantyDao.getWarrantiesByIds(listOf("1")) } returns listOf(localWarranty)
        coEvery { warrantyDao.applyRemoteChanges(any(), any()) } just Runs
        coEvery { warrantyDao.getUnsyncedWarranties() } returns emptyList()

        val result = repository.syncWarranties()
        assertTrue { result is Result.Success }

        coVerify(atLeast = 1) { syncPreferencesManager.updateLastSyncTimestamp(any()) }
        coVerify(exactly = 1) { warrantyDao.applyRemoteChanges(any(), any()) }
        coVerify(exactly = 1) { warrantyDao.getUnsyncedWarranties() }
    }

    @Test
    fun pullRemoteChanges_should_update_local_warranty_when_remote_is_newer() = runTest {
        val localWarranty = WarrantyEntity(
            id = "1",
            userId = "1",
            updatedAt = 1000L,
            purchaseDate = LocalDate.of(2024, 1, 1),
            expirationDate = LocalDate.of(2026, 1, 1),
            currency = "USD",
            title = "Test"
        )
        val remoteWarranty = WarrantyDto(
            id = "1",
            updatedAt = 1500L,
            purchaseDate = "2024-01-01",
            expirationDate = "2026-01-01",
            currency = "USD"
        )

        coEvery { syncPreferencesManager.getLastSyncTimestamp() } returns 1000L
        coEvery {
            firestoreWarrantyDataSource.getWarrantiesUpdatedSince(1000L)
        } returns listOf(remoteWarranty)
        coEvery { warrantyDao.getWarrantiesByIds(listOf("1")) } returns listOf(localWarranty)
        coEvery { warrantyDao.applyRemoteChanges(any(), any()) } just Runs
        coEvery { warrantyDao.getUnsyncedWarranties() } returns emptyList()

        val result = repository.syncWarranties()
        assertTrue(result is Result.Success)

        coVerify { syncPreferencesManager.updateLastSyncTimestamp(any()) }
        coVerify(exactly = 1) { warrantyDao.getWarrantiesByIds(listOf("1")) }
        coVerify(exactly = 1) {
            warrantyDao.applyRemoteChanges(
                upsertList = match {
                    it.isNotEmpty() && it.all { entity -> entity.syncStatus == SyncStatus.SYNCED }
                },
                deleteIds = any()
            )
        }
        coVerify(exactly = 1) { warrantyDao.getUnsyncedWarranties() }
    }

    @Test
    fun pullRemoteChanges_local_entity_should_not_be_updated_when_remote_older_than_local() =
        runTest {
            val remoteWarranty = WarrantyDto(
                id = "1",
                updatedAt = 700L,
                purchaseDate = "2024-01-01",
                expirationDate = "2026-01-01",
                currency = "USD"
            )
            val localWarranty = WarrantyEntity(
                id = "1",
                userId = "1",
                updatedAt = 1000L,
                purchaseDate = LocalDate.of(2024, 1, 1),
                expirationDate = LocalDate.of(2026, 1, 1),
                currency = "USD",
                title = "Test"
            )

            coEvery { syncPreferencesManager.getLastSyncTimestamp() } returns 1000L
            coEvery {
                firestoreWarrantyDataSource.getWarrantiesUpdatedSince(1000L)
            } returns listOf(remoteWarranty)
            coEvery { warrantyDao.getWarrantiesByIds(listOf("1")) } returns listOf(localWarranty)
            coEvery { warrantyDao.applyRemoteChanges(any(), any()) } just Runs
            coEvery { warrantyDao.getUnsyncedWarranties() } returns emptyList()

            val result = repository.syncWarranties()
            assertTrue(result is Result.Success)

            coVerify(exactly = 0) { warrantyDao.applyRemoteChanges(any(), any()) }
        }

    @Test
    fun syncWarranties_should_return_network_error_when_firestore_fails() = runTest {
        val mockFirebaseException = mockk<FirebaseFirestoreException>()

        coEvery { syncPreferencesManager.getLastSyncTimestamp() } returns 1000L
        coEvery {
            firestoreWarrantyDataSource.getWarrantiesUpdatedSince(1000L)
        } throws mockFirebaseException

        val result = repository.syncWarranties()
        assertTrue { result is Result.Error }

        val error = (result as Result.Error).throwable
        assertTrue { error is SyncError.NetworkError }

        coVerify(exactly = 0) { syncPreferencesManager.updateLastSyncTimestamp(any()) }
        coVerify(exactly = 0) { warrantyDao.getWarrantiesByIds(any()) }
        coVerify(exactly = 0) { warrantyDao.applyRemoteChanges(any(), any()) }
    }

    @Test
    fun syncWarranties_should_return_auth_error_when_user_not_authenticated() = runTest {
        val mockAuthException = mockk<AuthRequiredException>()

        coEvery { syncPreferencesManager.getLastSyncTimestamp() } returns 1000L
        coEvery {
            firestoreWarrantyDataSource.getWarrantiesUpdatedSince(1000L)
        } throws mockAuthException

        val result = repository.syncWarranties()
        assertTrue { result is Result.Error }

        val error = (result as Result.Error).throwable
        assertTrue { error is SyncError.AuthError }

        coVerify(exactly = 0) { syncPreferencesManager.updateLastSyncTimestamp(any()) }
        coVerify(exactly = 0) { warrantyDao.getWarrantiesByIds(any()) }
        coVerify(exactly = 0) { warrantyDao.applyRemoteChanges(any(), any()) }
    }

    @Test
    fun syncWarranties_should_return_SQLite_exception_when_local_db_fails() = runTest {
        val mockSQLiteException = mockk<SQLiteException>()

        val listWarrantiesDto = listOf(
            WarrantyDto(
                id = "1",
                updatedAt = 1000L,
                currency = "USD",
                purchaseDate = "2024-01-01",
                expirationDate = "2026-01-01"
            )
        )

        coEvery { syncPreferencesManager.getLastSyncTimestamp() } returns 1200L
        coEvery {
            firestoreWarrantyDataSource.getWarrantiesUpdatedSince(1200L)
        } returns listWarrantiesDto
        coEvery { warrantyDao.getWarrantiesByIds(listOf("1")) } throws mockSQLiteException

        val result = repository.syncWarranties()
        assertTrue { result is Result.Error }

        val error = (result as Result.Error).throwable
        assertTrue { error is SyncError.DatabaseError }

        coVerify(exactly = 0) { syncPreferencesManager.updateLastSyncTimestamp(any()) }
        coVerify(exactly = 0) { warrantyDao.applyRemoteChanges(any(), any()) }
    }

    @Test
    fun pullRemoteChanges_should_create_new_entity_when_local_is_null() = runTest {
        val remoteWarranty = WarrantyDto(
            id = "1",
            updatedAt = 1200L,
            purchaseDate = "2024-01-01",
            expirationDate = "2026-01-01",
            currency = "USD"
        )

        coEvery { syncPreferencesManager.getLastSyncTimestamp() } returns 1000L
        coEvery { syncPreferencesManager.updateLastSyncTimestamp(any()) } just Runs
        coEvery { firestoreWarrantyDataSource.getWarrantiesUpdatedSince(1000L) } returns listOf(
            remoteWarranty
        )
        coEvery { warrantyDao.getWarrantiesByIds(listOf("1")) } returns emptyList()
        coEvery { warrantyDao.applyRemoteChanges(any(), any()) } just Runs

        val result = repository.syncWarranties()
        assertTrue { result is Result.Success }

        coVerify(atLeast = 1) { syncPreferencesManager.updateLastSyncTimestamp(any()) }
        coVerify(exactly = 1) { warrantyDao.getWarrantiesByIds(listOf("1")) }
        coVerify(exactly = 1) {
            warrantyDao.applyRemoteChanges(
                upsertList = listOf(remoteWarranty.toEntity().copy(syncStatus = SyncStatus.SYNCED)),
                deleteIds = emptyList()
            )
        }
    }

    @Test
    fun pullRemoteChanges_should_delete_local_when_remote_is_deleted() = runTest {
        val remoteWarranty =
            WarrantyDto(id = "1", updatedAt = 1500L, isDeleted = true, currency = "USD")
        val localWarranty = WarrantyEntity(
            id = "1",
            userId = "1",
            updatedAt = 1000L,
            purchaseDate = LocalDate.of(2024, 1, 1),
            expirationDate = LocalDate.of(2026, 1, 1),
            currency = "USD",
            title = "Test"
        )

        coEvery { syncPreferencesManager.getLastSyncTimestamp() } returns 1000L
        coEvery { firestoreWarrantyDataSource.getWarrantiesUpdatedSince(1000L) } returns listOf(
            remoteWarranty
        )
        coEvery { warrantyDao.getWarrantiesByIds(listOf("1")) } returns listOf(localWarranty)
        coEvery { warrantyDao.applyRemoteChanges(any(), any()) } just Runs
        coEvery { warrantyDao.getUnsyncedWarranties() } returns emptyList()

        val result = repository.syncWarranties()
        assertTrue(result is Result.Success)

        coVerify(exactly = 1) {
            warrantyDao.applyRemoteChanges(emptyList(), listOf("1"))
        }
    }

    @Test
    fun pushLocalChanges_should_upload_unsynced_warranties() = runTest {
        val toUpload = listOf(
            WarrantyEntity(
                id = "1",
                userId = "1",
                updatedAt = 900L,
                purchaseDate = LocalDate.of(2024, 1, 1),
                expirationDate = LocalDate.of(2026, 1, 1),
                currency = "USD",
                title = "Test",
                syncStatus = SyncStatus.READY_TO_SYNC,
                isDeleted = false
            )
        )

        coEvery { syncPreferencesManager.getLastSyncTimestamp() } returns 1000L
        coEvery { firestoreWarrantyDataSource.getWarrantiesUpdatedSince(1000L) } returns emptyList()
        coEvery { warrantyDao.getUnsyncedWarranties() } returns toUpload
        coEvery {
            firestoreWarrantyDataSource.pushChangesBatch(
                toUpload.map { it.toDto() },
                any()
            )
        } just Runs
        coEvery {
            warrantyDao.updateSyncStatusForIds(
                toUpload.map { it.id },
                SyncStatus.SYNCED
            )
        } just Runs

        val result = repository.syncWarranties()
        assertTrue { result is Result.Success }

        coVerify(exactly = 1) {
            firestoreWarrantyDataSource.pushChangesBatch(
                match { it.size == 1 && it[0].id == "1" },
                emptyList()
            )
        }
        coVerify(exactly = 1) { warrantyDao.updateSyncStatusForIds(listOf("1"), SyncStatus.SYNCED) }
        coVerify(exactly = 0) { warrantyDao.hardDeleteWarranties(any()) }
    }

    @Test
    fun pushLocalChanges_should_hard_delete_marked_warranty() = runTest {
        val toDelete = listOf(
            WarrantyEntity(
                id = "1",
                userId = "1",
                updatedAt = 900L,
                purchaseDate = LocalDate.of(2024, 1, 1),
                expirationDate = LocalDate.of(2026, 1, 1),
                currency = "USD",
                title = "Test",
                syncStatus = SyncStatus.READY_TO_SYNC,
                isDeleted = true
            )
        )

        coEvery { syncPreferencesManager.getLastSyncTimestamp() } returns 1000L
        coEvery { firestoreWarrantyDataSource.getWarrantiesUpdatedSince(any()) } returns emptyList()
        coEvery { warrantyDao.getUnsyncedWarranties() } returns toDelete
        coEvery { firestoreWarrantyDataSource.pushChangesBatch(any(), any()) } just Runs
        coEvery { warrantyDao.hardDeleteWarranties(toDelete) } just Runs

        val result = repository.syncWarranties()
        assertTrue { result is Result.Success }

        coVerify(exactly = 1) {
            firestoreWarrantyDataSource.pushChangesBatch(emptyList(), listOf("1"))
        }
        coVerify(exactly = 1) {
            warrantyDao.hardDeleteWarranties(
                match { it.size == 1 && it[0].id == "1" }
            )
        }
        coVerify(exactly = 0) { warrantyDao.updateSyncStatusForIds(any(), any()) }
    }

    @Test
    fun syncWarranties_should_propagate_cancellation_exception() = runTest {
        coEvery { syncPreferencesManager.getLastSyncTimestamp() } returns 1000L
        coEvery {
            firestoreWarrantyDataSource.getWarrantiesUpdatedSince(1000L)
        } throws CancellationException()

        assertFailsWith<CancellationException> {
            repository.syncWarranties()
        }

        coVerify(exactly = 0) { syncPreferencesManager.updateLastSyncTimestamp(any()) }
        coVerify(exactly = 0) { warrantyDao.getWarrantiesByIds(any()) }
        coVerify(exactly = 0) { warrantyDao.applyRemoteChanges(any(), any()) }
    }

}