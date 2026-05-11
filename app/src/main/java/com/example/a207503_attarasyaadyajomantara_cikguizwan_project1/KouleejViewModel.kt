package com.example.a207503_attarasyaadyajomantara_cikguizwan_project1

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ─── User profile data (unchanged from Lab 4) ───────────────────
data class KahootUser(
    val username: String = "",
    val gradeLevel: String = "",
    val favoriteSubject: String = ""
)

// ─── A user-created Kouleej (Project 1 addition) ────────────────
data class Kouleej(
    val id: Int,
    val title: String,
    val subject: String,
    val questionCount: Int,
    val description: String
)

// ─── Shared view-model ──────────────────────────────────────────
class KahootViewModel : ViewModel() {

    // User profile — unchanged from Lab 4.
    private val _user = MutableStateFlow(KahootUser())
    val user: StateFlow<KahootUser> = _user.asStateFlow()

    // User-created kouleej list — new in Project 1.
    // Start with two sample rows so the Library screen isn't empty on first launch.
    private val _kouleejes = MutableStateFlow(
        listOf(
            Kouleej(1, "Pop Quiz: Algebra Basics", "Mathematics", 10, "A quick warm-up quiz on linear equations."),
            Kouleej(2, "World War II Trivia",      "History",     15, "Test your knowledge of major WWII events.")
        )
    )
    val kouleejes: StateFlow<List<Kouleej>> = _kouleejes.asStateFlow()

    // Id of the kouleej selected for the Detail screen.
    private val _selectedKouleejId = MutableStateFlow<Int?>(null)
    val selectedKouleejId: StateFlow<Int?> = _selectedKouleejId.asStateFlow()

    // ── User profile (Lab 4 API, kept exactly) ─────────────────
    fun updateUser(username: String, gradeLevel: String, favoriteSubject: String) {
        _user.value = KahootUser(
            username = username,
            gradeLevel = gradeLevel,
            favoriteSubject = favoriteSubject
        )
    }

    // ── Kouleej list: add / delete / select ────────────────────
    fun addKouleej(title: String, subject: String, questionCount: Int, description: String) {
        val current = _kouleejes.value
        val nextId  = (current.maxOfOrNull { it.id } ?: 0) + 1
        _kouleejes.value = current + Kouleej(
            id            = nextId,
            title         = title,
            subject       = subject,
            questionCount = questionCount,
            description   = description
        )
    }

    fun deleteKouleej(id: Int) {
        _kouleejes.value = _kouleejes.value.filterNot { it.id == id }
    }

    fun selectKouleej(id: Int) {
        _selectedKouleejId.value = id
    }
}
