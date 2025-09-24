package com.guarantify.settings

import androidx.lifecycle.ViewModel
import com.guarantify.domain.usecase.SignOutUserUseCase
import jakarta.inject.Inject

class SettingsViewModel @Inject constructor(
    private val signOutUserUseCase: SignOutUserUseCase
) : ViewModel() {

}