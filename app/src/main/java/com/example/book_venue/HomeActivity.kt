package com.example.book_venue

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_home.*
import kotlin.system.exitProcess

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
                //Toast.makeText(this,"Logged out Successfully", Toast.LENGTH_SHORT).show()
            }
        }

        addVenuebtn.setOnClickListener {
            startActivity(Intent(this,AddVenueActivity::class.java))
        }

        viewVenuebtn.setOnClickListener {
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
