package com.example.book_venue

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_view_venue.*

class VenueAdapter() : RecyclerView.Adapter<VenueViewHolder>() {

    private var activity:ViewVenueActivity = ViewVenueActivity()
    private val db:FirebaseFirestore=FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: FirebaseUser = auth.currentUser!!
    private lateinit var items: ArrayList<Venue>

    constructor( activity: ViewVenueActivity,  items:ArrayList<Venue>) : this() {
        this.activity=activity
        this.items=items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.venue_card, parent, false)
        return VenueViewHolder(view)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun deleteData(position: Int){
        val item:Venue=items[position]
        db.collection("venue").document(item.docId).delete()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    notifyRemoved(position)
                    Toast.makeText(activity, "Data Deleted !!", Toast.LENGTH_SHORT).show();
                }
            }
    }

    fun updateData(position: Int){
        val item:Venue=items[position]
        val bundle=Bundle().apply {
            putString("docId",item.docId)
            putString("name",item.Name)
            putString("description",item.Description)
            putString("landmark",item.Landmark)
            putString("city",item.City)
            putString("state",item.State)
            putString("capacity",item.VenueCapacity)
            putString("available",item.Availability)
            putString("dealerPh",item.Dealer_Ph)
            putString("rentHour",item.RentPerHour)
            putString("types",item.Types)
            putString("parking",item.Parking)
        }

        val intent= Intent(activity,AddVenueActivity::class.java)
        intent.putExtras(bundle)
        activity.startActivity(intent)
    }

    private fun notifyRemoved(position: Int){
        items.removeAt(position)
        //notifyDataSetChanged()
        activity.venueRecycler.adapter?.notifyItemRemoved(position)
        //activity.loadVenuesFromDb(user.uid)
        activity.restart()
    }
}
