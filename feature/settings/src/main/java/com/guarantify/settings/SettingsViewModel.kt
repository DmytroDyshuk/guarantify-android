package com.guarantify.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guarantify.common.result.Result
import com.guarantify.domain.usecase.SignOutUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val signOutUserUseCase: SignOutUserUseCase
) : ViewModel() {

    fun onSignOutClicked() {
        viewModelScope.launch {
            val result = signOutUserUseCase()
            if (result is Result.Error) {
                Log.e(TAG, "Failed to clear local data, sign-out aborted", result.throwable)
            }
        }
    }

    private companion object {
        const val TAG = "SettingsViewModel"
    }

}
