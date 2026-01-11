package com.guarantify.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.guarantify.ui.R
import com.guarantify.util.extensions.createTempImageUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarrantyPhotoPicker(
    onPhotoPicked: (Uri?) -> Unit,
    openSheet: Boolean,
    onDismissSheet: () -> Unit
) {
    val context = LocalContext.current

    var pendingCameraUri by rememberSaveable { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingCameraUri?.let { onPhotoPicked(it.toUri()) }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) onPhotoPicked(uri)
    }

    if (openSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissSheet
        ) {
            ListItem(
                headlineContent = { Text("Take photo") },
                leadingContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_photo_camera),
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable {
                    onDismissSheet()
                    val uri = context.createTempImageUri()
                    pendingCameraUri = uri.toString()
                    cameraLauncher.launch(uri)
                }
            )
            ListItem(
                headlineContent = { Text("Choose from gallery") },
                leadingContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_gallery_image),
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable {
                    onDismissSheet()
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}