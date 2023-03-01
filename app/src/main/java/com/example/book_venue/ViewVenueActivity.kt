package com.example.book_venue

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.book_venue.databinding.ActivityViewVenueBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ViewVenueActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var db:FirebaseFirestore
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: VenueAdapter
    private var venues = ArrayList<Venue>()
    private lateinit var binding: ActivityViewVenueBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityViewVenueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        auth= FirebaseAuth.getInstance()
        user= auth.currentUser!!
        firestore=FirebaseFirestore.getInstance()
        db = FirebaseFirestore.getInstance()

        venues= ArrayList()
        adapter=VenueAdapter(this,this,venues)
        binding.venueRecycler.apply {
            setHasFixedSize(true)
            layoutManager=LinearLayoutManager(this@ViewVenueActivity)
            adapter=adapter
        }

        val touchHelper = ItemTouchHelper(TouchHelper(adapter))
        touchHelper.attachToRecyclerView(binding.venueRecycler)

        loadVenuesFromDb(user.uid)

        binding.addVenueFAB.setOnClickListener {
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
                        binding.venueRecycler.visibility=View.INVISIBLE // for in case of delete card
                        binding.zeroVenues.visibility = View.VISIBLE
                        binding.addVenueFAB.visibility = View.VISIBLE
                        return@addOnSuccessListener
                    }

                    refreshAdapter(venues)
                    for (doc in result) {
                        val venueModel = doc.toObject(Venue::class.java)
                        venues.add(venueModel)
                    }
                    adapter.notifyDataSetChanged()
                }
        } catch (e:Exception){
            Toast.makeText(this,e.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshAdapter(list : ArrayList<Venue>) {
        adapter = VenueAdapter(this,this,list)
        //adapter.notifyItemRemoved()
        binding.venueRecycler.adapter = adapter
    }

    fun restart(){
        finish()
        startActivity(intent)
        overridePendingTransition(0,0) // Call immediately after one of the flavors of #startActivity(Intent) or #finish to specify an explicit transition animation to perform next.
    }
}
