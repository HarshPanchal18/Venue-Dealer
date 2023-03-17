package com.example.book_venue.model

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.book_venue.databinding.BookedVenueCardBinding
import com.google.firebase.auth.FirebaseAuth

class BookingViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    private lateinit var binding: BookedVenueCardBinding
    val user= FirebaseAuth.getInstance().currentUser

    fun bind(booked:Booked) {
        binding.apply {
            bookingUsername.text=booked.username
            bookingUserMail.text=booked.usermail
            bookingDate.text=booked.date
            bookingLandmark.text=booked.landmark
            bookingCity.text=booked.city
            bookingState.text=booked.state
        }
    }
}
