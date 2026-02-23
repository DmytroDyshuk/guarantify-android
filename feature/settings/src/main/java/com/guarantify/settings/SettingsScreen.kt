package com.guarantify.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.guarantify.settings.components.SettingButton
import com.guarantify.settings.components.SwitchSettingButton
import com.guarantify.ui.components.ConfirmationDialog
import com.guarantify.ui.theme.GuarantifyTheme
import com.guarantify.ui.theme.darkGrayishCyan

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    SettingsScreenContent(
        onSignOutClicked = {
            viewModel.onSignOutClicked()
        }
    )
}

@Composable
fun SettingsScreenContent(
    onSignOutClicked: () -> Unit
) {
    val scrollState = rememberScrollState()
    var checked by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        when {
            showDialog -> {
                ConfirmationDialog(
                    icon = ImageVector.vectorResource(R.drawable.rounded_logout_24),
                    title = stringResource(R.string.log_out_dialog_title),
                    text = stringResource(R.string.log_out_dialog_text),
                    onDismissRequest = { showDialog = false },
                    onConfirmation = {
                        onSignOutClicked()
                        showDialog = false
                    }
                )
            }
        }
        
        Text(
            modifier = Modifier.padding(start = 12.dp, top = 12.dp),
            text = stringResource(R.string.settings_appearance_title),
            color = darkGrayishCyan,
            style = MaterialTheme.typography.labelSmall
        )
        SettingButton(
            headlineText = stringResource(R.string.settings_app_theme_button),
            leadingIcon = ImageVector.vectorResource(R.drawable.rounded_light_mode_24),
            onClick = { }
        )
        SettingButton(
            headlineText = stringResource(R.string.settings_language_button),
            leadingIcon = ImageVector.vectorResource(R.drawable.rounded_language_24),
            onClick = { }
        )
        HorizontalDivider()

        Text(
            modifier = Modifier.padding(start = 12.dp, top = 12.dp),
            text = stringResource(R.string.settings_account_title),
            color = darkGrayishCyan,
            style = MaterialTheme.typography.labelSmall
        )
        SettingButton(
            headlineText = stringResource(R.string.settings_logout_button),
            leadingIcon = ImageVector.vectorResource(R.drawable.rounded_logout_24),
            onClick = { showDialog = true }
        )
        SettingButton(
            headlineText = stringResource(R.string.settings_delete_account_button),
            leadingIcon = ImageVector.vectorResource(R.drawable.rounded_delete_24),
            onClick = { }
        )
        HorizontalDivider()

        Text(
            modifier = Modifier.padding(start = 12.dp, top = 12.dp),
            text = stringResource(R.string.settings_notifications_title),
            color = darkGrayishCyan,
            style = MaterialTheme.typography.labelSmall

        )
        SwitchSettingButton(
            headlineText = stringResource(R.string.settings_notifications_button),
            leadingIcon = ImageVector.vectorResource(R.drawable.rounded_notifications_24),
            checked = checked,
            onCheck = {
                checked = it
            }
        )
        HorizontalDivider()

        Text(
            modifier = Modifier.padding(start = 12.dp, top = 12.dp),
            text = stringResource(R.string.settings_data_title),
            color = darkGrayishCyan,
            style = MaterialTheme.typography.labelSmall
        )
        SettingButton(
            headlineText = stringResource(R.string.settings_data_sync_button),
            leadingIcon = ImageVector.vectorResource(R.drawable.rounded_cloud_sync_24),
            onClick = { }
        )
        HorizontalDivider()

        Text(
            modifier = Modifier.padding(start = 12.dp, top = 12.dp),
            text = stringResource(R.string.settings_about_app_title),
            color = darkGrayishCyan,
            style = MaterialTheme.typography.labelSmall
        )
        SettingButton(
            headlineText = stringResource(R.string.settings_about_button),
            leadingIcon = ImageVector.vectorResource(R.drawable.rounded_info_24),
            onClick = { }
        )
        SettingButton(
            headlineText = stringResource(R.string.settings_terms_of_service_button),
            leadingIcon = ImageVector.vectorResource(R.drawable.rounded_docs_24),
            onClick = { }
        )
        SettingButton(
            headlineText = stringResource(R.string.settings_privacy_policy_button),
            leadingIcon = ImageVector.vectorResource(R.drawable.rounded_privacy_tip_24),
            onClick = { }
        )
        HorizontalDivider()

        Text(
            modifier = Modifier.padding(start = 12.dp, top = 12.dp),
            text = stringResource(R.string.settings_feedback_support_title),
            color = darkGrayishCyan,
            style = MaterialTheme.typography.labelSmall
        )
        SettingButton(
            headlineText = stringResource(R.string.settings_contact_us_button),
            leadingIcon = ImageVector.vectorResource(R.drawable.rounded_contact_support_24),
            onClick = { }
        )
        SettingButton(
            headlineText = stringResource(R.string.settings_report_bug_button),
            leadingIcon = ImageVector.vectorResource(R.drawable.rounded_bug_report_24),
            onClick = { }
        )
        SettingButton(
            headlineText = stringResource(R.string.settings_rate_app_button),
            leadingIcon = ImageVector.vectorResource(R.drawable.outline_heart_plus_24),
            onClick = { }
        )
        HorizontalDivider()
    }
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    GuarantifyTheme {
        Surface {
            SettingsScreenContent(
                onSignOutClicked = {}
            )
        }
    }
}