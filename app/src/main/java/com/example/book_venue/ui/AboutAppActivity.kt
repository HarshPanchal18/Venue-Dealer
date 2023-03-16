package com.example.book_venue.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.inflate
import com.example.book_venue.R
import com.example.book_venue.databinding.ActivityAboutAppBinding.inflate
import com.example.book_venue.databinding.ActivityAddVenueBinding

class AboutAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddVenueBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddVenueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val colorDrawable = ColorDrawable(Color.parseColor("#FF5693FD"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title=resources.getString(R.string.app_name)
    }
}
