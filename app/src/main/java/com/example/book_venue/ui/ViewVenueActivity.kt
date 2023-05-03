package com.example.book_venue.ui

import android.annotation.SuppressLint
import android.content.Context
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
import com.example.book_venue.data.Venue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ViewVenueActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: VenueAdapter
    private var venues = ArrayList<Venue>()
    lateinit var binding: ActivityViewVenueBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewVenueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.viewVenueToolbar)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        db = FirebaseFirestore.getInstance()

        binding.goBack.setOnClickListener { finish() }
        venues = ArrayList()
        adapter = VenueAdapter(this, this, venues)
        binding.venueRecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ViewVenueActivity)
            adapter = adapter
        }

        val touchHelper = ItemTouchHelper(TouchHelper(adapter))
        touchHelper.attachToRecyclerView(binding.venueRecycler)

        loadVenuesFromDb(user.uid)

        binding.searchVenue.apply {
            clearFocus()
            setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

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
        val filteredList = arrayListOf<Venue>()
        for (item in venues)
            if (item.Name.lowercase().contains(searchKeyword?.lowercase()!!)
                || item.City.lowercase().contains(searchKeyword.lowercase())
                || item.Types.lowercase().contains(searchKeyword.lowercase())
                || item.State.lowercase().contains(searchKeyword.lowercase())
                || item.Description.lowercase().contains(searchKeyword.lowercase())
            ) {
                filteredList.add(item)
            }

        if (filteredList.isEmpty())
            this.showToast("No venues found for $searchKeyword")

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
                        binding.venueRecycler.invisible() // for in case of deletion of a single remained card
                        binding.zeroVenues.visible()
                        binding.loadingVenue.gone()
                        return@addOnSuccessListener
                    }
                    binding.loadingVenue.gone()

                    refreshAdapter(venues)
                    for (doc in result) {
                        val venueModel = doc.toObject(Venue::class.java)
                        venues.add(venueModel)
                    }
                    adapter.notifyDataSetChanged()
                }
        } catch (e: Exception) {
            this.showToast(e.message.toString())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshAdapter(list: ArrayList<Venue>) {
        adapter = VenueAdapter(this, this, list)
        binding.venueRecycler.adapter = adapter
    }

    private fun View.visible() {
        visibility = View.VISIBLE
    }

    private fun View.invisible() {
        visibility = View.INVISIBLE
    }

    private fun View.gone() {
        visibility = View.GONE
    }

    private fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
