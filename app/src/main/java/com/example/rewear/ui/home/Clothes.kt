package com.example.rewear.ui.home

import java.io.Serializable

data class Clothes (
    val imageResId:Int,
    val label:String,
    val name:String,
    val location:String,
    val timeAgo:String,
    val likeCount:Int
):Serializable