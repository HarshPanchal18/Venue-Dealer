package com.example.book_venue.data

import com.google.firebase.Timestamp

data class Pending(
    val venuename: String,
    val username: String,
    val useremail: String,
    val landmark: String,
    val venuecity: String,
    val venuestate: String,
    var startdate: Timestamp,
    val enddate: Timestamp,
    val images: String,
    val rent: String,
    val dealerid: String,
    val pendingId: String,
    var bookingId: String,
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        Timestamp.now(),
        Timestamp.now(),
        "",
        "",
        "",
        "",
        ""
    )
}
