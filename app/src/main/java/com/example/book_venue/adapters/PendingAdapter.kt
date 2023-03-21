package com.example.book_venue.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.book_venue.databinding.PendingCardBinding
import com.example.book_venue.model.Pending
import com.example.book_venue.model.PendingViewHolder
import com.example.book_venue.ui.HomeActivity
import com.google.firebase.firestore.FirebaseFirestore

class PendingAdapter() :  RecyclerView.Adapter<PendingViewHolder>() {
    private lateinit var db: FirebaseFirestore
    private var activity : HomeActivity = HomeActivity()
    private lateinit var context: Context
    private lateinit var items: ArrayList<Pending>

    constructor( activity: HomeActivity, context: Context, items: ArrayList<Pending>) : this() {
        this.activity = activity
        this.context = context
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingViewHolder {
        val binding = PendingCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  PendingViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: PendingViewHolder, position: Int) {
        val currentCard: Pending = items[position]
        holder.bind(currentCard)
        db = FirebaseFirestore.getInstance()

        holder.binding.acceptButton.setOnClickListener {
            Toast.makeText(context,"Accepted",Toast.LENGTH_SHORT).show()
        }

        val currentItem: Pending = items[position]
        holder.binding.rejectButton.setOnClickListener {
            db.collection("pbooking").document(currentItem.requestId).delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show()
                    notifyRemoved(position)
                }
                .addOnFailureListener { Toast.makeText(context,it.message.toString(), Toast.LENGTH_SHORT).show() }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyRemoved(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        activity.binding.pendingPager.adapter?.notifyDataSetChanged()
    }

}
