package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.network

import com.google.gson.annotations.SerializedName

/*
 * ─────────────────────────────────────────────────────────────────────────────
 *  PILLAR 3 — INTERNET DATA (REST API)
 *
 *  Data Transfer Objects (DTOs) that mirror the JSON returned by the free
 *  Open Trivia Database API (https://opentdb.com/api.php).
 *
 *  Example request:  https://opentdb.com/api.php?amount=10&category=18&type=multiple
 *
 *  These plain data classes are what Gson deserializes the network response
 *  into. They are intentionally kept separate from the Room entities so the
 *  network shape and the database shape can evolve independently.
 * ─────────────────────────────────────────────────────────────────────────────
 */

/** Top-level wrapper object returned by the API. */
data class TriviaResponse(
    // 0 = success. Any other code means no results / an error (see API docs).
    @SerializedName("response_code") val responseCode: Int,
    @SerializedName("results") val results: List<TriviaQuestionDto>
)

/** A single trivia question as delivered by the API. */
data class TriviaQuestionDto(
    @SerializedName("type") val type: String,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("category") val category: String,
    @SerializedName("question") val question: String,
    @SerializedName("correct_answer") val correctAnswer: String,
    @SerializedName("incorrect_answers") val incorrectAnswers: List<String>
)
