package com.lifelink.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreSyncSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    private fun userCollection(uid: String, name: String) =
        firestore.collection("users").document(uid).collection(name)

    suspend fun uploadContact(uid: String, id: String, data: Map<String, Any?>) {
        userCollection(uid, "contacts").document(id).set(data).await()
    }

    suspend fun uploadVaultRecord(uid: String, id: String, data: Map<String, Any?>) {
        userCollection(uid, "vault_records").document(id).set(data).await()
    }

    suspend fun uploadCheckIn(uid: String, id: String, data: Map<String, Any?>) {
        userCollection(uid, "checkins").document(id).set(data).await()
    }

    suspend fun uploadPublicCheckIn(email: String, data: Map<String, Any?>) {
        firestore.collection("public_checkins").document(email.lowercase()).set(data).await()
    }

    suspend fun fetchPublicCheckIn(email: String): Map<String, Any?>? {
        val snapshot = firestore.collection("public_checkins").document(email.lowercase()).get().await()
        return if (snapshot.exists()) snapshot.data else null
    }
}