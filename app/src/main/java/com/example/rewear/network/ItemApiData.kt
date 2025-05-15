package com.example.rewear.network

// Data class for the request body when creating a new item.
data class CreateItemRequest(
    val title: String,
    val category: String,
    val description: String?,
    val swapMethod: String?,
    val imageUrls: List<String>
)

// Data class for the response received after an item-related operation (e.g., creation).
data class ItemResponse(
    val id: Int,
    val title: String,
    val category: String,
    val imageUrls: List<String>,
    val description: String?,
    val swapMethod: String?
)

// Data class for the request body when making a swap request.
data class SwapRequest(
    val swapStatus: String

)

// Data class for the response received after a swap operation.
data class SwapResponse(
    val id: Int,
    val itemId: Int,
    val swapStatus: String,
    val createdAt: String
)

// Data class for the response when fetching detailed information about an item.
data class ItemDetailResponse(
    val id: Int,
    val title: String,
    val category: String,
    val imageUrls: List<String>,
    val description: String?,
    val swapMethod: String?
)
