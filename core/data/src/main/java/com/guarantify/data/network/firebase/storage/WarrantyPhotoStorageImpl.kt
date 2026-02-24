package com.guarantify.data.network.firebase.storage

import android.net.Uri
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.storageMetadata
import com.guarantify.common.result.Result
import com.guarantify.domain.model.FirebaseStorageError
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import java.io.IOException

class WarrantyPhotoStorageImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseAuth: FirebaseAuth
) : WarrantyPhotoStorage {

    private val storageRef = firebaseStorage.reference

    override suspend fun uploadImage(imageUri: Uri, warrantyId: String): Result<String> {
        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: return Result.Error(Exception("User not authenticated"))

            val fileName = imageUri.lastPathSegment
            val imageRef = storageRef.child("users/$userId/warranties/$warrantyId/$fileName")

            val metadata = storageMetadata {
                contentType = "image/jpeg"
            }

            imageRef.putFile(imageUri, metadata).await()

            val downloadUrl = imageRef.downloadUrl.await()

            Result.Success(downloadUrl.toString())
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            val mappedError = when (e) {
                is FirebaseException -> mapFirebaseError(e)
                is IOException -> FirebaseStorageError.NetworkError()
                else -> FirebaseStorageError.Unknown(e)
            }
            Result.Error(mappedError)
        }
    }

    override suspend fun deleteImage(imageUrl: String) {
        TODO("Not yet implemented")
    }

    private fun mapFirebaseError(e: FirebaseException): FirebaseStorageError {
        return when (e) {
            is StorageException -> {
                when (e.errorCode) {
                    StorageException.ERROR_QUOTA_EXCEEDED -> FirebaseStorageError.QuotaExceeded()
                    StorageException.ERROR_NOT_AUTHENTICATED -> FirebaseStorageError.Unauthorized()
                    StorageException.ERROR_OBJECT_NOT_FOUND -> FirebaseStorageError.NotFound()
                    else -> FirebaseStorageError.NetworkError()
                }
            }

            else -> FirebaseStorageError.Unknown(e)
        }
    }
}