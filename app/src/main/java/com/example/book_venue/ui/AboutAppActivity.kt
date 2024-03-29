package com.example.book_venue.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.book_venue.databinding.ActivityAboutAppBinding

class AboutAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goHomeBtn.setOnClickListener {
            finish()
        }
        binding.devMailBtn.setOnClickListener {
            val mailIntent = Intent(Intent.ACTION_SENDTO)
            val data = Uri.parse("mailto:harshhhh1803@gmail.com")
            mailIntent.data = data
            startActivity(mailIntent)
        }
    }
}
