package com.guarantify.data.repository

import android.database.sqlite.SQLiteException
import android.net.Uri
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.guarantify.common.result.Result
import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.database.entity.WarrantyEntity
import com.guarantify.data.mapper.toDomain
import com.guarantify.data.network.firebase.storage.WarrantyPhotoStorage
import com.guarantify.domain.model.DatabaseError
import com.guarantify.domain.model.StorageError
import com.guarantify.domain.model.Warranty
import com.guarantify.domain.model.sync.SyncStatus
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class WarrantiesRepositoryTest {
    @MockK
    private lateinit var warrantyPhotoStorage: WarrantyPhotoStorage

    @MockK(relaxed = true)
    private lateinit var workManager: WorkManager

    @MockK(relaxed = true)
    private lateinit var warrantyDao: WarrantyDao
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: WarrantiesRepositoryImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        repository = WarrantiesRepositoryImpl(
            warrantyDao = warrantyDao,
            warrantyPhotoStorage = warrantyPhotoStorage,
            workManager = workManager,
            ioDispatcher = testDispatcher
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(Log::class)
        unmockkStatic(Uri::class)
    }

    private fun createFakeWarranty(
        id: String = "1",
        userId: String = "user123",
        name: String = "Samsung Galaxy S21"
    ) = Warranty(
        id = id,
        userId = userId,
        productName = name,
        purchaseDate = LocalDate.now(),
        expirationDate = LocalDate.now(),
        storeName = "Samsung",
        currency = "USD"
    )


    @Test
    fun latestWarranties_should_emit_mapped_domain_models() = runTest {
        // ARRANGE
        val entity1 = WarrantyEntity(
            id = "1",
            userId = "1",
            title = "Samsung Galaxy S21",
            purchaseDate = LocalDate.now(),
            expirationDate = LocalDate.now(),
            storeName = "Samsung"
        )
        val entity2 = WarrantyEntity(
            id = "2",
            userId = "2",
            title = "Samsung Galaxy S22",
            purchaseDate = LocalDate.now(),
            expirationDate = LocalDate.now(),
            storeName = "Samsung"
        )
        val entities = listOf(entity1, entity2)

        every { warrantyDao.getAllWarranties() } returns flowOf(entities)

        // ACT
        val resultList = repository.latestWarranties.first()

        // ASSERT
        Assertions.assertEquals(2, resultList.size)
        Assertions.assertEquals("Samsung Galaxy S21", resultList[0].productName)
        Assertions.assertEquals("2", resultList[1].userId)
    }

    @Test
    fun latestWarranties_should_emit_empty_list_when_dao_fails() = runTest {
        // ARRANGE
        every { warrantyDao.getAllWarranties() } returns flow { throw Exception("DB error") }

        // ACT
        val resultList = repository.latestWarranties.first()

        // ASSERT
        Assertions.assertTrue(resultList.isEmpty())
        verify { warrantyDao.getAllWarranties() }
    }

    @Test
    fun createOrUpdateWarranty_should_save_to_dao_and_start_upload_chain_when_photo_is_pending() =
        runTest {
            // ARRANGE
            val warranty = createFakeWarranty()
            val savedEntity = WarrantyEntity(
                id = warranty.id,
                userId = warranty.userId,
                title = warranty.productName,
                purchaseDate = warranty.purchaseDate,
                expirationDate = warranty.expirationDate,
                storeName = warranty.storeName,
                localPhotoUri = "file://photo.jpg",
                syncStatus = SyncStatus.PENDING
            )

            coEvery { warrantyDao.upsertWithPhotoLogic(any()) } returns savedEntity

            // ACT
            val result = repository.createOrUpdateWarranty(warranty)

            // ASSERT
            Assertions.assertTrue(result is Result.Success)

            coVerify(exactly = 1) { warrantyDao.upsertWithPhotoLogic(any()) }
            verify { workManager.beginWith(any<OneTimeWorkRequest>()) }
            confirmVerified(warrantyDao)
        }

    @Test
    fun createOrUpdateWarranty_should_return_Error_when_database_fails() = runTest {
        val warranty = createFakeWarranty()
        val expectedException = SQLiteException("Database error")

        coEvery { warrantyDao.upsertWithPhotoLogic(any()) } throws expectedException

        val result = repository.createOrUpdateWarranty(warranty)

        Assertions.assertTrue(result is Result.Error)
        Assertions.assertEquals(expectedException, (result as Result.Error).throwable)

        coVerify(exactly = 1) {
            warrantyDao.upsertWithPhotoLogic(any())
        }

        confirmVerified(warrantyDao)
    }

    @Test
    fun createOrUpdateWarranty_should_save_to_dao_without_starting_upload_when_no_photo() =
        runTest {
            val warranty = createFakeWarranty()
            val savedEntity = WarrantyEntity(
                id = warranty.id,
                userId = warranty.userId,
                title = warranty.productName,
                purchaseDate = warranty.purchaseDate,
                expirationDate = warranty.expirationDate,
                storeName = warranty.storeName,
                localPhotoUri = null,
                syncStatus = SyncStatus.READY_TO_SYNC
            )

            coEvery { warrantyDao.upsertWithPhotoLogic(any()) } returns savedEntity

            val result = repository.createOrUpdateWarranty(warranty)

            Assertions.assertTrue(result is Result.Success)
            coVerify(exactly = 1) { warrantyDao.upsertWithPhotoLogic(any()) }
            confirmVerified(warrantyDao)
        }

    @Test
    fun createOrUpdateWarranty_should_save_with_READY_TO_SYNC_when_photo_unchanged() =
        runTest {
            val warranty = createFakeWarranty()
            val savedEntity = WarrantyEntity(
                id = warranty.id,
                userId = warranty.userId,
                title = warranty.productName,
                purchaseDate = warranty.purchaseDate,
                expirationDate = warranty.expirationDate,
                storeName = warranty.storeName,
                localPhotoUri = "file://existing_photo.jpg",
                syncStatus = SyncStatus.READY_TO_SYNC
            )

            coEvery { warrantyDao.upsertWithPhotoLogic(any()) } returns savedEntity

            val result = repository.createOrUpdateWarranty(warranty)

            Assertions.assertTrue(result is Result.Success)
            coVerify(exactly = 1) { warrantyDao.upsertWithPhotoLogic(any()) }
            confirmVerified(warrantyDao)
        }

    @Test
    fun getWarranty_should_return_Success_with_mapped_data_when_DAO_returns_entity() = runTest {
        val warrantyId = "23"
        val warrantyEntity = WarrantyEntity(
            id = warrantyId,
            userId = "1",
            title = "Samsung Galaxy S21",
            purchaseDate = LocalDate.now(),
            expirationDate = LocalDate.now(),
            storeName = "Samsung"
        )
        val expectedDomain = warrantyEntity.toDomain()

        coEvery { warrantyDao.getWarrantyById(warrantyId) } returns warrantyEntity

        val result = repository.getWarranty(warrantyId)

        assertIs<Result.Success<Warranty>>(result)
        Assertions.assertEquals(expectedDomain, result.data)

        coVerify(exactly = 1) { warrantyDao.getWarrantyById(warrantyId) }

        confirmVerified(warrantyDao)
    }

    @Test
    fun getWarranty_should_return_Error_with_NotFound_when_database_return_null() = runTest {
        val warrantyId = "123"

        coEvery { warrantyDao.getWarrantyById(warrantyId) } returns null

        val result = repository.getWarranty(warrantyId)

        assertIs<Result.Error>(result)
        assertIs<DatabaseError.NotFound>(result.throwable)

        coVerify(exactly = 1) { warrantyDao.getWarrantyById(warrantyId) }

        confirmVerified(warrantyDao)
    }

    @Test
    fun getWarranty_should_return_Error_when_database_fails() = runTest {
        val sqliteException = SQLiteException()

        coEvery { warrantyDao.getWarrantyById(any()) } throws sqliteException

        val result = repository.getWarranty("123")

        assertIs<Result.Error>(result)
        Assertions.assertEquals(sqliteException, result.throwable)

        coVerify(exactly = 1) { warrantyDao.getWarrantyById(any()) }

        confirmVerified(warrantyDao)
    }

    @Test
    fun deleteWarranty_should_soft_delete_warranty_with_dao() = runTest {
        // ARRANGE
        val warranty = createFakeWarranty()

        coEvery { warrantyDao.softDeleteWarranty(any(), any(), any()) } just Runs

        // ACT
        repository.deleteWarranty(warranty)

        // ASSERT
        coVerify(exactly = 1) {
            warrantyDao.softDeleteWarranty(
                id = warranty.id,
                updatedAt = any(),
                syncStatus = SyncStatus.PENDING
            )
        }
        confirmVerified(warrantyDao)
    }

    @Test
    fun deleteWarranty_should_handle_database_errors_gracefully() = runTest {
        val warranty = createFakeWarranty()
        val exception = SQLiteException("DB error")

        coEvery { warrantyDao.softDeleteWarranty(any(), any(), any()) } throws exception

        val result = repository.deleteWarranty(warranty)

        assertIs<Result.Error>(result)
        Assertions.assertEquals(exception, result.throwable)
        coVerify { warrantyDao.softDeleteWarranty(any(), any(), any()) }
        confirmVerified(warrantyDao)
    }

    @Test
    fun uploadWarrantyPhoto_should_return_Success_when_storage_succeeds() = runTest {
        val warrantyId = "1"
        val photoUri = "file://test.jpg"
        val expectedUrl = "https://firebasestorage.com/1.jpg"
        val mockUri = mockk<Uri>()

        mockkStatic(Uri::class)
        every { Uri.parse(photoUri) } returns mockUri

        coEvery { warrantyPhotoStorage.uploadImage(warrantyId, mockUri) } returns Result.Success(
            expectedUrl
        )

        val result = repository.uploadWarrantyPhoto(warrantyId, photoUri)

        assertIs<Result.Success<String>>(result)
        Assertions.assertEquals(expectedUrl, result.data)

        unmockkStatic(Uri::class)
    }

    @Test
    fun uploadWarrantyPhoto_should_return_Error_when_storage_fails() = runTest {
        val exception = Exception("Upload failed")
        coEvery { warrantyPhotoStorage.uploadImage(any(), any()) } throws exception

        val result = repository.uploadWarrantyPhoto("1", "uri")

        assertIs<Result.Error>(result)
        assertIs<StorageError.Unknown>(result.throwable)
    }

    @Test
    fun updateRemoteUrlPhotoLocally_should_update_dao_and_return_success() = runTest {
        val warrantyId = "1"
        val photoUrl = "https://example.com/photo.jpg"
        val existingEntity = WarrantyEntity(
            id = warrantyId,
            userId = "u1",
            title = "T",
            purchaseDate = LocalDate.now(),
            expirationDate = LocalDate.now(),
            storeName = "S"
        )

        coEvery { warrantyDao.getWarrantyById(warrantyId) } returns existingEntity
        coEvery { warrantyDao.createOrUpdateWarranty(any()) } just Runs

        val result = repository.updateRemoteUrlPhotoLocally(warrantyId, photoUrl)

        assertIs<Result.Success<Unit>>(result)
        coVerify {
            warrantyDao.createOrUpdateWarranty(match {
                it.id == warrantyId && it.remotePhotoUrl == photoUrl && it.syncStatus == SyncStatus.READY_TO_SYNC
            })
        }
    }

    @Test
    fun updateRemoteUrlPhotoLocally_should_return_Error_when_warranty_not_found() = runTest {
        coEvery { warrantyDao.getWarrantyById(any()) } returns null

        val result = repository.updateRemoteUrlPhotoLocally("1", "url")

        assertIs<Result.Error>(result)
        assertIs<DatabaseError.NotFound>(result.throwable)
    }

}