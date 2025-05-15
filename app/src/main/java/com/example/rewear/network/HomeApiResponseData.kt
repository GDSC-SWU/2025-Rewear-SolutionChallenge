package com.example.rewear.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize // Enables automatic Parcelable implementation.
data class HomeApiResponseItem(
    // @SerializedName maps JSON keys to Kotlin properties.
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("address") val address: String,
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("imageUrls") val imageUrls: List<String>
) : Parcelable