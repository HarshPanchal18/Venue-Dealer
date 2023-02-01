package com.example.book_venue

class Venue (
    var title:String?="",
    var desc:String?="",
    var location:String?="",
    var city:String?="",
    var state:String?="",
    var availableTime:Boolean=false,
    var dealerContact:String?="",
    var venueType:ArrayList<String> = ArrayList()
)
