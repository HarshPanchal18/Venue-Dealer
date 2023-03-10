package com.example.book_venue.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.book_venue.adapters.VenueAdapter
import com.example.book_venue.databinding.ActivityViewVenueBinding
import com.example.book_venue.helper.TouchHelper
import com.example.book_venue.model.Venue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore

class ViewVenueActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var db:FirebaseFirestore
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: VenueAdapter
    private var venues = ArrayList<Venue>()
    lateinit var binding: ActivityViewVenueBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityViewVenueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.viewVenueToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth= FirebaseAuth.getInstance()
        user= auth.currentUser!!
        firestore=FirebaseFirestore.getInstance()
        db = FirebaseFirestore.getInstance()

        venues= ArrayList()
        adapter= VenueAdapter(this,this,venues)
        binding.venueRecycler.apply {
            setHasFixedSize(true)
            layoutManager=LinearLayoutManager(this@ViewVenueActivity)
            adapter=adapter
        }

        val touchHelper = ItemTouchHelper(TouchHelper(adapter))
        touchHelper.attachToRecyclerView(binding.venueRecycler)

        loadVenuesFromDb(user.uid)

        binding.searchVenue.apply {
            clearFocus()
            setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean { return false }

                override fun onQueryTextChange(searchKeyword: String?): Boolean {
                    filterList(searchKeyword)
                    return false
                }
            })
        }

        binding.addVenueFAB.setOnClickListener {
            startActivity(Intent(this, AddVenueActivity::class.java))
            finish()
        }
    }

    private fun filterList(searchKeyword: String?) {
        val filteredList= arrayListOf<Venue>()
        for(item in venues)
            if(item.Name.toLowerCase().contains(searchKeyword?.toLowerCase()!!)
                ||item.City.toLowerCase().contains(searchKeyword?.toLowerCase()!!)
                ||item.Types.toLowerCase().contains(searchKeyword?.toLowerCase()!!)
                ||item.State.toLowerCase().contains(searchKeyword?.toLowerCase()!!))
            { filteredList.add(item) }

        if(filteredList.isEmpty())
            Toast.makeText(this,"No venues found for $searchKeyword",Toast.LENGTH_SHORT).show()

        adapter.setFilterList(filteredList)

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
                        binding.loadingVenue.visibility = View.GONE
                        return@addOnSuccessListener
                    }
                    binding.loadingVenue.visibility = View.GONE

                    refreshAdapter(venues)
                    for (doc in result) {
                        val venueModel = doc.toObject(Venue::class.java)
                        venues.add(venueModel)
                    }
                    adapter.notifyDataSetChanged()
                }
        }
        catch (e:Exception) { Toast.makeText(this,e.message.toString(),Toast.LENGTH_SHORT).show() }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshAdapter(list : ArrayList<Venue>) {
        adapter = VenueAdapter(this,this,list)
        //adapter.notifyItemRemoved()
        binding.venueRecycler.adapter = adapter
    }

}
