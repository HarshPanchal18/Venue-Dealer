package com.example.book_venue

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.book_venue.databinding.ActivityHomeBinding
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
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            //positive action
            builder.setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                Intent(this,MainActivity::class.java).also {
                    it.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }

            //cancel action
            builder.setNegativeButton("No")
            { _, _ -> Toast.makeText(applicationContext, "Welcome Back :)", Toast.LENGTH_SHORT).show() }

            //create the alert dialog
            val alertDialog: AlertDialog = builder.create()

            //other properties
            alertDialog.setCancelable(false)
            alertDialog.show()

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
