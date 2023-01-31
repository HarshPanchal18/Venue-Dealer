package com.example.book_venue

class Venue (
    val title:String="",
    val desc:String="",
    val location:String="",
    val city:String="",
    val state:String="",

    val availableTime:Boolean = false,
    val dealerName:String="",
    val dealerContact:String="",
    val dealerPhNo:String="",
    val venueType:ArrayList<String> =ArrayList()
)
