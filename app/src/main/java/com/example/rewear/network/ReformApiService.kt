package com.example.rewear.network

import retrofit2.Response
import retrofit2.http.GET
import com.example.rewear.network.ApiReformItem
import retrofit2.http.Query

/**
 * API service interface for 'reform' (repair/alteration) related operations.
 */
interface ReformApiService {
    @GET("api/reform/items")
    suspend fun getReformItems(@Query("status") status: String? = null)// Optional query parameter to filter by status
            : Response<List<ApiReformItem>>
}