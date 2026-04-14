package com.guarantify.data.repository

import android.database.sqlite.SQLiteException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.guarantify.common.result.Result
import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.database.entity.WarrantyEntity
import com.guarantify.data.network.dto.WarrantyDto
import com.guarantify.data.network.firebase.firestore.FirestoreWarrantyDataSource
import com.guarantify.domain.model.auth.AuthRequiredException
import com.guarantify.domain.model.sync.SyncError
import com.guarantify.domain.model.sync.SyncStatus
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SyncWarrantiesRepositoryTest {
    val testDispatcher = UnconfinedTestDispatcher()

    @MockK(relaxed = true)
    private lateinit var warrantyDao: WarrantyDao

    @MockK(relaxed = true)
    private lateinit var firestoreWarrantyDataSource: FirestoreWarrantyDataSource

    private lateinit var repository: SyncWarrantiesRepositoryImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        repository = SyncWarrantiesRepositoryImpl(
            warrantyDao = warrantyDao,
            firestoreWarrantyDataSource = firestoreWarrantyDataSource,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun pullRemoteChanges_should_update_local_warranty_when_remote_is_newer() = runTest {
        coEvery { warrantyDao.getLastUpdatedTimestamp() } returns 1000L

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

        coEvery {
            firestoreWarrantyDataSource.getWarrantiesUpdatedSince(1000L)
        } returns listOf(remoteWarranty)

        coEvery { warrantyDao.getWarrantyById("1") } returns localWarranty
        coEvery { warrantyDao.createOrUpdateWarranty(any()) } just Runs
        coEvery { warrantyDao.getUnsyncedWarranties() } returns emptyList()

        val result = repository.syncWarranties()

        assertTrue(result is Result.Success)

        coVerify(exactly = 1) { warrantyDao.createOrUpdateWarranty(any()) }

        coVerify {
            warrantyDao.createOrUpdateWarranty(
                match { entity ->
                    entity.id == "1" && entity.syncStatus == SyncStatus.SYNCED
                }
            )
        }
    }

    @Test
    fun pullRemoteChanges_local_entity_should_not_be_updated_when_remote_older_than_local() = runTest {
        coEvery { warrantyDao.getLastUpdatedTimestamp() } returns 1000L

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

        coEvery {
            firestoreWarrantyDataSource.getWarrantiesUpdatedSince(1000L)
        } returns listOf(remoteWarranty)

        coEvery { warrantyDao.getWarrantyById("1") } returns localWarranty
        coEvery { warrantyDao.createOrUpdateWarranty(any()) } just Runs
        coEvery { warrantyDao.getUnsyncedWarranties() } returns emptyList()

        val result = repository.syncWarranties()

        assertTrue(result is Result.Success)

        coVerify(exactly = 0) { warrantyDao.createOrUpdateWarranty(any()) }
    }

    @Test
    fun syncWarranties_should_return_result_success_when_sync_succeed() = runTest {
        coEvery { warrantyDao.getLastUpdatedTimestamp() } returns 1000L

        val listWarrantiesDto = listOf(
            WarrantyDto(
                id = "1",
                updatedAt = 1000L,
                currency = "USD",
                purchaseDate = "2024-01-01",
                expirationDate = "2026-01-01"
            )
        )

        coEvery {
            firestoreWarrantyDataSource.getWarrantiesUpdatedSince(1000L)
        } returns listWarrantiesDto

        coEvery { warrantyDao.getWarrantyById(any()) } returns null

        coEvery { warrantyDao.createOrUpdateWarranty(any()) } just Runs

        coEvery { warrantyDao.getUnsyncedWarranties() } returns emptyList()

        val result = repository.syncWarranties()
        assertTrue { result is Result.Success }
    }

    @Test
    fun syncWarranties_should_return_network_error_when_firestore_fails() = runTest {
        val mockFirebaseException = mockk<FirebaseFirestoreException>()

        coEvery { warrantyDao.getLastUpdatedTimestamp() } returns 0L

        coEvery {
            firestoreWarrantyDataSource.getWarrantiesUpdatedSince(0L)
        } throws mockFirebaseException

        val result = repository.syncWarranties()

        assertTrue { result is Result.Error }

        val error = (result as Result.Error).throwable
        assertTrue { error is SyncError.NetworkError }
    }

    @Test
    fun syncWarranties_should_return_auth_error_when_user_not_authenticated() = runTest {
        val mockAuthException = mockk<AuthRequiredException>()

        coEvery { warrantyDao.getLastUpdatedTimestamp() } returns 0L

        coEvery {
            firestoreWarrantyDataSource.getWarrantiesUpdatedSince(0L)
        } throws mockAuthException

        val result = repository.syncWarranties()

        assertTrue { result is Result.Error }

        val error = (result as Result.Error).throwable
        assertTrue { error is SyncError.AuthError }
    }

    @Test
    fun syncWarranties_should_return_SQLite_exception_when_local_db_fails() = runTest {
        val mockSQLiteException = mockk<SQLiteException>()

        coEvery { warrantyDao.getLastUpdatedTimestamp() } throws mockSQLiteException

        coEvery {
            firestoreWarrantyDataSource.getWarrantiesUpdatedSince(any())
        } returns emptyList()

        val result = repository.syncWarranties()

        assertTrue { result is Result.Error }

        val error = (result as Result.Error).throwable
        assertTrue { error is SyncError.DatabaseError }
    }

}