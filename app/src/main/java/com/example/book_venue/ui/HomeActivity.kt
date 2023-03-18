package com.example.book_venue.ui

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.book_venue.MainActivity
import com.example.book_venue.R
import com.example.book_venue.databinding.ActivityHomeBinding
import com.example.book_venue.model.Booked
import com.example.book_venue.model.BookingViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
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

        val imageURI:String = if(user.photoUrl==null)
            resources.getResourceName(R.drawable.logo)
        else
            user.photoUrl.toString()

        Glide.with(this).load(imageURI).into(binding.userPhoto)

        val collectionRef = FirebaseFirestore.getInstance().collection("cbooking")
        val options = FirestoreRecyclerOptions.Builder<Booked>()
            .setQuery(collectionRef, Booked::class.java).build()

        //val adapter= FirestoreRecyclerOptions<Booked,BookingViewHolder>(options){}

        binding.apply {
            accountName.text = user.displayName
            accountMail.text = user.email

            accountName.setOnClickListener {
                Handler().postDelayed({
                    homeTitle.visibility = View.VISIBLE
                    accountName.visibility = View.VISIBLE
                    accountMail.visibility = View.GONE
                },1800)
                homeTitle.visibility = View.GONE
                accountName.visibility = View.GONE
                accountMail.visibility = View.VISIBLE
            }
        }

        binding.addVenue.setOnClickListener {
            startActivity(Intent(this, AddVenueActivity::class.java))
        }

        binding.viewVenue.setOnClickListener {
            startActivity(Intent(this, ViewVenueActivity::class.java))
        }

        binding.aboutDevBtn.setOnClickListener {
            startActivity(Intent(this,AboutAppActivity::class.java))
        }

        binding.logoutbtn.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
            val view: View = LayoutInflater.from(this)
                .inflate(R.layout.warning_dialog, findViewById<ConstraintLayout>(R.id.layoutDialogContainer))
            builder.setView(view)
            (view.findViewById<View>(R.id.textTitle) as TextView).text = resources.getString(R.string.warning_title)
            (view.findViewById<View>(R.id.textMessage) as TextView).text = resources.getString(R.string.logout_text)
            (view.findViewById<View>(R.id.buttonYes) as Button).text = resources.getString(R.string.yes)
            (view.findViewById<View>(R.id.buttonNo) as Button).text = resources.getString(R.string.no)
            (view.findViewById<View>(R.id.imageIcon) as ImageView).setImageResource(R.drawable.warning)

            val alertDialog = builder.create()
            view.findViewById<View>(R.id.buttonYes).setOnClickListener {
                alertDialog.dismiss()
                auth.signOut()
                Intent(this, MainActivity::class.java).also {
                    it.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }
            view.findViewById<View>(R.id.buttonNo).setOnClickListener {
                alertDialog.dismiss()
            }

            if (alertDialog.window != null) {
                alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }

            alertDialog.setCancelable(false)
            alertDialog.show()
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
