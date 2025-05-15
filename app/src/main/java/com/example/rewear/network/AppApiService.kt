package com.example.rewear.network

import retrofit2.Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Defines API endpoints for the main application services.
 */
interface AppApiService {
    /** Creates a new item listing. */
    @POST("api/items")
    fun createItem(
        @Query("address") address: String, // Address as a query parameter for the item
        @Body itemRequest: CreateItemRequest // Data for the new item
    ): Call<ItemResponse>

    /** Fetches items for the home feed, can be filtered by address. Uses Kotlin Coroutines. */
    @GET("api/home")
    suspend fun getHomeItems(
        @Query("address") address: String
    ): Response<List<HomeApiResponseItem>> // Returns a Response object for more detailed handling

    /** Retrieves details for a specific item by its ID. */
    @GET("api/items/{itemsId}")
    fun getItemDetail(
        @Path("itemsId") itemId: String // Item ID from the URL path
    ): Call<ItemDetailResponse>

    /** Initiates or updates a swap request for a specific item. */
    @PUT("api/items/{itemsId}/swap")
    fun requestSwap(
        @Path("itemsId") itemId: String, // Item ID from the URL path
        @Body swapRequest: SwapRequest // Data for the swap request
    ): Call<SwapResponse>
}

