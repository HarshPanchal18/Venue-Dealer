package com.example.book_venue.ui

import android.annotation.SuppressLint
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
import com.example.book_venue.adapters.BookingAdapter
import com.example.book_venue.databinding.ActivityHomeBinding
import com.example.book_venue.model.Booked
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.system.exitProcess

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var binding : ActivityHomeBinding
    private lateinit var adapter: BookingAdapter
    private var bookedCardList = ArrayList<Booked>()
    private lateinit var db: FirebaseFirestore
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        auth=FirebaseAuth.getInstance()
        user= auth.currentUser!!
        db = FirebaseFirestore.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val imageURI:String = if(user.photoUrl==null)
            resources.getResourceName(R.drawable.logo)
        else
            user.photoUrl.toString()

        Glide.with(this).load(imageURI).into(binding.userPhoto)

        bookedCardList= ArrayList()
        adapter=BookingAdapter(bookedCardList)
        binding.confirmPager.adapter=adapter

        loadBookingsFromDb(user.uid)

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
            startActivity(Intent(this, AboutAppActivity::class.java))
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

    private fun loadBookingsFromDb(user: String) {
        try {
            val ref = db.collection("cbooking")
            ref//.whereEqualTo("userId", user)
                .get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty) {
                        //binding.venueRecycler.visibility = View.INVISIBLE // for in case of deletion of a single remained card
                        binding.confirmTxt.visibility = View.VISIBLE
                        return@addOnSuccessListener
                    }
                    //binding.loadingVenue.visibility = View.GONE

                    refreshAdapter(bookedCardList)
                    for (doc in result) {
                        val bookingModel = doc.toObject(Booked::class.java)
                        bookedCardList.add(bookingModel)
                    }
                    adapter.notifyDataSetChanged()
                }
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshAdapter(list: ArrayList<Booked>) {
        adapter = BookingAdapter(/*this,*/list)
        binding.confirmPager.adapter = adapter
    }

    // Ask again for exit
    private var backPressedTime: Long = 0
    override fun onBackPressed() {

        if(backPressedTime+2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            exitProcess(0)
        }

        Toast.makeText(this,"Press again to exit",Toast.LENGTH_SHORT).show()
        backPressedTime = System.currentTimeMillis()
    }
}
