package com.example.book_venue.model

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Color.red
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.book_venue.R
import com.example.book_venue.databinding.BookedCardBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class BookingViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    var binding = BookedCardBinding.bind(itemView)
    val user = FirebaseAuth.getInstance().currentUser

    @SuppressLint("SimpleDateFormat")
    fun bind(booked: Booked) {
        binding.apply {
            venueTitle.text = booked.venuename
            bookingUsername.text = booked.username
            bookingUserMail.text = booked.useremail
            bookingCity.text = booked.venuecity
            bookingState.text = booked.venuestate
            bookingLandmark.text = booked.landmark
            rentPrice.text = booked.rent

            val miliSeconds = booked.enddate.toDate().time - booked.startdate.toDate().time
            val seconds = miliSeconds / 1000
            val minutes = seconds / (60*1000)
            val hours = minutes / (60*60*1000)
            val days = miliSeconds  / (24*60*60*1000)
            val duration = "$days days, $hours hours"//, $minutes minutes"
            bookingDuration.text = duration

            val startdate = booked.startdate
            var formattedDate =
                SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(
                    startdate.toDate())
            bookingStartDate.text = formattedDate

            val enddate = booked.enddate
            formattedDate =
                SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(
                    enddate.toDate())
            bookingEndDate.text = formattedDate

            if(booked.startdate >= Timestamp.now()) {
                activeStatusCard.visibility = View.GONE
            } else {
                upcomingStatusCard.visibility = View.GONE
                cancelBookingButton.visibility = View.GONE
            }
        }
    }
}
