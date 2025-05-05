package com.example.rewear.ui.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Clothes(
    val imageList:List<Int>,
    val label:String,
    val name:String,
    val location:String,
    val timeAgo:String,
    val likeCount:Int,
    val description: String
):Parcelable