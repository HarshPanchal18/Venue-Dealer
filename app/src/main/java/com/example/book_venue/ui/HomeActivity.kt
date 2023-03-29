package com.example.book_venue.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.book_venue.MainActivity
import com.example.book_venue.R
import com.example.book_venue.adapters.BookingAdapter
import com.example.book_venue.adapters.PendingAdapter
import com.example.book_venue.databinding.ActivityHomeBinding
import com.example.book_venue.databinding.ErrorDialogBinding
import com.example.book_venue.databinding.WarningDialogBinding
import com.example.book_venue.model.Booked
import com.example.book_venue.model.Pending
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.system.exitProcess

@SuppressLint("NotifyDataSetChanged")
class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    lateinit var binding : ActivityHomeBinding
    lateinit var adapterBooking: BookingAdapter
    private lateinit var adapterPending: PendingAdapter
    private lateinit var bookedCardList: ArrayList<Booked>
    private lateinit var pendingCardList: ArrayList<Pending>
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeObjects()
        loadBookingsFromDb(user.uid)
        loadPendingsFromDb(user.uid)

    }

    private fun loadBookingsFromDb(user: String) {
        try {
            val ref = db.collection("cbooking")
            ref.whereEqualTo("dealerid", user)
                .orderBy("enddate")
                .whereGreaterThanOrEqualTo("enddate", Timestamp.now())
                .get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty) {
                        //binding.venueRecycler.visibility = View.INVISIBLE // for in case of deletion of a single remained card
                        binding.confirmTxt.visible()
                        binding.bookingsLoading.gone()
                        binding.bookingsLoadingText.gone()
                        return@addOnSuccessListener
                    }
                    binding.bookingsLoading.gone()
                    binding.bookingsLoadingText.gone()
                    binding.bookingHead.visible()

                    adapterBooking = BookingAdapter(this, this,bookedCardList)
                    binding.confirmPager.adapter = adapterBooking
                    for (doc in result) {
                        val bookingModel = doc.toObject(Booked::class.java)
                        bookedCardList.add(bookingModel)
                    }
                    adapterBooking.notifyDataSetChanged()
                }
                .addOnFailureListener {
                    showErrorDialog(it.message.toString())
                }
        } catch (e: Exception) {
            showErrorDialog(e.message.toString())
        }
    } // end of loadingBookingsFromDb()

    private fun loadPendingsFromDb(user: String) {
        try {
            val ref = db.collection("pbooking")
            ref.whereEqualTo("dealerid", user)
                .whereGreaterThanOrEqualTo("startdate", Timestamp.now())
                .orderBy("startdate")
                .get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty) {
                        //binding.venueRecycler.visibility = View.INVISIBLE // for in case of deletion of a single remained card
                        binding.pendingTxt.visible()
                        binding.bookingsLoading.gone()
                        binding.bookingsLoadingText.gone()
                        return@addOnSuccessListener
                    }
                    binding.bookingsLoading.gone()
                    binding.bookingsLoadingText.gone()
                    binding.pendingHead.visible()

                    adapterPending = PendingAdapter(this, this,pendingCardList)
                    binding.pendingPager.adapter = adapterPending
                    for (doc in result) {
                        val pendingModel = doc.toObject(Pending::class.java)
                        pendingCardList.add(pendingModel)
                    }
                    adapterPending.notifyDataSetChanged()
                }
                .addOnFailureListener {
                    showErrorDialog(it.message.toString())
                }
        } catch (e: Exception) {
            showErrorDialog(e.message.toString())
        }
    } // end of loadingBookingsFromDb()

    private fun initializeObjects() {
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        db = FirebaseFirestore.getInstance()

        bookedCardList = ArrayList()
        adapterBooking = BookingAdapter(this, this, bookedCardList)
        binding.confirmPager.adapter = adapterBooking

        pendingCardList = ArrayList()
        adapterPending = PendingAdapter(this, this, pendingCardList)
        binding.pendingPager.adapter = adapterPending

        binding.apply {
            userPhoto.loadImage(user.photoUrl.toString())
            accountName.text = user.displayName
            accountMail.text = user.email

            accountName.setOnClickListener {
                Handler(Looper.getMainLooper()).postDelayed({
                    homeTitle.setTextColor(Color.BLACK)
                    homeSubtitle.setTextColor(Color.BLACK)
                    accountName.setTextColor(Color.BLACK)
                    accountMail.gone()
                }, 1800)
                accountMail.visible()
                homeTitle.setTextColor(Color.WHITE)
                homeSubtitle.setTextColor(Color.WHITE)
                accountName.setTextColor(Color.WHITE)
            }

            // disabling overScrollMode programmatically
            var child: View = confirmPager.getChildAt(0)
            (child as? RecyclerView)?.overScrollMode = View.OVER_SCROLL_NEVER
            child = pendingPager.getChildAt(0)
            (child as? RecyclerView)?.overScrollMode = View.OVER_SCROLL_NEVER

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
            val wbinding: WarningDialogBinding = WarningDialogBinding.bind(LayoutInflater.from(this)
                .inflate(R.layout.warning_dialog,
                    findViewById<ConstraintLayout>(R.id.layoutDialogContainer)))

            builder.setView(wbinding.root)
            wbinding.textTitle.text = resources.getString(R.string.warning_title)
            wbinding.textMessage.text = resources.getString(R.string.logout_text)
            wbinding.buttonYes.text = resources.getString(R.string.yes)
            wbinding.buttonNo.text = resources.getString(R.string.no)
            wbinding.imageIcon.setImageResource(R.drawable.warning)

            val alertDialog = builder.create()
            wbinding.buttonYes.setOnClickListener {
                alertDialog.dismiss()
                auth.signOut()
                Intent(this, MainActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }
            wbinding.buttonNo.setOnClickListener {
                alertDialog.dismiss()
            }

            if (alertDialog.window != null) {
                alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }

            alertDialog.setCancelable(false)
            alertDialog.show()
        } // end of logout button

    }

    // Ask again for exit
    private var backPressedTime: Long = 0
    override fun onBackPressed() {

        if(backPressedTime+2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            exitProcess(0)
        }
        this.showToast("Press again to exit")
        backPressedTime = System.currentTimeMillis()
    }

    private fun View.visible() {
        visibility = View.VISIBLE
    }

    private fun View.gone() {
        visibility = View.GONE
    }

    private fun ImageView.loadImage(url: String?) {
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.logo04)
            .error(R.drawable.error)
            .into(this)
    }

    private fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showErrorDialog(message: String) {
        val builder = android.app.AlertDialog.Builder(this, R.style.AlertDialogTheme)
        val ebinding: ErrorDialogBinding =
            ErrorDialogBinding.bind(LayoutInflater.from(this)
                .inflate(R.layout.error_dialog,
                    this.findViewById<ConstraintLayout>(R.id.layoutDialogContainer)))

        ebinding.apply {
            builder.setView(root)
            textTitle.text = resources.getString(R.string.network_error_title)
            textMessage.text = message
            buttonAction.text = resources.getString(R.string.okay)
            imageIcon.setImageResource(R.drawable.error)

            val alertDialog = builder.create()
            buttonAction.setOnClickListener { alertDialog.dismiss() }

            if (alertDialog.window != null) {
                alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }

            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }

    override fun onRestart() {
        super.onRestart()

        bookedCardList = ArrayList()
        adapterBooking = BookingAdapter(this, this, bookedCardList)
        binding.confirmPager.adapter = adapterBooking

        pendingCardList = ArrayList()
        adapterPending = PendingAdapter(this, this, pendingCardList)
        binding.pendingPager.adapter = adapterPending

        loadBookingsFromDb(user.uid)
        loadPendingsFromDb(user.uid)
    }
}
