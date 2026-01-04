package com.guarantify.warranties.create

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guarantify.ui.R
import com.guarantify.ui.components.AppDateField
import com.guarantify.ui.components.AppDatePickerModalInput
import com.guarantify.ui.components.AppOutlinedTextField
import com.guarantify.ui.components.PriceInputField
import com.guarantify.ui.theme.GuarantifyTheme
import com.guarantify.warranties.create.state.CreateWarrantyUiState
import com.guarantify.warranties.create.viewmodel.CreateWarrantyViewModel

@Composable
fun CreateWarrantyScreen(viewModel: CreateWarrantyViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateWarrantyScreenContent(
        uiState = uiState,
        onProductNameChange = { viewModel.onProductNameChange(it) },
        onBrandNameChange = { viewModel.onBrandNameChange(it) },
        onStoreNameChange = { viewModel.onStoreNameChange(it) },
        onPiceChange = { viewModel.onPriceChange(it) },
        onCurrencyChange = { viewModel.onCurrencyChange(it) },
        onNotesChange = { viewModel.onNotesChange(it) },
        onPurchaseDateSelected = { viewModel.onPurchaseDateSelected(it) },
        onExpirationDateSelected = { viewModel.onExpirationDateSelected(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWarrantyScreenContent(
    uiState: CreateWarrantyUiState,
    onProductNameChange: (String) -> Unit,
    onBrandNameChange: (String) -> Unit,
    onStoreNameChange: (String) -> Unit,
    onPiceChange: (String) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onPurchaseDateSelected: (Long?) -> Unit,
    onExpirationDateSelected: (Long?) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var showDatePicker by remember { mutableStateOf(false) }
    var activeDateField by remember { mutableStateOf<ActiveDateField?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Add new Warranty")
                },
                navigationIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.arrow_back),
                        contentDescription = "Back"
                    )
                },
                actions = {
                    //TODO
                }
            )
        }
    ) { innerPadding ->
        if (showDatePicker) {
            AppDatePickerModalInput(
                onDateSelected = {
                    when (activeDateField) {
                        ActiveDateField.Purchase -> onPurchaseDateSelected(it)
                        ActiveDateField.Expiration -> onExpirationDateSelected(it)
                        null -> Unit
                    }
                },
                onDismiss = {
                    showDatePicker = false
                    activeDateField = null
                    focusManager.clearFocus(force = true)
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                onValueChange = { onProductNameChange(it) },
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
                value = uiState.storeName,
                label = "Store Name",
                onValueChange = { onStoreNameChange(it) },
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
                value = uiState.brand,
                label = "Brand (Optional)",
                onValueChange = { onBrandNameChange(it) },
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
                label = "Price",
                placeholder = "0.00",
                value = uiState.price,
                onValueChange = onPiceChange,
                onCurrencyChange = onCurrencyChange,
                focusManager = focusManager
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
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                AppDateField(
                    modifier = Modifier.weight(1f),
                    value = uiState.expirationDateText,
                    label = "Expiration date",
                    onClick = {
                        activeDateField = ActiveDateField.Expiration
                        showDatePicker = true
                    }
                )
            }

            AppOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.notes,
                onValueChange = onNotesChange,
                label = "Notes",
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

            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            ) {
                Text(
                    text = "Save"
                )
            }
        }
    }
}

private enum class ActiveDateField { Purchase, Expiration }

@Composable
@Preview
fun CreateWarrantyScreenPreview() {
    GuarantifyTheme {
        CreateWarrantyScreen()
    }
}