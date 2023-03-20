package com.example.book_venue.model

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.book_venue.databinding.BookedCardBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.lang.String.valueOf
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.namespace.QName.valueOf

class BookingViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    var binding = BookedCardBinding.bind(itemView)
    val user = FirebaseAuth.getInstance().currentUser

    fun bind(booked: Booked) {
        binding.apply {
            bookingUsername.text = booked.username
            bookingUserMail.text = booked.useremail
            bookingCity.text = booked.venuecity
            bookingState.text = booked.venuestate

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

        }
    }
}
