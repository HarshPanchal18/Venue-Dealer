package com.example.book_venue

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.venue_card.view.*

class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    val auth=FirebaseAuth.getInstance()
    val user=auth.currentUser

    fun bind(venue:Venue){
        itemView.apply {
            venueTitle_card.text=venue.title
            venueDescription_card.text=venue.desc
            nameDealer.text=user?.displayName
            contactDealer.text=user?.email.toString()
        }
    }
}
