package com.example.book_venue.model

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.book_venue.databinding.BookedCardBinding
import com.google.firebase.auth.FirebaseAuth

class BookingViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    var binding = BookedCardBinding.bind(itemView)
    val user = FirebaseAuth.getInstance().currentUser

    fun bind(booked: Booked) {
        binding.apply {
            bookingUsername.text = booked.username
            bookingUserMail.text = booked.useremail
            //bookingStartDate.text = booked.startdate
            //bookingEndDate.text = booked.enddate
            bookingLandmark.text=booked.venueownercontact
            bookingCity.text = booked.venuecity
            bookingState.text = booked.venuestate
        }
    }
}
