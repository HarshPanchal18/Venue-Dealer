package com.example.book_venue

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.venue_card.view.*

class VenueAdapter(val venueList:ArrayList<Venue>,val context: Context)
    : RecyclerView.Adapter<VenueAdapter.venueViewHolder>() {

    class venueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): venueViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.venue_card,parent,false)
        return venueViewHolder(view)
    }

    override fun onBindViewHolder(holder: venueViewHolder, position: Int) {
        val venue=venueList[position]
        //with(holder){
        holder.itemView.venueTitle.text=venue.title
        holder.itemView.venueDescription.text=venue.desc
        //}
        /*holder.title.text=model.title
        holder.location.text=model.location
        holder.city.text=model.city
        holder.state.text=model.state
        holder.availableTime.text= model.availableTime.toString()
        //holder.dealerName.text=Firebase
        holder.dealerContact.text=model.dealerContact
        holder.venueTypes.text=model.venueType.toString()*/
    }

    override fun getItemCount(): Int {
        return venueList.size
    }
}
