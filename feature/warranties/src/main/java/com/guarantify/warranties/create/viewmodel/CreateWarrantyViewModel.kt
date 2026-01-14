package com.guarantify.warranties.create.viewmodel

import androidx.lifecycle.ViewModel
import com.guarantify.domain.repository.WarrantiesRepository
import com.guarantify.ui.di.AppDateFormatProvider
import com.guarantify.warranties.create.state.CreateWarrantyErrors
import com.guarantify.warranties.create.state.CreateWarrantyEvent
import com.guarantify.warranties.create.state.CreateWarrantyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.ZoneOffset

@HiltViewModel
class CreateWarrantyViewModel @Inject constructor(
    private val warrantiesRepository: WarrantiesRepository,
    private val dateFormatProvider: AppDateFormatProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateWarrantyUiState())
    val uiState: StateFlow<CreateWarrantyUiState> = _uiState.asStateFlow()

    fun onEvent(event: CreateWarrantyEvent) {
        when (event) {
            is CreateWarrantyEvent.ProductNameChanged -> {
                _uiState.update {
                    val updated = it.copy(productName = event.value)
                    updated.copy(errors = validate(updated))
                }
            }

            is CreateWarrantyEvent.BrandChanged -> {
                _uiState.update { it.copy(brand = event.value) }
            }

            is CreateWarrantyEvent.StoreNameChanged -> {
                _uiState.update {
                    val updated = it.copy(storeName = event.value)
                    updated.copy(errors = validate(updated))
                }
            }

            is CreateWarrantyEvent.NotesChanged -> {
                _uiState.update { it.copy(notes = event.value) }
            }

            is CreateWarrantyEvent.PriceChanged -> {
                _uiState.update { it.copy(price = event.value) }
            }

            is CreateWarrantyEvent.CurrencyChanged -> {
                _uiState.update { it.copy(selectedCurrency = event.value) }
            }

            is CreateWarrantyEvent.PurchaseDateSelected -> {
                _uiState.update {
                    val updated = it.copy(
                        purchaseDateMillis = event.date,
                        purchaseDateText = event.date?.toDateText() ?: ""
                    )
                    updated.copy(errors = validate(updated))
                }
            }

            is CreateWarrantyEvent.ExpirationDateSelected -> {
                _uiState.update {
                    val updated = it.copy(
                        expirationDateMillis = event.date,
                        expirationDateText = event.date?.toDateText() ?: ""
                    )
                    updated.copy(errors = validate(updated))
                }
            }

            is CreateWarrantyEvent.PhotoPicked -> {
                _uiState.update {
                    it.copy(
                        photoUri = event.value.toString(),
                        photoUploadError = null
                    )
                }
            }

            is CreateWarrantyEvent.PhotoRemoved -> {
                _uiState.update {
                    it.copy(
                        photoUri = null,
                        photoUploadError = null
                    )
                }
            }

            is CreateWarrantyEvent.SaveClicked -> {
                val errors = validate(_uiState.value)

                _uiState.update {
                    it.copy(
                        attemptedSubmit = true,
                        errors = errors
                    )
                }

                val hasErrors = listOf(
                    errors.productNameError,
                    errors.storeNameError,
                    errors.purchaseDateError,
                    errors.expirationDateError
                ).any { it != null }

                if (!hasErrors) {
                    //todo: implement save

                    _uiState.update { it.copy(attemptedSubmit = false) }
                }
            }
        }
    }

    private fun validate(state: CreateWarrantyUiState): CreateWarrantyErrors {
        val nameErr = if (state.productName.isBlank()) "Name is required" else null
        val storeErr = if (state.storeName.isBlank()) "Store is required" else null
        val purchaseErr =
            if (state.purchaseDateMillis == null) "Purchase date is required" else null
        val expirationErr =
            if (state.expirationDateMillis == null) "Expiration date is required" else null

        return CreateWarrantyErrors(
            productNameError = nameErr,
            storeNameError = storeErr,
            purchaseDateError = purchaseErr,
            expirationDateError = expirationErr
        )
    }

    private fun Long.toDateText(): String {
        val localDate = Instant.ofEpochMilli(this)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        return localDate.format(dateFormatProvider.shortDate)
    }

}