package com.guarantify.data

import android.database.sqlite.SQLiteException
import android.util.Log
import com.guarantify.common.result.Result
import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.database.entity.WarrantyEntity
import com.guarantify.data.mapper.toDomain
import com.guarantify.data.network.firebase.FirebaseWarrantyDataSource
import com.guarantify.data.repository.WarrantiesRepositoryImpl
import com.guarantify.domain.model.Warranty
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.time.LocalDate
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class WarrantiesRepositoryImplTest {
    @MockK
    private lateinit var firebaseDataSource: FirebaseWarrantyDataSource

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
            firebaseWarrantyDataSource = firebaseDataSource,
            warrantyDao = warrantyDao,
            ioDispatcher = testDispatcher
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(Log::class)
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
    fun `createOrUpdateWarranty should save to dao, sync to firebase and update sync status on SUCCESS`() =
        runTest {
            // ARRANGE
            val warranty = createFakeWarranty()

            coEvery { warrantyDao.createOrUpdateWarranty(any()) } just Runs
            coEvery { firebaseDataSource.createOrUpdateWarranty(any()) } just Runs
            coEvery { warrantyDao.updateSyncStatus(any(), any()) } just Runs

            // ACT
            val result = repository.createOrUpdateWarranty(warranty)

            // ASSERT
            assertTrue(result is Result.Success)

            coVerifyOrder {
                warrantyDao.createOrUpdateWarranty(match { !it.isSynced })

                firebaseDataSource.createOrUpdateWarranty(any())

                warrantyDao.updateSyncStatus(id = warranty.id, isSynced = match { true })
            }

            confirmVerified(warrantyDao, firebaseDataSource)
        }

    @Test
    fun `createOrUpdateWarranty should return Error when database fails`() = runTest {
        val warranty = createFakeWarranty()
        val expectedException = SQLiteException("Database error")

        coEvery { warrantyDao.createOrUpdateWarranty(any()) } throws expectedException

        val result = repository.createOrUpdateWarranty(warranty)

        assertTrue(result is Result.Error)
        assertEquals(expectedException, (result as Result.Error).throwable)

        coVerify(exactly = 1) {
            warrantyDao.createOrUpdateWarranty(any())
        }

        confirmVerified(warrantyDao, firebaseDataSource)
    }

    @Test
    fun `createOrUpdateWarranty should return Success even if firebase fails with IOException`() =
        runTest {
            val warranty = createFakeWarranty()

            coEvery { warrantyDao.createOrUpdateWarranty(any()) } just Runs
            coEvery { firebaseDataSource.createOrUpdateWarranty(any()) } throws IOException("No internet")

            val result = repository.createOrUpdateWarranty(warranty)

            assertTrue(result is Result.Success)

            coVerify(exactly = 1) { warrantyDao.createOrUpdateWarranty(any()) }
            coVerify(exactly = 1) { firebaseDataSource.createOrUpdateWarranty(any()) }
            coVerify(exactly = 0) { warrantyDao.updateSyncStatus(any(), any()) }

            confirmVerified(warrantyDao, firebaseDataSource)
        }

    @Test
    fun `createOrUpdateWarranty should succeed when local saves but Firebase fails with Exception`() =
        runTest {
            val warranty = createFakeWarranty()

            coEvery { warrantyDao.createOrUpdateWarranty(any()) } just Runs
            coEvery { firebaseDataSource.createOrUpdateWarranty(any()) } throws Exception("Firebase exception")

            val result = repository.createOrUpdateWarranty(warranty)

            assertIs<Result.Success<Unit>>(result)

            coVerify(exactly = 1) { warrantyDao.createOrUpdateWarranty(any()) }
            coVerify(exactly = 1) { firebaseDataSource.createOrUpdateWarranty(any()) }
            coVerify(exactly = 0) { warrantyDao.updateSyncStatus(any(), any()) }

            confirmVerified(warrantyDao, firebaseDataSource)
        }

    @Test
    fun `createOrUpdateWarranty should save with isSynced FALSE when firebase fails`() = runTest {
        val warranty = createFakeWarranty()

        coEvery { warrantyDao.createOrUpdateWarranty(any()) } just Runs
        coEvery { firebaseDataSource.createOrUpdateWarranty(any()) } throws Exception("No internet")

        repository.createOrUpdateWarranty(warranty)

        coVerify(exactly = 1) {
            warrantyDao.createOrUpdateWarranty(match { entity ->
                !entity.isSynced && entity.title == "Samsung Galaxy S21"
            })
        }
        coVerify(exactly = 1) { firebaseDataSource.createOrUpdateWarranty(any()) }
        coVerify(exactly = 0) { warrantyDao.updateSyncStatus(any(), any()) }

        confirmVerified(warrantyDao, firebaseDataSource)
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
    fun `getWarranty should return Error when database return null`() = runTest {
        val warrantyId = "123"
        val expectedException = Exception("Warranty not found")

        coEvery { warrantyDao.getWarrantyById(warrantyId) } returns null

        val result = repository.getWarranty(warrantyId)

        assertIs<Result.Error>(result)
        assertEquals(expectedException.message, result.throwable.message)

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
    fun `deleteWarranty should delete warranty with dao and firebase`() = runTest {
        // ARRANGE
        val warranty = createFakeWarranty()

        coEvery { warrantyDao.deleteWarranty(any()) } just Runs
        coEvery { firebaseDataSource.deleteWarranty(any()) } just Runs

        // ACT
        repository.deleteWarranty(warranty)

        // ASSERT
        coVerify(exactly = 1) {
            warrantyDao.deleteWarranty(match { it.id == warranty.id })
        }
        coVerify(exactly = 1) {
            firebaseDataSource.deleteWarranty(any())
        }
    }

    @Test
    fun `deleteWarranty should delete locally even if firebase fails`() = runTest {
        val warranty = createFakeWarranty()

        coEvery { warrantyDao.deleteWarranty(any()) } just Runs
        coEvery { firebaseDataSource.deleteWarranty(any()) } throws RuntimeException("No Internet")

        repository.deleteWarranty(warranty)

        coVerify { warrantyDao.deleteWarranty(any()) }
        verify { Log.e(any(), any(), any()) }
    }

}