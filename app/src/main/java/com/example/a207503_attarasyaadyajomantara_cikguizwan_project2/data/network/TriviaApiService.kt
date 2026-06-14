package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/*
 * ─────────────────────────────────────────────────────────────────────────────
 *  PILLAR 3 — INTERNET DATA (Retrofit interface)
 *
 *  This is the heart of the REST integration that I must be able to explain and
 *  live-modify during the Q&A. Retrofit turns this annotated interface into a
 *  working HTTP client at runtime.
 * ─────────────────────────────────────────────────────────────────────────────
 */
interface TriviaApiService {

    /**
     * GET https://opentdb.com/api.php?amount={amount}&category={category}&type=multiple
     *
     * @param amount   how many questions to fetch (max 50)
     * @param category Open Trivia DB category id. Education-relevant defaults:
     *                 9 = General Knowledge, 17 = Science & Nature,
     *                 18 = Science: Computers, 19 = Mathematics, 22 = Geography.
     * @param type     "multiple" = multiple-choice questions.
     *
     * `suspend` so it can be called from a coroutine without blocking the UI.
     */
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int = 10,
        @Query("category") category: Int = 18,
        @Query("type") type: String = "multiple"
    ): TriviaResponse
}

/**
 * Lazily-built singleton that exposes the configured [TriviaApiService].
 *
 * Kept as a simple object (rather than DI) to make the wiring obvious for the
 * Q&A: base URL + Gson converter + an OkHttp logging interceptor for debugging.
 */
object TriviaApi {

    private const val BASE_URL = "https://opentdb.com/"

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        )
        .build()

    val service: TriviaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create(GsonBuilder().create())
            )
            .build()
            .create(TriviaApiService::class.java)
    }
}
