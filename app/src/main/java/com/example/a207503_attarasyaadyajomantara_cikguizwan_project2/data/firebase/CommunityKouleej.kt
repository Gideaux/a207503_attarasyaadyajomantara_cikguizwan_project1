package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.firebase

/*
 * ─────────────────────────────────────────────────────────────────────────────
 *  PILLAR 2 — CLOUD INTEGRATION (Firestore model)
 *
 *  Represents a Kouleej that has been shared to the community and lives in the
 *  Cloud Firestore "community_kouleejes" collection. Unlike the Room entity
 *  (which is private/local), these documents are shared across all users.
 *
 *  Firestore requires a no-argument constructor to deserialize documents, which
 *  Kotlin gives us for free by providing default values for every property.
 * ─────────────────────────────────────────────────────────────────────────────
 */
data class CommunityKouleej(
    val id: String = "",          // Firestore document id
    val title: String = "",
    val subject: String = "",
    val questionCount: Int = 0,
    val description: String = "",
    val author: String = "Anonymous",
    val createdAt: Long = 0L      // epoch millis, used for ordering
)
