package com.guarantify.app.sync

import com.guarantify.common.di.ApplicationScope
import com.guarantify.domain.model.auth.AuthState
import com.guarantify.domain.repository.AuthRepository
import com.guarantify.domain.sync.SyncScheduler
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@Singleton
class SyncInitializer @Inject constructor(
    private val authRepository: AuthRepository,
    private val syncScheduler: SyncScheduler,
    @param:ApplicationScope private val appScope: CoroutineScope
) {

    fun start(isAppInForeground: Flow<Boolean>) {
        val isSignedIn = authRepository.observeAuthState()
            .map { it is AuthState.Authenticated }
            .distinctUntilChanged()
            .onEach { signedIn ->
                if (signedIn) syncScheduler.schedulePeriodicSync()
            }

        combine(
            isSignedIn,
            isAppInForeground
        ) { signedIn, inForeground -> signedIn && inForeground }
            .distinctUntilChanged()
            .filter { shouldSync -> shouldSync }
            .onEach { syncScheduler.requestImmediateSync() }
            .launchIn(appScope)
    }

}
