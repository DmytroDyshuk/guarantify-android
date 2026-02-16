package com.guarantify.warranties.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.guarantify.ui.R
import com.guarantify.ui.components.AppDateField
import com.guarantify.ui.components.AppDatePickerModalInput
import com.guarantify.ui.components.AppOutlinedTextField
import com.guarantify.ui.components.DashedCard
import com.guarantify.ui.components.PriceInputField
import com.guarantify.ui.components.WarrantyPhotoPicker
import com.guarantify.warranties.create.state.CreateWarrantyErrors
import com.guarantify.warranties.create.state.CreateWarrantyEvent
import com.guarantify.warranties.create.state.CreateWarrantyUiState

@Composable
fun CreateWarrantyScreen(
    viewModel: CreateWarrantyViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onCreateWarranty: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateWarrantyScreenContent(
        uiState = uiState,
        validationErrorsState = uiState.validationErrors,
        onBackClick = onBackClick,
        onEvent = viewModel::onEvent,
        onCreateWarranty = onCreateWarranty
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWarrantyScreenContent(
    uiState: CreateWarrantyUiState,
    validationErrorsState: CreateWarrantyErrors,
    onBackClick: () -> Unit,
    onCreateWarranty: () -> Unit,
    onEvent: (CreateWarrantyEvent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var showDatePicker by remember { mutableStateOf(false) }
    var activeDateField by remember { mutableStateOf<ActiveDateField?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }
    val showErrors = uiState.attemptedSubmit

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onCreateWarranty()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Add new Warranty")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
                    .fillMaxWidth(),
                onClick = {
                    onEvent(CreateWarrantyEvent.SaveClicked)
                },
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Save Warranty"
                    )
                }
            }
        }
    ) { innerPadding ->
        LaunchedEffect(uiState.saveError) {
            uiState.saveError?.let { error ->
                snackBarHostState.showSnackbar(
                    message = error,
                    duration = SnackbarDuration.Long
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                },
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.productName,
                label = "Product Name",
                onValueChange = { onEvent(CreateWarrantyEvent.ProductNameChanged(it)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                isError = showErrors && validationErrorsState.productNameError != null,
                errorMessage = validationErrorsState.productNameError
            )
            AppOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.storeName,
                label = "Store Name",
                onValueChange = { onEvent(CreateWarrantyEvent.StoreNameChanged(it)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                isError = showErrors && validationErrorsState.storeNameError != null,
                errorMessage = validationErrorsState.storeNameError
            )
            AppOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.brand,
                label = "Brand (Optional)",
                onValueChange = { onEvent(CreateWarrantyEvent.BrandChanged(it)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            )
            AppOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.serialNumber,
                label = "Serial Number (Optional)",
                onValueChange = { onEvent(CreateWarrantyEvent.SerialNumberChanged(it)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            )
            PriceInputField(
                modifier = Modifier.fillMaxWidth(),
                label = "Price (Optional)",
                placeholder = "0.00",
                value = uiState.price,
                selectedCurrency = uiState.selectedCurrency,
                focusManager = focusManager,
                isError = showErrors && validationErrorsState.priceError != null,
                errorMessage = validationErrorsState.priceError,
                onValueChange = {
                    onEvent(CreateWarrantyEvent.PriceChanged(it))
                },
                onCurrencyChange = {
                    onEvent(CreateWarrantyEvent.CurrencyChanged(it))
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                AppDateField(
                    modifier = Modifier.weight(1f),
                    value = uiState.purchaseDateText,
                    label = "Purchase date",
                    onClick = {
                        activeDateField = ActiveDateField.Purchase
                        showDatePicker = true
                    },
                    isError = showErrors && validationErrorsState.purchaseDateError != null,
                    errorMessage = validationErrorsState.purchaseDateError
                )
                Spacer(modifier = Modifier.width(16.dp))
                AppDateField(
                    modifier = Modifier.weight(1f),
                    value = uiState.expirationDateText,
                    label = "Expiration date",
                    onClick = {
                        activeDateField = ActiveDateField.Expiration
                        showDatePicker = true
                    },
                    isError = showErrors && validationErrorsState.expirationDateError != null,
                    errorMessage = validationErrorsState.expirationDateError
                )
            }

            AppOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.notes,
                onValueChange = {
                    onEvent(CreateWarrantyEvent.NotesChanged(it))
                },
                label = "Notes (Optional)",
                singleLine = false,
                maxCharacters = 255,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                )
            )
            WarrantyPhotoField(
                photoUri = uiState.photoUri,
                onAddOrReplace = {
                    showBottomSheet = true
                },
                onRemove = {
                    onEvent(CreateWarrantyEvent.PhotoRemoved)
                }
            )
        }

        if (showDatePicker) {
            AppDatePickerModalInput(
                onDateSelect = {
                    when (activeDateField) {
                        ActiveDateField.Purchase -> onEvent(
                            CreateWarrantyEvent.PurchaseDateSelected(it)
                        )

                        ActiveDateField.Expiration -> onEvent(
                            CreateWarrantyEvent.ExpirationDateSelected(it)
                        )

                        null -> Unit
                    }
                },
                onDismiss = {
                    showDatePicker = false
                    activeDateField = null
                    focusManager.clearFocus(force = true)
                },
                initialSelectedDateMillis = when (activeDateField) {
                    ActiveDateField.Purchase -> uiState.purchaseDateMillis
                    ActiveDateField.Expiration -> uiState.expirationDateMillis
                        ?: uiState.purchaseDateMillis

                    null -> null
                },
                minDateMillis = if (activeDateField == ActiveDateField.Expiration) {
                    uiState.purchaseDateMillis
                } else null
            )
        }

        WarrantyPhotoPicker(
            openSheet = showBottomSheet,
            onPhotoPick = { uri ->
                uri?.let { onEvent(CreateWarrantyEvent.PhotoPicked(it)) }

            },
            onDismissSheet = {
                showBottomSheet = false
            }
        )
    }
}

@Composable
private fun WarrantyPhotoField(
    photoUri: String? = null,
    onAddOrReplace: () -> Unit,
    onRemove: () -> Unit
) {
    if (photoUri == null) {
        DashedCard(
            modifier = Modifier
                .height(124.dp)
                .fillMaxWidth()
                .clickable { onAddOrReplace() },
            color = MaterialTheme.colorScheme.primary
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_add_photo),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Add Photo"
                )
                Text(
                    text = "Add warranty photo",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ElevatedCard(
                modifier = Modifier
                    .height(154.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = null,
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    onClick = onRemove
                ) {
                    Text(
                        text = "Remove"
                    )
                }
                Spacer(modifier = Modifier.width(24.dp))
                Button(
                    onClick = onAddOrReplace
                ) {
                    Text(
                        text = "Replace"
                    )
                }
            }
        }
    }
}

private enum class ActiveDateField { Purchase, Expiration }