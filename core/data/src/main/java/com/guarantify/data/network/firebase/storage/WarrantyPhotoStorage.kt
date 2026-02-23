package com.guarantify.data.network.firebase.storage

import android.net.Uri
import com.guarantify.common.result.Result

interface WarrantyPhotoStorage {
    suspend fun uploadImage(imageUri: Uri, warrantyId: String): Result<String>
    suspend fun deleteImage(imageUrl: String)
}