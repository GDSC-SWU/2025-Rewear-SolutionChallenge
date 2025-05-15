package com.example.rewear.ai

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton Retrofit client for AI API interactions.
 */
object RetrofitClient {
    // Base URL for the AI clothing classification API.
    private const val BASE_URL = "https://clothing-api-949882041921.us-central1.run.app/"

    private val client: OkHttpClient = OkHttpClient.Builder()
        .build()

    /**
     * Lazily initialized [ApiService] instance for making API calls.
     */
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}