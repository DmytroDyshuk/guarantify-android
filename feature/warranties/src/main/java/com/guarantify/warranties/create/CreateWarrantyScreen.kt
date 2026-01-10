package com.guarantify.warranties.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guarantify.ui.R
import com.guarantify.ui.components.AppDateField
import com.guarantify.ui.components.AppDatePickerModalInput
import com.guarantify.ui.components.AppOutlinedTextField
import com.guarantify.ui.components.DashedCard
import com.guarantify.ui.components.PriceInputField
import com.guarantify.ui.theme.GuarantifyTheme
import com.guarantify.warranties.create.state.CreateWarrantyEvent
import com.guarantify.warranties.create.state.CreateWarrantyUiState
import com.guarantify.warranties.create.viewmodel.CreateWarrantyViewModel

@Composable
fun CreateWarrantyScreen(
    viewModel: CreateWarrantyViewModel = hiltViewModel(),
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateWarrantyScreenContent(
        uiState = uiState,
        onBackClicked = onBackClicked,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWarrantyScreenContent(
    uiState: CreateWarrantyUiState,
    onBackClicked: () -> Unit,
    onEvent: (CreateWarrantyEvent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var showDatePicker by remember { mutableStateOf(false) }
    var activeDateField by remember { mutableStateOf<ActiveDateField?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Add new Warranty")
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .clickable {
                                onBackClicked()
                            },
                        imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back),
                        contentDescription = "Back"
                    )
                }
            )
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                onClick = {
                    onEvent(CreateWarrantyEvent.SaveClicked)
                }
            ) {
                Text(
                    text = "Save"
                )
            }
        }
    ) { innerPadding ->
        if (showDatePicker) {
            AppDatePickerModalInput(
                onDateSelected = {
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

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                Column {
                    ListItem(   
                        headlineContent = { Text("Take photo") },
                        leadingContent = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_photo_camera),
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.clickable { } //TODO: implement
                    )
                    ListItem(
                        headlineContent = { Text("Choose from gallery") },
                        leadingContent = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_gallery_image),
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.clickable { } //TODO: implement
                    )
                }
            }
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
                onValueChange = { onEvent(CreateWarrantyEvent.ProductNameChanged(it)) },
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
                onValueChange = { onEvent(CreateWarrantyEvent.StoreNameChanged(it)) },
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
            PriceInputField(
                modifier = Modifier.fillMaxWidth(),
                label = "Price",
                placeholder = "0.00",
                value = uiState.price,
                onValueChange = {
                    onEvent(CreateWarrantyEvent.PriceChanged(it))
                },
                onCurrencyChange = {
                    onEvent(CreateWarrantyEvent.CurrencyChanged(it))
                },
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
                onValueChange = {
                    onEvent(CreateWarrantyEvent.NotesChanged(it))
                },
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
            DashedCard(
                modifier = Modifier
                    .height(124.dp)
                    .fillMaxWidth()
                    .clickable {
                        showBottomSheet = true
                    },
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
        }
    }
}

private enum class ActiveDateField { Purchase, Expiration }

@Composable
@Preview
fun CreateWarrantyScreenPreview() {
    GuarantifyTheme {
        CreateWarrantyScreen() {}
    }
}