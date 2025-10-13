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
            .collection("users")
            .document(userId)
            .collection("warranties")
            .document(warranty.id)
            .set(warranty.copy(updatedAt = System.currentTimeMillis()))
            .await()
    }

    override suspend fun getAllWarranties(): List<WarrantyDto> {
        val snapshot = firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("warranties")
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(WarrantyDto::class.java) }
    }

    override suspend fun getUpdatedSince(timestamp: Long): List<WarrantyDto> {
        val snapshot = firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("warranties")
            .whereGreaterThan("updatedAt", timestamp)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(WarrantyDto::class.java) }
    }
}