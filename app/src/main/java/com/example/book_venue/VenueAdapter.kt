package com.example.book_venue

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.venue_card.view.*

class VenueAdapter(val venueList:ArrayList<Venue>, val context: Context)
    : RecyclerView.Adapter<VenueAdapter.venueViewHolder>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    inner class venueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): venueViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.venue_card,parent,false)
        return venueViewHolder(view)
    }

    override fun onBindViewHolder(holder: venueViewHolder, position: Int) {

        auth= FirebaseAuth.getInstance()
        user= auth.currentUser!!

        val venue=venueList[position]
        holder.itemView.venueTitle.text=venue.title
        holder.itemView.venueDescription.text=venue.desc
        holder.itemView.Location.text=venue.location
        holder.itemView.City.text=venue.city
        holder.itemView.State.text=venue.state
        holder.itemView.availableTime.text=venue.availableTime.toString()
        holder.itemView.nameDealer.text=user.displayName
        holder.itemView.contactDealer.text=venue.dealerContact
        holder.itemView.types.text= venue.venueType.toString()
    }

    override fun getItemCount(): Int {
        return venueList.size
    }
}
