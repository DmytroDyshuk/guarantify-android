package com.guarantify.warranties.create.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guarantify.domain.model.Warranty
import com.guarantify.domain.repository.GoogleAuthRepository
import com.guarantify.domain.repository.WarrantiesRepository
import com.guarantify.util.date.DateFormatter
import com.guarantify.util.extensions.toLocalDate
import com.guarantify.warranties.create.state.CreateWarrantyErrors
import com.guarantify.warranties.create.state.CreateWarrantyEvent
import com.guarantify.warranties.create.state.CreateWarrantyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CreateWarrantyViewModel @Inject constructor(
    private val warrantiesRepository: WarrantiesRepository,
    private val authRepository: GoogleAuthRepository,
    private val dateFormatter: DateFormatter
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
                _uiState.update { state ->
                    val updated = state.copy(
                        purchaseDateMillis = event.date,
                        purchaseDateText = event.date?.let {
                            dateFormatter.formatToShortText(it)
                        } ?: ""
                    )
                    updated.copy(errors = validate(updated))
                }
            }

            is CreateWarrantyEvent.ExpirationDateSelected -> {
                _uiState.update { state ->
                    val updated = state.copy(
                        expirationDateMillis = event.date,
                        expirationDateText = event.date?.let {
                            dateFormatter.formatToShortText(it)
                        } ?: ""
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
                        errors = errors,
                        saveError = null
                    )
                }

                if (errors.hasErrors()) return

                val userId = authRepository.getSignedUser()?.userId
                if (userId == null) {
                    _uiState.update {
                        it.copy(
                            saveError = "You must be logged in to create a warranty",
                            attemptedSubmit = false
                        )
                    }
                    return
                }

                val state = _uiState.value

                val purchaseDate = state.purchaseDateMillis?.toLocalDate()
                val expirationDate = state.expirationDateMillis?.toLocalDate()

                if (purchaseDate == null || expirationDate == null) {
                    _uiState.update {
                        it.copy(
                            saveError = "Please select both purchase and expiration dates",
                            attemptedSubmit = false
                        )
                    }
                    return
                }

                _uiState.update { it.copy(isSaving = true) }

                viewModelScope.launch {
                    try {
                        val warranty = Warranty(
                            userId = userId,
                            productName = state.productName,
                            storeName = state.storeName,
                            brand = state.brand.takeIf { it.isNotBlank() },
                            amount = state.price.toDoubleOrNull()?.let { (it * 100).toLong() },
                            currency = state.selectedCurrency,
                            photoUrl = state.photoUri,
                            purchaseDate = purchaseDate,
                            expirationDate = expirationDate,
                            notes = state.notes.takeIf { it.isNotBlank() }
                        )

                        warrantiesRepository.createOrUpdateWarranty(warranty)

                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                attemptedSubmit = false,
                                saveSuccess = true
                            )
                        }
                    } catch (e: Exception) {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                saveError = e.message ?: "Failed to save warranty",
                                attemptedSubmit = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun validate(state: CreateWarrantyUiState): CreateWarrantyErrors {
        val nameErr = if (state.productName.isBlank()) "Name is required" else null
        val storeErr = if (state.storeName.isBlank()) "Store is required" else null
        val purchaseErr =
            if (state.purchaseDateMillis == null) "Purchase date is required" else null
        val expirationErr = when {
            state.expirationDateMillis == null -> "Expiration date is required"
            state.purchaseDateMillis != null &&
                    state.expirationDateMillis <= state.purchaseDateMillis -> "Expiration date must be after purchase date"

            else -> null
        }
        val priceError = if (state.price.isNotBlank() && state.price.toDoubleOrNull() == null) {
            "Invalid price format"
        } else null

        return CreateWarrantyErrors(
            productNameError = nameErr,
            storeNameError = storeErr,
            purchaseDateError = purchaseErr,
            expirationDateError = expirationErr,
            priceError = priceError
        )
    }

    private fun CreateWarrantyErrors.hasErrors(): Boolean {
        return listOf(
            productNameError,
            storeNameError,
            purchaseDateError,
            expirationDateError,
            priceError
        ).any { it != null }
    }

}