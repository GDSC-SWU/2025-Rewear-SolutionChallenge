package com.example.rewear.network

import com.google.gson.annotations.SerializedName

/**
 * Data model representing an item related to a "reform" (repair/alteration) process,
 * typically fetched from or sent to the API.
 */
data class ApiReformItem(
    @SerializedName("id") val id: Int, // Unique ID for the reform record/entry
    @SerializedName("itemId") val itemId: Int, // ID of the original item undergoing reform
    @SerializedName("partnerId") val partnerId: Int, // ID of the partner (e.g., tailor, repair shop) handling the reform
    @SerializedName("partnerName") val partnerName: String,  // Name of the reform partner
    @SerializedName("title") val title: String,  // Title of the item (usually the original item's title)
    @SerializedName("category") val category: String, // Category of the item
    @SerializedName("imageUrls") val imageUrls: List<String>,  // Image URLs associated with the item or reform details
    @SerializedName("reformStatus") val reformStatus: String // Current status of the reform process (e.g., "REQUESTED", "IN_PROGRESS", "COMPLETED")
)
