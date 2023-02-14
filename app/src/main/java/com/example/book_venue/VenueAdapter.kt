package com.example.book_venue

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class VenueAdapter(private val items:ArrayList<Venue>) : RecyclerView.Adapter<VenueViewHolder>() {

    private lateinit var activtiy:ViewVenueActivity
    private val db:FirebaseFirestore=FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.venue_card, parent, false)
        return VenueViewHolder(view)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        holder.bind(items[position])
        auth= FirebaseAuth.getInstance()
        user= auth.currentUser!!
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun deleteData(position: Int){
        val item:Venue=items[position]
        db.collection("venue").document(item.docId).delete()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    //Toast.makeText(activtiy,"Deleted",Toast.LENGTH_SHORT).show()
                    notifyRemoved(position)
                }
                else {
                    //Toast.makeText(this,it.exception,Toast.LENGTH_SHORT).show()
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

        val intent= Intent(ViewVenueActivity(),AddVenueActivity::class.java)
        intent.putExtras(bundle)
        ViewVenueActivity().startActivity(intent)
    }

    fun notifyRemoved(position: Int){
        items.removeAt(position)
        notifyItemRemoved(position)
        //activtiy= ViewVenueActivity()
        //activtiy.loadVenuesFromDb(user.uid)
    }
}
