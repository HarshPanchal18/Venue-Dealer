package com.example.book_venue

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.venue_card.view.*

class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    val auth=FirebaseAuth.getInstance()
    val user=auth.currentUser

    val sBuilder=StringBuilder()

    fun bind(venue:Venue){
        itemView.apply {
            venueTitle_card.text=venue.Name
            venueDescription_card.text=venue.Description
            landmark_card.text=venue.Landmark
            city_card.text=venue.City
            state_card.text=venue.State
            nameDealer.text=user?.displayName
            capacity_card.text= venue.Capacity
            availableTime_card.text= venue.Availability
            contactDealer.text=user?.email.toString()
            //types_card.text= venue.Types.toString()
            /*for (i in 0..venue.Types.size){
                sBuilder.append(venue.Types[i]).toString()
            }
            types_card.text=sBuilder*/
        }
    }
}
