package com.example.rewear.ai

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Interface for AI-related network operations.
interface ApiService {

    // Uploads a clothing image for classification.
    @Multipart
    @POST("classify-clothing/")
    fun classifyClothing(
        @Part file: MultipartBody.Part
    ): Call<CategoryResponse>
}