package com.guarantify.app.sync

import com.guarantify.domain.model.auth.AuthState
import com.guarantify.domain.repository.AuthRepository
import com.guarantify.domain.sync.SyncScheduler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SyncInitializerTest {

    private val authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    private val isAppInForeground = MutableStateFlow(false)

    private val authRepository = mockk<AuthRepository> {
        every { observeAuthState() } returns authState
    }
    private val syncScheduler = mockk<SyncScheduler>(relaxed = true)

    private lateinit var testScope: TestScope

    @BeforeEach
    fun setUp() {
        testScope = TestScope(UnconfinedTestDispatcher())
        SyncInitializer(authRepository, syncScheduler, testScope.backgroundScope)
            .start(isAppInForeground)
    }

    @Test
    fun `app opened by signed-in user schedules periodic and immediate sync`() = testScope.runTest {
        authState.value = AuthState.Authenticated
        isAppInForeground.value = true

        verify(exactly = 1) { syncScheduler.schedulePeriodicSync() }
        verify(exactly = 1) { syncScheduler.requestImmediateSync() }
    }

    @Test
    fun `background process started by WorkManager does not request immediate sync`() = testScope.runTest {
        authState.value = AuthState.Authenticated

        verify(exactly = 1) { syncScheduler.schedulePeriodicSync() }
        verify(exactly = 0) { syncScheduler.requestImmediateSync() }
    }

    @Test
    fun `returning to foreground requests immediate sync again`() = testScope.runTest {
        authState.value = AuthState.Authenticated
        isAppInForeground.value = true
        isAppInForeground.value = false
        isAppInForeground.value = true

        verify(exactly = 2) { syncScheduler.requestImmediateSync() }
        verify(exactly = 1) { syncScheduler.schedulePeriodicSync() }
    }

    @Test
    fun `signed-out user gets no sync`() = testScope.runTest {
        authState.value = AuthState.Unauthenticated
        isAppInForeground.value = true

        verify(exactly = 0) { syncScheduler.schedulePeriodicSync() }
        verify(exactly = 0) { syncScheduler.requestImmediateSync() }
    }

    @Test
    fun `signing in to another account while app is open reschedules sync`() = testScope.runTest {
        isAppInForeground.value = true
        authState.value = AuthState.Authenticated
        authState.value = AuthState.Unauthenticated
        authState.value = AuthState.Authenticated

        verify(exactly = 2) { syncScheduler.schedulePeriodicSync() }
        verify(exactly = 2) { syncScheduler.requestImmediateSync() }
    }

}
