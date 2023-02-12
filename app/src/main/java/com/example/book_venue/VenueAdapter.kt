package com.example.book_venue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class VenueAdapter(private val items:ArrayList<Venue>) : RecyclerView.Adapter<MyViewHolder>() {

    private lateinit var activtiy:ViewVenueActivity
    private lateinit var mList:ArrayList<Venue>
    private val db:FirebaseFirestore=FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.venue_card, parent, false)
        return MyViewHolder(view)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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
                    //Toast.makeText(activtiy,"Deleted",Toast.LENGTH_SHORT).show()
                }
                else {
                    //Toast.makeText(activtiy,it.exception,Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun notifyRemoved(position: Int){
        items.removeAt(position)
        notifyRemoved(position)
    }
}
