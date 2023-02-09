package com.example.book_venue

data class Venue(
    val Name: String,
    val Description: String,//? = null,
    val Landmark: String,
    val City: String,
    val State: String,
    val Capacity: String,
    val Availability: String,
    val Dealer_Ph: String,
    val RentPerHour: String,
    //val Types: ArrayList<String>
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
        //arrayListOf<String>()
        //listOf<String>()/* = java.util.ArrayList<kotlin.String> */
    )
}
