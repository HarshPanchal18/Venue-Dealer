package com.example.book_venue.viewModel

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.book_venue.data.Pending
import com.example.book_venue.databinding.PendingCardBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class PendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var binding = PendingCardBinding.bind(itemView)
    val user = FirebaseAuth.getInstance().currentUser

    fun bind(pending: Pending) {
        binding.apply {
            pendingUsername.text = pending.username
            pendingUserMail.text = pending.useremail
            pendingCity.text = pending.venuecity
            pendingState.text = pending.venuestate
            pendingVenuename.text = pending.venuename
            pendingLandmark.text = pending.landmark
            pendingVenueRent.text = pending.rent

            val miliSeconds = pending.enddate.toDate().time - pending.startdate.toDate().time
            val seconds = miliSeconds / 1000
            val minutes = seconds / (60 * 1000)
            val hours = minutes / (60 * 60 * 1000)
            val days = miliSeconds / (24 * 60 * 60 * 1000)
            val duration = "$days days, $hours hours"//, $minutes minutes"
            pendingDuration.text = duration

            val startdate = pending.startdate
            var formattedDate =
                SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(
                    startdate.toDate())
            pendingStartDate.text = formattedDate

            val enddate = pending.enddate
            formattedDate =
                SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(
                    enddate.toDate())
            pendingEndDate.text = formattedDate

        }
    }

}
