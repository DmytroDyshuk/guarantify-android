package com.guarantify.data.network.firebase.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.guarantify.data.network.dto.WarrantyDto
import com.guarantify.domain.model.auth.AuthRequiredException
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class FirestoreWarrantyDataSourceImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : FirestoreWarrantyDataSource {

    private val userId: String
        get() = firebaseAuth.currentUser?.uid
            ?: throw AuthRequiredException()

    private val warrantiesCollection
        get() = firebaseFirestore
            .collection(FirestoreConstants.COLLECTION_USERS)
            .document(userId)
            .collection(FirestoreConstants.COLLECTION_WARRANTIES)

    override suspend fun createOrUpdateWarranty(warranty: WarrantyDto) {
        warrantiesCollection.document(warranty.id).set(warranty).await()
    }

    override suspend fun getAllWarranties(): List<WarrantyDto> {
        val snapshot = warrantiesCollection.get().await()

        return snapshot.documents.mapNotNull {
            val dto = it.toObject(WarrantyDto::class.java)
            dto?.copy(id = it.id)
        }
    }

    override suspend fun getWarrantiesUpdatedSince(timestamp: Long): List<WarrantyDto> {
        val snapshot = warrantiesCollection
            .whereGreaterThan(FirestoreConstants.FIELD_UPDATED_AT, timestamp)
            .get()
            .await()

        return snapshot.documents.mapNotNull {
            val dto = it.toObject(WarrantyDto::class.java)
            dto?.copy(id = it.id)
        }
    }

    override suspend fun deleteWarranty(id: String) {
        warrantiesCollection.document(id).delete().await()
    }

    override suspend fun pushChangesBatch(toUpload: List<WarrantyDto>, toDelete: List<String>) {
        val batch = firebaseFirestore.batch()

        toUpload.forEach { dto ->
            batch.set(warrantiesCollection.document(dto.id), dto)
        }

        toDelete.forEach { id ->
            batch.delete(warrantiesCollection.document(id))
        }

        batch.commit().await()
    }

}