package com.guarantify.warranties

import androidx.lifecycle.ViewModel
import com.guarantify.domain.repository.WarrantiesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class WarrantiesViewModel @Inject constructor(
    private val warrantiesRepository: WarrantiesRepository
) : ViewModel() {

}