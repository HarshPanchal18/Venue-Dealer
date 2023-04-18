package com.example.book_venue.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.book_venue.R
import com.example.book_venue.databinding.BookedCardBinding
import com.example.book_venue.databinding.SuccessDialogBinding
import com.example.book_venue.databinding.WarningDialogBinding
import com.example.book_venue.data.Booked
import com.example.book_venue.viewModel.BookingViewHolder
import com.example.book_venue.ui.HomeActivity
import com.google.firebase.firestore.FirebaseFirestore

class BookingAdapter() : RecyclerView.Adapter<BookingViewHolder>() {
    private lateinit var db: FirebaseFirestore
    private var activity: HomeActivity = HomeActivity()
    private lateinit var context: Context
    private var items = ArrayList<Booked>()

    constructor(activity: HomeActivity, context: Context, items: ArrayList<Booked>) : this() {
        this.activity = activity
        this.context = context
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = BookedCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding.root)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val currentCard: Booked = items[position]
        holder.bind(currentCard)
        db = FirebaseFirestore.getInstance()

        val currentItem: Booked = items[position]
        holder.binding.cancelBookingButton.setOnClickListener {

            val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
            val wbinding: WarningDialogBinding =
                WarningDialogBinding.bind(LayoutInflater.from(context)
                    .inflate(R.layout.warning_dialog,
                        holder.itemView.findViewById<ConstraintLayout>(R.id.layoutDialogContainer)))

            wbinding.apply {
                builder.setView(root)
                textTitle.text = context.resources.getString(R.string.warning_title)
                textMessage.text =
                    "Are you sure you want to delete this booking??\tActions like this can be serious and irreversible."
                buttonYes.text = context.resources.getString(R.string.yes)
                buttonNo.text = context.resources.getString(R.string.no)
                imageIcon.setImageResource(R.drawable.warning)

                val alertDialog = builder.create()
                buttonYes.setOnClickListener {
                    alertDialog.dismiss()
                    db.collection("cbooking").document(currentItem.bookingId).delete()
                        .addOnSuccessListener {
                            showSuccessDialog("The booking was deleted")
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

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyRemoved(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        activity.binding.confirmPager.adapter?.notifyDataSetChanged()

        if (items.isEmpty()) {
            activity.binding.confirmTxt.visibility = View.VISIBLE
            activity.binding.bookingHead.visibility = View.INVISIBLE
        }
    }

    private fun showSuccessDialog(message: String) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        val sbinding: SuccessDialogBinding = SuccessDialogBinding.bind(LayoutInflater.from(context)
            .inflate(R.layout.success_dialog,
                (context as Activity).findViewById<ConstraintLayout>(R.id.layoutDialogContainer)))

        builder.setView(sbinding.root)
        sbinding.textTitle.text = context.resources.getString(R.string.success_title)
        sbinding.textMessage.text = message
        sbinding.buttonAction.text = context.resources.getString(R.string.okay)
        sbinding.imageIcon.setImageResource(R.drawable.done)

        val alertDialog = builder.create()
        sbinding.buttonAction.setOnClickListener { alertDialog.dismiss() }

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}
