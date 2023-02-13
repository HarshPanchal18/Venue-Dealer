package com.example.book_venue

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.venue_card.view.*

class VenueViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    val auth=FirebaseAuth.getInstance()
    val user=auth.currentUser

    fun bind(venue:Venue){
        itemView.apply {
            venueTitle_card.text=venue.Name
            venueDescription_card.text=venue.Description
            landmark_card.text=venue.Landmark
            city_card.text=venue.City
            state_card.text=venue.State
            capacity_card.text= venue.VenueCapacity
            parking_card.text= venue.Parking
            availableTime_card.text= venue.Availability
            //nameDealer.text=user?.displayName
            //contactDealer.text=user?.email.toString()
            types_card.text= venue.Types.removePrefix("[").removeSuffix("]")
        }
    }
}
