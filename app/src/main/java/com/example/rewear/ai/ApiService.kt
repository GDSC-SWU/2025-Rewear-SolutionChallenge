package com.example.rewear.ai

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("classify-clothing/")
    fun classifyClothing(
        @Part file:MultipartBody.Part
    ):retrofit2.Call<CategoryResponse>
}