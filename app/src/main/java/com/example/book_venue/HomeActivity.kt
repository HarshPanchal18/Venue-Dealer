package com.example.book_venue

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportActionBar?.hide()
        auth=FirebaseAuth.getInstance()
        user= auth.currentUser!!

        account_name.text="${user.displayName}\n${user.email}"

        logoutbtn.setOnClickListener {
            auth.signOut()
            Intent(this,MainActivity::class.java).also {
                it.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                Toast.makeText(this,"Logged out Successfully", Toast.LENGTH_SHORT).show()
            }
        }

        addVenuebtn.setOnClickListener {
            startActivity(Intent(this,AddVenueActivity::class.java))
        }

        viewVenuebtn.setOnClickListener {
            startActivity(Intent(this,ViewVenueActivity::class.java))
        }
    }}
