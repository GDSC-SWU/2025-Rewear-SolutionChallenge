package com.example.rewear.ui.home

data class Notifications(
    val type:NotificationType,
    val userName:String,
    val imageResId:Int,
    val title:String,
    val message:String,
    val timeAgo:String
)


enum class NotificationType(val title:String,val message:String) {
    SWAP_REQUEST("Swap request from %s","%s wants to swap an item with you!\nCheck out their feed and make a match."),
    SWAP_SUCCESSFUL("Swap successful with %s","Your item was successfully swapped.\nCheck the details now."),
    SWAP_FAILED("Swap failed with %s","Sadly, your swap request was declined. Pick another item from someone new :)"),
    UPCYCLE("Your Item Is Still Waiting...","It's been 6 days and there's not response.\nWant to upcycle or donate?")
}

