package com.example.book_venue.model

import com.google.firebase.Timestamp

data class Pending(
    val venuename: String,
    val username: String,
    val useremail: String,
    //val landmark: String,
    val venuecity: String,
    val venuestate: String,
    var startdate: Timestamp,
    val enddate: Timestamp,
    val venueownercontact: String,
    val requestId: String,
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        Timestamp.now(),
        Timestamp.now(),
        "",
        ""
    )
}
