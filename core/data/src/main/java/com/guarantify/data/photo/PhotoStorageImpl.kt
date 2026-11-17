package com.guarantify.data.photo

import android.content.Context
import android.net.Uri
import com.guarantify.common.di.IoDispatcher
import com.guarantify.common.result.Result
import com.guarantify.domain.model.StorageError
import com.guarantify.domain.photo.PhotoStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import androidx.core.net.toUri

class PhotoStorageImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PhotoStorage {

    override suspend fun copyPhoto(sourceUri: String): Result<String> = withContext(ioDispatcher) {
        val photosDir = File(context.filesDir, PHOTOS_DIR).apply { mkdirs() }
        val targetFile = File(photosDir, "${UUID.randomUUID()}.jpg")

        try {
            val input = context.contentResolver.openInputStream(sourceUri.toUri())
                ?: throw IllegalStateException("Cannot open input stream for $sourceUri")

            input.use { source ->
                targetFile.outputStream().use { target ->
                    source.copyTo(target)
                }
            }

            Result.Success(Uri.fromFile(targetFile).toString())
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            targetFile.delete()
            Result.Error(StorageError.Unknown(e))
        }
    }

    companion object {
        const val PHOTOS_DIR = "photos"
    }
}
