package com.example.book_venue.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.book_venue.databinding.BookedCardBinding
import com.example.book_venue.model.Booked
import com.example.book_venue.model.BookingViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class BookingAdapter(
    //private var context: Context,
    private var items: ArrayList<Booked>
) : RecyclerView.Adapter<BookingViewHolder>() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: FirebaseUser = auth.currentUser!!


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding= BookedCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  BookingViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val currentCard=items[position]
        holder.bind(currentCard)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
