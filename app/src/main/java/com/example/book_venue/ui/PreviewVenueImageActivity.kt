package com.example.book_venue.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.book_venue.adapters.VenueImageAdapter
import com.example.book_venue.data.Venue
import com.example.book_venue.databinding.ActivityPreviewVenueImageBinding

class PreviewVenueImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewVenueImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewVenueImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create a list to hold the image data
        val imageList = ArrayList<Venue>()

        val adapter = VenueImageAdapter(imageList)
        binding.venueImageRecycler.layoutManager = LinearLayoutManager(this@PreviewVenueImageActivity)
        binding.venueImageRecycler.adapter = adapter
    }
}
