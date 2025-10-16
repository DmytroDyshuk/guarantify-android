package com.guarantify.data.network.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.guarantify.data.network.dto.WarrantyDto
import kotlinx.coroutines.tasks.await

class FirebaseWarrantyDataSourceImpl(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : FirebaseWarrantyDataSource {

    val userId: String
        get() = firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException("User not authenticated")

    override suspend fun createOrUpdateWarranty(warranty: WarrantyDto) {
        firebaseFirestore
            .collection(FirestoreConstants.COLLECTION_USERS)
            .document(userId)
            .collection(FirestoreConstants.COLLECTION_WARRANTIES)
            .document(warranty.id)
            .set(warranty)
            .await()
    }

    override suspend fun getAllWarranties(): List<WarrantyDto> {
        val snapshot = firebaseFirestore
            .collection(FirestoreConstants.COLLECTION_USERS)
            .document(userId)
            .collection(FirestoreConstants.COLLECTION_WARRANTIES)
            .get()
            .await()

        return snapshot.documents.mapNotNull {
            val dto = it.toObject(WarrantyDto::class.java)
            dto?.copy(id = it.id)
        }
    }

    override suspend fun getUpdatedSince(timestamp: Long): List<WarrantyDto> {
        val snapshot = firebaseFirestore
            .collection(FirestoreConstants.COLLECTION_USERS)
            .document(userId)
            .collection(FirestoreConstants.COLLECTION_WARRANTIES)
            .whereGreaterThan(FirestoreConstants.FIELD_UPDATED_AT, timestamp)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(WarrantyDto::class.java) }
    }

    override suspend fun deleteWarranty(remoteId: String) {
        firebaseFirestore
            .collection(FirestoreConstants.COLLECTION_USERS)
            .document(userId)
            .collection(FirestoreConstants.COLLECTION_WARRANTIES)
            .document(remoteId)
            .delete()
            .await()
    }
}