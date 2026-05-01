package com.guarantify.app

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestDriver
import androidx.work.testing.WorkManagerTestInitHelper
import com.guarantify.common.result.Result
import com.guarantify.data.workmanager.SyncWorker
import com.guarantify.domain.model.sync.SyncError
import com.guarantify.domain.repository.SyncWarrantiesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class SyncWorkerWorkManagerTest {

    private lateinit var context: Context
    private lateinit var workManager: WorkManager
    private lateinit var testDriver: TestDriver

    private val syncRepo = mockk<SyncWarrantiesRepository>()

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()

        val config = Configuration.Builder()
            .setWorkerFactory(object : WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker {
                    return SyncWorker(appContext, workerParameters, syncRepo)
                }
            })
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        workManager = WorkManager.getInstance(context)
        testDriver = requireNotNull(
            WorkManagerTestInitHelper.getTestDriver(context)
        ) { "TestDriver is null. Did you initialize WorkManagerTestInitHelper?" }
    }

    private fun WorkManager.awaitFinished(id: UUID): WorkInfo {
        repeat(100) {
            val info = requireNotNull(getWorkInfoById(id).get()) {
                "WorkInfo is null for id=$id"
            }

            if (info.state.isFinished) return info
        }

        error("Timeout waiting for finish")
    }

    private fun WorkManager.awaitRetry(id: UUID): WorkInfo {
        repeat(100) {
            val info = getWorkInfoById(id).get()
            if (info?.state == WorkInfo.State.ENQUEUED && info.runAttemptCount > 0) {
                return info
            }
            Thread.sleep(50)
        }

        error("Timeout waiting for retry")
    }

    @Test
    fun test_sync_work_success() = runTest {
        coEvery { syncRepo.syncWarranties() } returns Result.Success(Unit)

        val request = OneTimeWorkRequestBuilder<SyncWorker>().build()

        workManager.enqueue(request).result.get()

        testDriver.setAllConstraintsMet(request.id)

        val workInfo = workManager.awaitFinished(request.id)

        assertEquals(WorkInfo.State.SUCCEEDED, workInfo.state)

        coVerify { syncRepo.syncWarranties() }
    }

    @Test
    fun test_sync_work_database_error_should_failure() = runTest {
        val databaseException = mockk<SQLiteException>()
        coEvery { syncRepo.syncWarranties() } returns Result.Error(
            SyncError.DatabaseError(databaseException)
        )

        val request = OneTimeWorkRequestBuilder<SyncWorker>().build()

        workManager.enqueue(request).result.get()

        testDriver.setAllConstraintsMet(request.id)

        val workInfo = workManager.awaitFinished(request.id)

        assertEquals(WorkInfo.State.FAILED, workInfo.state)

        coVerify { syncRepo.syncWarranties() }
    }

    @Test
    fun test_sync_work_network_error_should_retry() = runTest {
        coEvery { syncRepo.syncWarranties() } returns Result.Error(SyncError.NetworkError())

        val request = OneTimeWorkRequestBuilder<SyncWorker>().build()

        workManager.enqueue(request).result.get()

        testDriver.setAllConstraintsMet(request.id)

        val workInfo = workManager.awaitRetry(request.id)

        assertEquals(WorkInfo.State.ENQUEUED, workInfo.state)

        coVerify { syncRepo.syncWarranties() }
    }

    @Test
    fun test_sync_work_auth_error_should_failure() = runTest {
        coEvery { syncRepo.syncWarranties() } returns Result.Error(SyncError.AuthError())

        val request = OneTimeWorkRequestBuilder<SyncWorker>().build()

        workManager.enqueue(request).result.get()

        testDriver.setAllConstraintsMet(request.id)

        val workInfo = workManager.awaitFinished(request.id)

        assertEquals(WorkInfo.State.FAILED, workInfo.state)

        coVerify { syncRepo.syncWarranties() }
    }

}