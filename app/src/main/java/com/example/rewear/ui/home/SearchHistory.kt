package com.example.rewear.ui.home

data class SearchHistory(
    val title:String,
    val detail:String,
    var isChecked:Boolean=false,
    val latitude: Double,
    val longitude: Double
)