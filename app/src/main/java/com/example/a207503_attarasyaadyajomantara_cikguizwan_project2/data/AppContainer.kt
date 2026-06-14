package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data

import android.content.Context
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.firebase.CommunityRepository
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.firebase.FirestoreCommunityRepository
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.network.NetworkTriviaRepository
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.network.TriviaRepository

/**
 * Manual dependency-injection container.
 *
 * Holds the single instance of each repository for the whole app. Built lazily
 * so heavy resources (the database, network client, Firestore) aren't created
 * until something actually needs them.
 *
 * Project 2 adds two cloud/internet repositories alongside the original Room one:
 *  - [kouleejRepository]  → Pillar 1: Room (local persistence)
 *  - [triviaRepository]   → Pillar 3: Retrofit (Open Trivia DB REST API)
 *  - [communityRepository]→ Pillar 2: Firebase Firestore (cloud community data)
 */
interface AppContainer {
    val kouleejRepository: KouleejRepository
    val triviaRepository: TriviaRepository
    val communityRepository: CommunityRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val kouleejRepository: KouleejRepository by lazy {
        OfflineKouleejRepository(KouleejDatabase.getDatabase(context).kouleejDao())
    }

    override val triviaRepository: TriviaRepository by lazy {
        NetworkTriviaRepository()
    }

    override val communityRepository: CommunityRepository by lazy {
        FirestoreCommunityRepository()
    }
}
