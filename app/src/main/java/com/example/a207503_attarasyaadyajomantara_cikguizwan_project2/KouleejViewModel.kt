package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.KouleejEntity
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.KouleejRepository
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.firebase.CommunityKouleej
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.firebase.CommunityRepository
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.network.TriviaQuestion
import com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.network.TriviaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ─── User profile data (carried over from Lab 4 — still in-memory, not the
//     focus of persistence). Only the Kouleej list is persisted via Room. ───
data class KahootUser(
    val username: String = "",
    val gradeLevel: String = "",
    val favoriteSubject: String = ""
)

/** UI state for the Retrofit-powered Discover (trivia) screen. */
sealed interface TriviaUiState {
    data object Idle : TriviaUiState
    data object Loading : TriviaUiState
    data class Success(val questions: List<TriviaQuestion>) : TriviaUiState
    data class Error(val message: String) : TriviaUiState
}

/**
 * The shared ViewModel for the whole app. It is the single source of truth that
 * the Compose screens observe, and it talks to the three data-layer pillars
 * through their repositories:
 *
 *   • [kouleejRepository]   → Pillar 1: Room (local quizzes)
 *   • [communityRepository] → Pillar 2: Firebase Firestore (shared quizzes)
 *   • [triviaRepository]    → Pillar 3: Retrofit (Open Trivia DB questions)
 *
 * The camera/QR sensor (Pillar 4) is a UI concern handled in QrScannerScreen,
 * which simply navigates using ids this ViewModel already exposes.
 */
class KouleejViewModel(
    private val repository: KouleejRepository,
    private val communityRepository: CommunityRepository,
    private val triviaRepository: TriviaRepository
) : ViewModel() {

    // ═══════════════════════════════════════════════════════════
    //  PILLAR 1 — ROOM: user-created Kouleejes (persistent, local)
    // ═══════════════════════════════════════════════════════════
    val kouleejes: StateFlow<List<KouleejEntity>> =
        repository.getAllKouleejesStream()
            .stateIn(
                scope        = viewModelScope,
                started      = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    fun addKouleej(title: String, subject: String, questionCount: Int, description: String) {
        viewModelScope.launch {
            repository.insertKouleej(
                KouleejEntity(
                    title         = title,
                    subject       = subject,
                    questionCount = questionCount,
                    description   = description
                )
            )
        }
    }

    fun deleteKouleej(id: Int) {
        viewModelScope.launch {
            val target = kouleejes.value.firstOrNull { it.id == id } ?: return@launch
            repository.deleteKouleej(target)
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  PILLAR 2 — FIRESTORE: shared community Kouleejes (cloud)
    // ═══════════════════════════════════════════════════════════
    /** True when google-services.json is present and Firebase is usable. */
    val isFirebaseAvailable: Boolean = communityRepository.isAvailable

    val communityKouleejes: StateFlow<List<CommunityKouleej>> =
        communityRepository.communityStream()
            .catch { emit(emptyList()) } // swallow Firestore errors → empty list
            .stateIn(
                scope        = viewModelScope,
                started      = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    // One-shot status message for the Community screen (e.g. "Shared!" / errors).
    private val _communityMessage = MutableStateFlow<String?>(null)
    val communityMessage: StateFlow<String?> = _communityMessage.asStateFlow()

    /** Publish a local Kouleej to the shared Firestore collection. */
    fun publishToCommunity(kouleej: KouleejEntity) {
        viewModelScope.launch {
            try {
                communityRepository.publish(
                    CommunityKouleej(
                        title         = kouleej.title,
                        subject       = kouleej.subject,
                        questionCount = kouleej.questionCount,
                        description   = kouleej.description,
                        author        = _user.value.username.ifBlank { "Anonymous" }
                    )
                )
                _communityMessage.value = "Shared “${kouleej.title}” to the community!"
            } catch (e: Exception) {
                _communityMessage.value = e.message ?: "Could not share to community."
            }
        }
    }

    fun clearCommunityMessage() { _communityMessage.value = null }

    // ═══════════════════════════════════════════════════════════
    //  PILLAR 3 — RETROFIT: live trivia from Open Trivia DB
    // ═══════════════════════════════════════════════════════════
    private val _triviaState = MutableStateFlow<TriviaUiState>(TriviaUiState.Idle)
    val triviaState: StateFlow<TriviaUiState> = _triviaState.asStateFlow()

    /** Fetch a fresh batch of questions for the given Open Trivia DB category. */
    fun loadTrivia(category: Int = 18, amount: Int = 10) {
        viewModelScope.launch {
            _triviaState.value = TriviaUiState.Loading
            _triviaState.value = try {
                val questions = triviaRepository.fetchQuestions(amount, category)
                if (questions.isEmpty()) {
                    TriviaUiState.Error("No questions returned. Try another topic.")
                } else {
                    TriviaUiState.Success(questions)
                }
            } catch (e: Exception) {
                TriviaUiState.Error(e.message ?: "Failed to load questions. Check your connection.")
            }
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  Shared UI state: user profile + selected Kouleej (in-memory)
    // ═══════════════════════════════════════════════════════════
    private val _user = MutableStateFlow(KahootUser())
    val user: StateFlow<KahootUser> = _user.asStateFlow()

    private val _selectedKouleejId = MutableStateFlow<Int?>(null)
    val selectedKouleejId: StateFlow<Int?> = _selectedKouleejId.asStateFlow()

    fun updateUser(username: String, gradeLevel: String, favoriteSubject: String) {
        _user.value = KahootUser(
            username        = username,
            gradeLevel      = gradeLevel,
            favoriteSubject = favoriteSubject
        )
    }

    fun selectKouleej(id: Int) {
        _selectedKouleejId.value = id
    }

    companion object {
        // How long to keep Flows active after the last subscriber leaves
        // (e.g. during a config change). 5s mirrors the Now-in-Android sample.
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
