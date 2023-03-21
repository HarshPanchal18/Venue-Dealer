package com.example.book_venue.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.book_venue.databinding.BookedCardBinding
import com.example.book_venue.model.Booked
import com.example.book_venue.model.BookingViewHolder
import com.example.book_venue.ui.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class BookingAdapter() : RecyclerView.Adapter<BookingViewHolder>() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: FirebaseUser = auth.currentUser!!
    private lateinit var db: FirebaseFirestore
    private var activity : HomeActivity = HomeActivity()
    private lateinit var context: Context
    private lateinit var items: ArrayList<Booked>

    constructor( activity: HomeActivity, context: Context, items: ArrayList<Booked>) : this() {
        this.activity = activity
        this.context = context
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding= BookedCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  BookingViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val currentCard: Booked = items[position]
        holder.bind(currentCard)
        db = FirebaseFirestore.getInstance()

        holder.binding.bookingUsername.setOnClickListener {
            Toast.makeText(context,currentCard.username,Toast.LENGTH_SHORT).show()
        }

        val currentItem: Booked = items[position]
        holder.binding.cancelBookingButton.setOnClickListener {
            db.collection("cbooking").document(currentItem.bookingId).delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                    notifyRemoved(position)
                }
                .addOnFailureListener { Toast.makeText(context,it.message.toString(),Toast.LENGTH_SHORT).show() }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyRemoved(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        activity.binding.confirmPager.adapter?.notifyDataSetChanged()
    }

}
