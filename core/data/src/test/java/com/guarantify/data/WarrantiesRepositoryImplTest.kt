package com.guarantify.data

import android.util.Log
import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.database.entity.WarrantyEntity
import com.guarantify.data.network.firebase.FirebaseWarrantyDataSource
import com.guarantify.data.repository.WarrantiesRepositoryImpl
import com.guarantify.domain.model.Result
import com.guarantify.domain.model.Warranty
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
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
import java.time.LocalDate

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
            val warranty = Warranty(
                id = "1",
                userId = "1",
                productName = "Samsung Galaxy S21",
                purchaseDate = LocalDate.now(),
                expirationDate = LocalDate.now(),
                storeName = "Samsung",
                currency = "USD"
            )

            coEvery { warrantyDao.createOrUpdateWarranty(any()) } just Runs
            coEvery { firebaseDataSource.createOrUpdateWarranty(any()) } just Runs

            // ACT
            val result = repository.createOrUpdateWarranty(warranty)

            // ASSERT
            assertTrue(result is Result.Success)

            coVerifyOrder {
                warrantyDao.createOrUpdateWarranty(match { !it.isSynced })

                firebaseDataSource.createOrUpdateWarranty(any())

                warrantyDao.createOrUpdateWarranty(match { it.isSynced })
            }
        }

    @Test
    fun `createOrUpdateWarranty should return Error when firebase fails`() = runTest {
        val warranty = Warranty(
            id = "1",
            userId = "1",
            productName = "Samsung Galaxy S21",
            purchaseDate = LocalDate.now(),
            expirationDate = LocalDate.now(),
            storeName = "Samsung",
            currency = "USD"
        )
        val expectedError = "Network error"

        coEvery { warrantyDao.createOrUpdateWarranty(any()) } just Runs
        coEvery { firebaseDataSource.createOrUpdateWarranty(any()) } throws RuntimeException(
            expectedError
        )

        val result = repository.createOrUpdateWarranty(warranty)

        assertTrue(result is Result.Error)

        coVerify(exactly = 1) {
            warrantyDao.createOrUpdateWarranty(any())
        }

        coVerify(exactly = 0) {
            warrantyDao.createOrUpdateWarranty(match { it.isSynced })
        }
    }

    @Test
    fun `deleteWarranty should delete warranty with dao and firebase`() = runTest {
        // ARRANGE
        val warranty = Warranty(
            id = "1",
            userId = "1",
            productName = "Samsung Galaxy S21",
            purchaseDate = LocalDate.now(),
            expirationDate = LocalDate.now(),
            storeName = "Samsung",
            currency = "USD"
        )

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
        val warranty = Warranty(
            id = "1",
            userId = "1",
            productName = "Samsung Galaxy S21",
            purchaseDate = LocalDate.now(),
            expirationDate = LocalDate.now(),
            storeName = "Samsung",
            currency = "USD"
        )

        coEvery { warrantyDao.deleteWarranty(any()) } just Runs
        coEvery { firebaseDataSource.deleteWarranty(any()) } throws RuntimeException("No Internet")

        repository.deleteWarranty(warranty)

        coVerify { warrantyDao.deleteWarranty(any()) }
        verify { Log.e(any(), any(), any()) }
    }

}