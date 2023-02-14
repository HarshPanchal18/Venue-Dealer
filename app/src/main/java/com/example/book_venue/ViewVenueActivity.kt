package com.example.book_venue

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
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


        venueRecycler.apply {
            setHasFixedSize(true)
            layoutManager=LinearLayoutManager(this@ViewVenueActivity)
            //adapter=adapter
        }

        venues= ArrayList()
        adapter=VenueAdapter(venues)
        venueRecycler.adapter=adapter

        val touchHelper = ItemTouchHelper(TouchHelper(adapter))
        touchHelper.attachToRecyclerView(venueRecycler)

        loadVenuesFromDb(user.uid)

        addVenueFAB.setOnClickListener {
            startActivity(Intent(this,AddVenueActivity::class.java))
            finish()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun loadVenuesFromDb(user: String) {
        try {
            val ref = db.collection("venue")
            ref.whereEqualTo("userId", user)
                .get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty) {
                        Toast.makeText(this@ViewVenueActivity, "No venue found", Toast.LENGTH_SHORT).show()
                        zeroVenues.visibility = View.VISIBLE
                        addVenueFAB.visibility = View.VISIBLE
                        return@addOnSuccessListener
                    }

                    for (doc in result) {
                        val venueModel = doc.toObject(Venue::class.java)
                        venues.add(venueModel)
                    }

                    refreshAdapter(venues)
                    adapter.notifyDataSetChanged()
                    /*venueRecycler.apply {
                    layoutManager=LinearLayoutManager(this@ViewVenueActivity)
                    adapter=adapter//VenueAdapter(venueList)//, this@ViewVenueActivity)
                }*/
                }
        } catch (e:Exception){
            Toast.makeText(this,e.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshAdapter(list : ArrayList<Venue>) {
        adapter = VenueAdapter(list)
        venueRecycler.adapter = adapter
    }
}
