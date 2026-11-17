package com.guarantify.data.repository

import android.content.Context
import android.database.sqlite.SQLiteException
import com.guarantify.common.result.Result
import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.photo.PhotoStorageImpl
import com.guarantify.data.workmanager.WarrantyWork
import com.guarantify.domain.model.DatabaseError
import com.guarantify.domain.preferences.SyncPreferencesManager
import com.guarantify.domain.sync.SyncScheduler
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class LocalDataRepositoryTest {

    @MockK
    private lateinit var context: Context

    @MockK(relaxed = true)
    private lateinit var syncScheduler: SyncScheduler

    @MockK(relaxed = true)
    private lateinit var warrantyDao: WarrantyDao

    @MockK(relaxed = true)
    private lateinit var syncPreferencesManager: SyncPreferencesManager

    @TempDir
    lateinit var cacheDir: File

    @TempDir
    lateinit var filesDir: File

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: LocalDataRepositoryImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { context.cacheDir } returns cacheDir
        every { context.filesDir } returns filesDir

        repository = LocalDataRepositoryImpl(
            context = context,
            syncScheduler = syncScheduler,
            warrantyDao = warrantyDao,
            syncPreferencesManager = syncPreferencesManager,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun `clearLocalData cancels work before deleting warranties and sync state`() = runTest(testDispatcher) {
        val result = repository.clearLocalData()

        assertIs<Result.Success<Unit>>(result)
        coVerifyOrder {
            syncScheduler.cancelAllWork()
            warrantyDao.deleteAllWarranties()
            syncPreferencesManager.clear()
        }
    }

    @Test
    fun `clearLocalData deletes compressed photos and keeps other cache files`() = runTest(testDispatcher) {
        val compressedPhoto = File(cacheDir, "${WarrantyWork.COMPRESSED_PHOTO_PREFIX}42.jpg").apply { writeText("x") }
        val otherFile = File(cacheDir, "image_cache.bin").apply { writeText("x") }

        repository.clearLocalData()

        Assertions.assertFalse(compressedPhoto.exists())
        Assertions.assertTrue(otherFile.exists())
    }

    @Test
    fun `clearLocalData deletes local photos and keeps other files`() = runTest(testDispatcher) {
        val photosDir = File(filesDir, PhotoStorageImpl.PHOTOS_DIR).apply { mkdirs() }
        val photo = File(photosDir, "photo.jpg").apply { writeText("x") }
        val otherFile = File(filesDir, "datastore.preferences_pb").apply { writeText("x") }

        repository.clearLocalData()

        Assertions.assertFalse(photo.exists())
        Assertions.assertFalse(photosDir.exists())
        Assertions.assertTrue(otherFile.exists())
    }

    @Test
    fun `clearLocalData returns DatabaseError when deleting warranties fails`() = runTest(testDispatcher) {
        val exception = mockk<SQLiteException>()
        coEvery { warrantyDao.deleteAllWarranties() } throws exception

        val result = repository.clearLocalData()

        assertIs<Result.Error>(result)
        val error = assertIs<DatabaseError.Unknown>(result.throwable)
        Assertions.assertEquals(exception, error.e)
        coVerify(exactly = 0) { syncPreferencesManager.clear() }
    }

}
