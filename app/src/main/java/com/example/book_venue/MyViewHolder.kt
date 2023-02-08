package com.example.book_venue

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.venue_card.view.*

class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    val auth=FirebaseAuth.getInstance()
    val user=auth.currentUser

    fun bind(venue:Venue){
        itemView.apply {
            venueTitle_card.text=venue.title
            venueDescription_card.text=venue.desc
            landmark_card.text=venue.landmark
            city_card.text=venue.city
            state_card.text=venue.state
            nameDealer.text=user?.displayName
            availableTime_card.text= venue.availableTime.toString()
            contactDealer.text=user?.email.toString()
            types_card.text=venue.venueTypes.toString()
        }
    }
}
