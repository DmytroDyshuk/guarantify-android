package com.guarantify.util.extensions

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun Context.createTempImageUri(): Uri {
    val imagesDir = File(cacheDir, "images").apply { mkdirs() }
    val file = File.createTempFile("warranty_", ".jpg", imagesDir)

    return FileProvider.getUriForFile(
        this,
        "$packageName.fileprovider",
        file
    )
}