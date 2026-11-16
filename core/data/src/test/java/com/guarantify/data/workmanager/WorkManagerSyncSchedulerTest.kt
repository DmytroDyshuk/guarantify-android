package com.guarantify.data.workmanager

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.Operation
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.common.util.concurrent.ListenableFuture
import io.mockk.MockKAnnotations
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WorkManagerSyncSchedulerTest {

    @MockK(relaxed = true)
    private lateinit var workManager: WorkManager

    private lateinit var scheduler: WorkManagerSyncScheduler

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { workManager.cancelAllWorkByTag(any()) } returns completedOperation()
        every { workManager.cancelUniqueWork(any()) } returns completedOperation()

        scheduler = WorkManagerSyncScheduler(workManager)
    }

    private fun completedOperation(): Operation {
        val future = mockk<ListenableFuture<Operation.State.SUCCESS>> {
            every { isDone } returns true
            every { get() } returns Operation.SUCCESS
        }
        return mockk { every { result } returns future }
    }

    @Test
    fun `schedulePeriodicSync keeps existing tagged work and requires network`() {
        val request = slot<PeriodicWorkRequest>()

        scheduler.schedulePeriodicSync()

        verify {
            workManager.enqueueUniquePeriodicWork(
                WarrantyWork.PERIODIC_SYNC_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                capture(request)
            )
        }
        Assertions.assertTrue(WarrantyWork.TAG in request.captured.tags)
        Assertions.assertEquals(NetworkType.CONNECTED, request.captured.workSpec.constraints.requiredNetworkType)
    }

    @Test
    fun `requestImmediateSync keeps pending sync instead of replacing it`() {
        val request = slot<OneTimeWorkRequest>()

        scheduler.requestImmediateSync()

        verify {
            workManager.enqueueUniqueWork(
                WarrantyWork.IMMEDIATE_SYNC_NAME,
                ExistingWorkPolicy.KEEP,
                capture(request)
            )
        }
        Assertions.assertTrue(WarrantyWork.TAG in request.captured.tags)
        Assertions.assertEquals(NetworkType.CONNECTED, request.captured.workSpec.constraints.requiredNetworkType)
    }

    @Test
    fun `cancelAllWork cancels tagged work and untagged sync from older versions`() = runTest {
        scheduler.cancelAllWork()

        coVerifyOrder {
            workManager.cancelAllWorkByTag(WarrantyWork.TAG)
            workManager.cancelUniqueWork(WarrantyWork.PERIODIC_SYNC_NAME)
            workManager.cancelUniqueWork(WarrantyWork.IMMEDIATE_SYNC_NAME)
        }
    }

}
