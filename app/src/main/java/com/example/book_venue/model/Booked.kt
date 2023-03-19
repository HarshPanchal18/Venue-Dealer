package com.example.book_venue.model

data class Booked(
    val venuename: String,
    val username: String,
    val useremail: String,
    //val landmark: String,
    val venuecity: String,
    val venuestate: String,
    //val enddate: String,
    //val startdate: String,
    val venueownercontact: String
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        //"",
        //"",
        //"",
    )
}
