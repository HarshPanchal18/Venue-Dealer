package com.example.book_venue.viewModel

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.book_venue.R
import com.example.book_venue.data.Venue
import com.example.book_venue.databinding.VenueCardBinding
import com.google.firebase.auth.FirebaseAuth

class VenueViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    var binding=VenueCardBinding.bind(itemView)

    val user=FirebaseAuth.getInstance().currentUser

    fun bind(venue: Venue) {

        binding.apply {
            venueTitleCard.text = venue.Name
            venueDescriptionCard.text = venue.Description
            landmarkCard.text = venue.Landmark
            cityCard.text = venue.City
            stateCard.text = venue.State
            capacityCard.text = venue.VenueCapacity
            parkingCard.text = venue.Parking
            availableTimeCard.text = venue.Availability
            typesCard.text = venue.Types.removePrefix("[").removeSuffix("]")

            val imageUrl = if(venue.url0 != "") venue.url0 else R.drawable.logo

            // Load image into ImageView using Glide library
            Glide.with(itemView)
                .load(imageUrl)
                .into(venueHeadImage)

        }
    }
}
