package com.example.book_venue.model

data class Venue(
    val Name: String,
    val Description: String,
    val Landmark: String,
    val City: String,
    val State: String,
    val VenueCapacity: String,
    val Availability: String,
    val DealerContact: String,
    val RentPerHour: String,
    val Types: String,
    val Parking: String,
    val RestRooms: String,
    val docId: String,
    val url0: String,
    //val images: ArrayList<Map<String,String>>
) {
    constructor():this(
        "",
        "",
        "",
        "",
        "",
        "0",
        "",
        "",
        "0",
        "",
        "",
        "",
        "",
        "",
        //ArrayList<Map<String, String>>()
    )
}
