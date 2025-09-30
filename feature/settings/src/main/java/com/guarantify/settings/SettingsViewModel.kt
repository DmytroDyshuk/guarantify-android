package com.guarantify.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            signOutUserUseCase()
        }
    }

}