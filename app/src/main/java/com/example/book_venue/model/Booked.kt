package com.example.book_venue.model

data class Booked(
    val username: String,
    val landmark: String,
    val city: String,
    val state: String,
    val date: String,
    val usermail: String,
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        ""
    )
}
