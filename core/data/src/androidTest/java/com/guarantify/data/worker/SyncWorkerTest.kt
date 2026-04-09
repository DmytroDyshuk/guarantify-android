package com.guarantify.data.worker

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.guarantify.common.result.Result
import com.guarantify.data.workmanager.SyncWorker
import com.guarantify.domain.model.sync.SyncError
import com.guarantify.domain.repository.SyncWarrantiesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import androidx.work.ListenableWorker.Result as WorkResult

@RunWith(AndroidJUnit4::class)
class SyncWorkerTest {
    private val appContext = ApplicationProvider.getApplicationContext<Context>()
    private val syncWarrantiesRepository: SyncWarrantiesRepository = mockk()

    private fun buildWorker(): SyncWorker {
        return TestListenableWorkerBuilder<SyncWorker>(appContext)
            .setWorkerFactory(object : WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ) = SyncWorker(appContext, workerParameters, syncWarrantiesRepository)
            })
            .build()
    }

    @Test
    fun whenSyncSucceeds_returnsSuccess() = runTest {
        coEvery { syncWarrantiesRepository.syncWarranties() } returns Result.Success(Unit)

        val result = buildWorker().doWork()

        assertEquals(WorkResult.success(), result)
    }

    @Test
    fun whenNetworkError_returnsRetry() = runTest {
        coEvery { syncWarrantiesRepository.syncWarranties() } returns
                Result.Error(SyncError.NetworkError())

        val result = buildWorker().doWork()

        assertEquals(WorkResult.retry(), result)
    }

    @Test
    fun whenAuthError_returnsFailure() = runTest {
        coEvery { syncWarrantiesRepository.syncWarranties() } returns
                Result.Error(SyncError.AuthError())

        val result = buildWorker().doWork()

        assertEquals(WorkResult.failure(), result)
    }

    @Test
    fun whenDatabaseError_returnsFailure() = runTest {
        coEvery { syncWarrantiesRepository.syncWarranties() } returns
                Result.Error(SyncError.DatabaseError(SQLiteException()))

        val result = buildWorker().doWork()

        assertEquals(WorkResult.failure(), result)
    }

    @Test
    fun whenUnknownError_returnsRetry() = runTest {
        coEvery { syncWarrantiesRepository.syncWarranties() } returns
                Result.Error(SyncError.UnknownError(RuntimeException()))

        val result = buildWorker().doWork()

        assertEquals(WorkResult.retry(), result)
    }

}