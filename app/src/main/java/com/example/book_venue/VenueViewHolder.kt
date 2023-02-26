package com.example.book_venue

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.book_venue.databinding.VenueCardBinding
import com.google.firebase.auth.FirebaseAuth

class VenueViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var binding=VenueCardBinding.bind(itemView)

    private val auth=FirebaseAuth.getInstance()
    val user=auth.currentUser

    fun bind(venue:Venue){

        binding.apply {
            venueTitleCard.text=venue.Name
            venueDescriptionCard.text=venue.Description
            landmarkCard.text=venue.Landmark
            cityCard.text=venue.City
            stateCard.text=venue.State
            capacityCard.text= venue.VenueCapacity
            parkingCard.text= venue.Parking
            availableTimeCard.text= venue.Availability
            nameDealer.text=venue.docId
            //contactDealer.text=user?.email.toString()
            typesCard.text= venue.Types.removePrefix("[").removeSuffix("]")
        }
    }
}
