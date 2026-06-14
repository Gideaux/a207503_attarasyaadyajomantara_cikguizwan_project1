package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.network

import android.text.Html

/*
 * ─────────────────────────────────────────────────────────────────────────────
 *  PILLAR 3 — INTERNET DATA (repository)
 *
 *  Wraps the Retrofit service and converts raw network DTOs into a clean UI
 *  model. Open Trivia DB returns text with HTML entities (e.g. &quot; &#039;),
 *  so we decode them here, keeping the UI layer simple.
 * ─────────────────────────────────────────────────────────────────────────────
 */

/** Clean, UI-friendly trivia question (decoded text, shuffled answer list). */
data class TriviaQuestion(
    val category: String,
    val difficulty: String,
    val question: String,
    val correctAnswer: String,
    val allAnswers: List<String>
)

interface TriviaRepository {
    suspend fun fetchQuestions(amount: Int, category: Int): List<TriviaQuestion>
}

class NetworkTriviaRepository(
    private val apiService: TriviaApiService = TriviaApi.service
) : TriviaRepository {

    override suspend fun fetchQuestions(amount: Int, category: Int): List<TriviaQuestion> {
        val response = apiService.getQuestions(amount = amount, category = category)

        // Map each DTO to the clean domain model.
        return response.results.map { dto ->
            val answers = (dto.incorrectAnswers + dto.correctAnswer)
                .map { decode(it) }
                .shuffled()

            TriviaQuestion(
                category      = decode(dto.category),
                difficulty    = dto.difficulty.replaceFirstChar { it.uppercase() },
                question      = decode(dto.question),
                correctAnswer = decode(dto.correctAnswer),
                allAnswers    = answers
            )
        }
    }

    /** Decode HTML entities returned by the API into readable text. */
    private fun decode(raw: String): String =
        Html.fromHtml(raw, Html.FROM_HTML_MODE_LEGACY).toString()
}
