package com.example.rewear.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton Retrofit client for the main application's API services.
 * Provides instances of [AppApiService] and [ReformApiService].
 */
object RetrofitClientApp {
    // Base URL for the application's backend API.
    private const val APP_BASE_URL = "http://35.216.19.128:8080/"

    private val client: OkHttpClient = OkHttpClient.Builder()
        .build()

    // Single, lazily-initialized Retrofit instance shared by all services from this client.
    // This avoids redundant Retrofit object creation.
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(APP_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /** Provides a lazily-initialized instance of [AppApiService]. */
    val apiService: AppApiService by lazy {
        Retrofit.Builder()
            .baseUrl(APP_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AppApiService::class.java)
    }

    /** Provides a lazily-initialized instance of [ReformApiService]. */
    val reformApiService: ReformApiService by lazy {
        retrofit.create(ReformApiService::class.java)
    }
}