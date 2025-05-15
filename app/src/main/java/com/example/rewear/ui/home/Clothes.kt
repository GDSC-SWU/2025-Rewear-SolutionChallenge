package com.example.rewear.ui.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data model for a clothing item, used primarily in the UI.
 * Implements [Parcelable] to allow instances to be passed between Android components (e.g., Activities, Fragments).
 */
@Parcelize
data class Clothes(
    val id: String, // Unique identifier for the clothing item
    val imageList: List<String>, // List of image URLs for the item
    val label: String, // A general label, often used for category (e.g., "Tops", "Pants")
    val name: String, // Name of the clothing item
    val location: String, // Location associated with the item (e.g., "Mapo-dong")
    val timeAgo: String, // Relative time string (e.g., "1m ago", "2 hours ago")
    val likeCount: Int, // Number of likes
    val description: String, // Description of the item
    val swapMethod: String // Preferred swap method (e.g., "In-person", "Shipping")
) : Parcelable