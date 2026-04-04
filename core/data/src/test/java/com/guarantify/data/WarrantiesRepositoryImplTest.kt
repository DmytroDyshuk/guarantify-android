package com.guarantify.data

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
import com.guarantify.data.repository.WarrantiesRepositoryImpl
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class WarrantiesRepositoryImplTest {
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
    fun `latestWarranties should emit mapped domain models`() = runTest {
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
        assertEquals(2, resultList.size)
        assertEquals("Samsung Galaxy S21", resultList[0].productName)
        assertEquals("2", resultList[1].userId)
    }

    @Test
    fun `latestWarranties should emit empty list when dao fails`() = runTest {
        // ARRANGE
        every { warrantyDao.getAllWarranties() } returns flow { throw Exception("DB error") }

        // ACT
        val resultList = repository.latestWarranties.first()

        // ASSERT
        assertTrue(resultList.isEmpty())
        verify { warrantyDao.getAllWarranties() }
    }

    @Test
    fun `createOrUpdateWarranty should save to dao and start upload chain when photo is pending`() =
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
            assertTrue(result is Result.Success)

            coVerify(exactly = 1) { warrantyDao.upsertWithPhotoLogic(any()) }
            verify { workManager.beginWith(any<OneTimeWorkRequest>()) }
            confirmVerified(warrantyDao)
        }

    @Test
    fun `createOrUpdateWarranty should return Error when database fails`() = runTest {
        val warranty = createFakeWarranty()
        val expectedException = SQLiteException("Database error")

        coEvery { warrantyDao.upsertWithPhotoLogic(any()) } throws expectedException

        val result = repository.createOrUpdateWarranty(warranty)

        assertTrue(result is Result.Error)
        assertEquals(expectedException, (result as Result.Error).throwable)

        coVerify(exactly = 1) {
            warrantyDao.upsertWithPhotoLogic(any())
        }

        confirmVerified(warrantyDao)
    }

    @Test
    fun `createOrUpdateWarranty should save to dao without starting upload when no photo`() =
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

            assertTrue(result is Result.Success)
            coVerify(exactly = 1) { warrantyDao.upsertWithPhotoLogic(any()) }
            confirmVerified(warrantyDao)
        }

    @Test
    fun `createOrUpdateWarranty should save with READY_TO_SYNC when photo unchanged`() =
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

            assertTrue(result is Result.Success)
            coVerify(exactly = 1) { warrantyDao.upsertWithPhotoLogic(any()) }
            confirmVerified(warrantyDao)
        }

    @Test
    fun `getWarranty should return Success with mapped data when DAO returns entity`() = runTest {
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
        assertEquals(expectedDomain, result.data)

        coVerify(exactly = 1) { warrantyDao.getWarrantyById(warrantyId) }

        confirmVerified(warrantyDao)
    }

    @Test
    fun `getWarranty should return Error with NotFound when database return null`() = runTest {
        val warrantyId = "123"

        coEvery { warrantyDao.getWarrantyById(warrantyId) } returns null

        val result = repository.getWarranty(warrantyId)

        assertIs<Result.Error>(result)
        assertIs<DatabaseError.NotFound>(result.throwable)

        coVerify(exactly = 1) { warrantyDao.getWarrantyById(warrantyId) }

        confirmVerified(warrantyDao)
    }

    @Test
    fun `getWarranty should return Error when database fails`() = runTest {
        val sqliteException = SQLiteException()

        coEvery { warrantyDao.getWarrantyById(any()) } throws sqliteException

        val result = repository.getWarranty("123")

        assertIs<Result.Error>(result)
        assertEquals(sqliteException, result.throwable)

        coVerify(exactly = 1) { warrantyDao.getWarrantyById(any()) }

        confirmVerified(warrantyDao)
    }

    @Test
    fun `deleteWarranty should soft delete warranty with dao`() = runTest {
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
    fun `deleteWarranty should handle database errors gracefully`() = runTest {
        val warranty = createFakeWarranty()
        val exception = SQLiteException("DB error")

        coEvery { warrantyDao.softDeleteWarranty(any(), any(), any()) } throws exception

        val result = repository.deleteWarranty(warranty)

        assertIs<Result.Error>(result)
        assertEquals(exception, result.throwable)
        coVerify { warrantyDao.softDeleteWarranty(any(), any(), any()) }
        confirmVerified(warrantyDao)
    }

    @Test
    fun `uploadWarrantyPhoto should return Success when storage succeeds`() = runTest {
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
        assertEquals(expectedUrl, result.data)

        unmockkStatic(Uri::class)
    }

    @Test
    fun `uploadWarrantyPhoto should return Error when storage fails`() = runTest {
        val exception = Exception("Upload failed")
        coEvery { warrantyPhotoStorage.uploadImage(any(), any()) } throws exception

        val result = repository.uploadWarrantyPhoto("1", "uri")

        assertIs<Result.Error>(result)
        assertIs<StorageError.Unknown>(result.throwable)
    }

    @Test
    fun `updateRemoteUrlPhotoLocally should update dao and return success`() = runTest {
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
    fun `updateRemoteUrlPhotoLocally should return Error when warranty not found`() = runTest {
        coEvery { warrantyDao.getWarrantyById(any()) } returns null

        val result = repository.updateRemoteUrlPhotoLocally("1", "url")

        assertIs<Result.Error>(result)
        assertIs<DatabaseError.NotFound>(result.throwable)
    }

}