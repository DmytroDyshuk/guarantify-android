package com.guarantify.data.network.firebase.storage

import android.net.Uri
import com.guarantify.common.result.Result

interface WarrantyPhotoStorage {
    suspend fun uploadImage(warrantyId: String, imageUri: Uri): Result<String>
    suspend fun deleteImage(imageUrl: String)
}