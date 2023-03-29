package com.example.book_venue.model

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class Booked(
    val venuename: String,
    val username: String,
    val useremail: String,
    val landmark: String,
    val venuecity: String,
    val venuestate: String,
    var startdate: Timestamp,
    val enddate: Timestamp,
    val venueownercontact: String,
    val images: String,
    val dealerid: String,
    val rent: String,
    val bookingId: String,
    val pendingId: String,
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
        "",
        ""
    )
}

/*
val startdates = doc.getTimestamp("startdate")
bookingModel.startdate = startdates?.toDate().toString()
val dateFormat = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a z", Locale.getDefault())
val formattedDate = startdates?.let { dateFormat.format(it) }
*/
