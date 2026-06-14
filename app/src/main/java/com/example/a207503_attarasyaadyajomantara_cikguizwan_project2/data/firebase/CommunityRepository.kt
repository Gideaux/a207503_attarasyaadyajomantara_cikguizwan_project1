package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

/*
 * ─────────────────────────────────────────────────────────────────────────────
 *  PILLAR 2 — CLOUD INTEGRATION (Firestore repository)
 *
 *  All Firebase Firestore calls live here, isolated from the UI and from the
 *  Room/Retrofit layers. This is the code I explain during the Q&A for the
 *  "cloud" pillar.
 *
 *  IMPORTANT: Firebase is optional in this project. If google-services.json is
 *  missing, FirebaseFirestore.getInstance() throws, so we guard every access and
 *  expose [isAvailable]. When unavailable, reads return an empty stream and
 *  writes throw a clear exception the UI can surface — the rest of the app still
 *  runs. See the README "Firebase setup" section.
 * ─────────────────────────────────────────────────────────────────────────────
 */
interface CommunityRepository {
    /** Whether Firebase has been configured (google-services.json present). */
    val isAvailable: Boolean

    /** Live stream of all shared Kouleejes, newest first. */
    fun communityStream(): Flow<List<CommunityKouleej>>

    /** Publishes a Kouleej to the shared cloud collection. */
    suspend fun publish(kouleej: CommunityKouleej)
}

class FirestoreCommunityRepository : CommunityRepository {

    companion object {
        private const val COLLECTION = "community_kouleejes"
    }

    // Try to obtain the Firestore instance. If Firebase isn't configured this
    // throws, so we capture null instead of crashing the whole app.
    private val firestore: FirebaseFirestore? = runCatching {
        FirebaseFirestore.getInstance()
    }.getOrNull()

    override val isAvailable: Boolean
        get() = firestore != null

    override fun communityStream(): Flow<List<CommunityKouleej>> {
        val db = firestore ?: return flowOf(emptyList())

        // callbackFlow bridges Firestore's listener callback into a coroutine
        // Flow, emitting a fresh list every time the cloud data changes.
        return callbackFlow {
            val registration = db.collection(COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        // Close the flow with the error so the UI can react.
                        close(error)
                        return@addSnapshotListener
                    }
                    val items = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(CommunityKouleej::class.java)?.copy(id = doc.id)
                    }.orEmpty()
                    trySend(items)
                }
            // Detach the listener when the collector stops.
            awaitClose { registration.remove() }
        }
    }

    override suspend fun publish(kouleej: CommunityKouleej) {
        val db = firestore
            ?: throw IllegalStateException("Firebase is not configured. Add google-services.json.")

        // Let Firestore generate the document id; store the rest of the fields.
        db.collection(COLLECTION)
            .add(
                mapOf(
                    "title" to kouleej.title,
                    "subject" to kouleej.subject,
                    "questionCount" to kouleej.questionCount,
                    "description" to kouleej.description,
                    "author" to kouleej.author,
                    "createdAt" to System.currentTimeMillis()
                )
            )
            .await() // suspend until the write completes (kotlinx-coroutines-play-services)
    }
}
