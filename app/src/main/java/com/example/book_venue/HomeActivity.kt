package com.example.book_venue

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.book_venue.databinding.ActivityHomeBinding
import com.example.book_venue.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.system.exitProcess

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var binding : ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        auth=FirebaseAuth.getInstance()
        user= auth.currentUser!!

        binding.accountName.text="${user.displayName}\n${user.email}"

        binding.logoutbtn.setOnClickListener {
            auth.signOut()
            Intent(this,MainActivity::class.java).also {
                it.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                //Toast.makeText(this,"Logged out Successfully", Toast.LENGTH_SHORT).show()
            }
        }

        binding.addVenuebtn.setOnClickListener {
            startActivity(Intent(this,AddVenueActivity::class.java))
        }

        binding.viewVenuebtn.setOnClickListener {
            startActivity(Intent(this,ViewVenueActivity::class.java))
        }
    }

    // Ask again for exit
    private var backPressedTime:Long=0
    override fun onBackPressed() {

        if(backPressedTime+2000>System.currentTimeMillis()) {
            super.onBackPressed()
            exitProcess(0)
        }

        Toast.makeText(this,"Press again to exit",Toast.LENGTH_SHORT).show()
        backPressedTime= System.currentTimeMillis()
    }
}
