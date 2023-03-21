package com.example.book_venue.model

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.book_venue.databinding.PendingCardBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class PendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var binding = PendingCardBinding.bind(itemView)
    val user = FirebaseAuth.getInstance().currentUser

    fun bind(pending:  Pending) {
        binding.apply {
            pendingUsername.text = pending.username
            pendingUserMail.text = pending.useremail
            pendingCity.text = pending.venuecity
            pendingState.text = pending.venuestate

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
