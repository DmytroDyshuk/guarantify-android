package com.guarantify.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.guarantify.data.workmanager.SyncWorker
import com.guarantify.domain.model.auth.AuthState
import com.guarantify.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val workManager: WorkManager
) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> = authRepository.observeAuthState()
        .onEach {
            if (it is AuthState.Authenticated) {
                triggerImmediateSync()
            }
        }
        .map {
            MainActivityUiState.Success(it)
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = MainActivityUiState.Loading,
            started = SharingStarted.WhileSubscribed(5_000)
        )

    private fun triggerImmediateSync() {
        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()

        workManager.enqueueUniqueWork(
            "immediate_sync_warranties_work",
            ExistingWorkPolicy.REPLACE,
            syncWorkRequest
        )
    }

}