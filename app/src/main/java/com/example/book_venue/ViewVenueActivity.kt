package com.example.book_venue

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_view_venue.*

class ViewVenueActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    lateinit var db:FirebaseFirestore
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: VenueAdapter
    private var venues = ArrayList<Venue>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_venue)

        supportActionBar?.hide()
        auth= FirebaseAuth.getInstance()
        user= auth.currentUser!!
        firestore=FirebaseFirestore.getInstance()
        db = FirebaseFirestore.getInstance()

        venues= ArrayList()
        adapter=VenueAdapter(venues)

        //loadVenuesFromDb(user.uid)

        venueRecycler.apply {
            layoutManager=LinearLayoutManager(this@ViewVenueActivity)
            adapter=adapter
        }

        try {
            val ref=db.collection("venue")
            ref.whereEqualTo("userId",user.uid)
                .get()
                .addOnSuccessListener { result ->

                    if(result.isEmpty){
                        Toast.makeText(this@ViewVenueActivity,"No venue found",Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    for(doc in result){
                        val venueModel=doc.toObject(Venue::class.java)
                        venues.add(venueModel)
                    }
                    refreshAdapter(venues)
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener {
                    Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT).show()
                }
        } catch (e:Exception){
            Toast.makeText(this,e.message.toString(),Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadVenuesFromDb(user: String) {
        val venueList=ArrayList<Venue>()

        val ref=db.collection("venue")
        ref.whereEqualTo("userId",user)
            .get()
            .addOnSuccessListener { result ->
            if(result.isEmpty){
                Toast.makeText(this@ViewVenueActivity,"No venue found",Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }
            for(doc in result){
                val venueModel=doc.toObject(Venue::class.java)
                venueList.add(venueModel)
            }
                adapter.notifyDataSetChanged()
            venueRecycler.apply {
                layoutManager=LinearLayoutManager(this@ViewVenueActivity)
                adapter=adapter//VenueAdapter(venueList)//, this@ViewVenueActivity)
            }
        }
    }

    private fun refreshAdapter(list : ArrayList<Venue>) {
        adapter = VenueAdapter(list)
        venueRecycler.adapter = adapter
    }
}
