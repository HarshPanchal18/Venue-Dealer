package com.example.book_venue.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.book_venue.R
import com.example.book_venue.databinding.BookedCardBinding
import com.example.book_venue.databinding.WarningDialogBinding
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
    var items = ArrayList<Booked>()

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

        val currentItem: Booked = items[position]
        holder.binding.cancelBookingButton.setOnClickListener {

            val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
            val binding: WarningDialogBinding =
                WarningDialogBinding.bind(LayoutInflater.from(context)
                    .inflate(R.layout.warning_dialog,
                        holder.itemView.findViewById<ConstraintLayout>(R.id.layoutDialogContainer)))

            binding.apply {
                builder.setView(root)
                textTitle.text = context.resources.getString(R.string.warning_title)
                textMessage.text =
                    "Are you sure you want to delete this booking??\nActions like this can be serious and irreversible."
                buttonYes.text = context.resources.getString(R.string.yes)
                buttonNo.text = context.resources.getString(R.string.no)
                imageIcon.setImageResource(R.drawable.warning)

                val alertDialog = builder.create()
                buttonYes.setOnClickListener {
                    alertDialog.dismiss()
                    db.collection("cbooking").document(currentItem.bookingId).delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                            notifyRemoved(position)
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                }

                buttonNo.setOnClickListener {
                    alertDialog.dismiss()
                }

                if (alertDialog.window != null) {
                    alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
                }

                alertDialog.setCancelable(false)
                alertDialog.show()
            }
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

        /*if(items.isEmpty()) {
            val intent = Intent(context, HomeActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            pendingIntent.send()
            activity.finish()
        }*/
    }

}
