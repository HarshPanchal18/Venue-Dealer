package com.example.book_venue.adapters

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.book_venue.R
import com.example.book_venue.databinding.PendingCardBinding
import com.example.book_venue.databinding.WarningDialogBinding
import com.example.book_venue.model.Pending
import com.example.book_venue.model.PendingViewHolder
import com.example.book_venue.ui.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("NotifyDataSetChanged","CutPasteId", "SetTextI18n", "UnspecifiedImmutableFlag")
class PendingAdapter() :  RecyclerView.Adapter<PendingViewHolder>() {
    private lateinit var db: FirebaseFirestore
    private var activity : HomeActivity = HomeActivity()
    private lateinit var context: Context
    private lateinit var items: ArrayList<Pending>
    private lateinit var documentRef: DocumentReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

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
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        db = FirebaseFirestore.getInstance()

        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        val binding: WarningDialogBinding =
            WarningDialogBinding.bind(LayoutInflater.from(context).inflate(
                R.layout.warning_dialog,
                holder.itemView.findViewById<ConstraintLayout>(R.id.layoutDialogContainer)))
        builder.setView(binding.root)
        binding.textTitle.text = context.resources.getString(R.string.warning_title)
        binding.buttonYes.text = context.resources.getString(R.string.yes)
        binding.buttonNo.text = context.resources.getString(R.string.no)
        binding.imageIcon.setImageResource(R.drawable.warning)

        val alertDialog = builder.create()
        binding.buttonNo.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.setCancelable(false)
        if (alertDialog.window != null)
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))

        val currentItem: Pending = items[position]
        holder.binding.rejectButton.setOnClickListener {
            binding.apply {
                textMessage.text =
                    "Are you sure you want to delete this request??" +
                    "\nActions like this can be serious and irreversible."
                buttonYes.setOnClickListener {
                    alertDialog.dismiss()
                    db.collection("pbooking").document(currentItem.requestId).delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show()
                            notifyRemoved(position)
                        }
                        .addOnFailureListener {
                            Toast.makeText(context,
                                it.message.toString(),
                                Toast.LENGTH_SHORT).show()
                        }
                }
                alertDialog.show()
            }
        }

        holder.binding.acceptButton.setOnClickListener {
            binding.apply {
                builder.setView(root)
                textMessage.text = "Are you sure you want to accept this request?"
                buttonYes.setOnClickListener {
                    alertDialog.dismiss()
                    documentRef = db.collection("cbooking").document()
                    currentItem.bookingId = documentRef.id
                    documentRef.set(currentItem)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Accepted", Toast.LENGTH_SHORT).show()
                            notifyRemoved(position)
                            refreshPager()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                }
                alertDialog.show()
            }
        }
    }

    private fun refreshPager() {
        val intent = Intent(context, HomeActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        pendingIntent.send()
        activity.finish()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun notifyRemoved(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        activity.binding.pendingPager.adapter?.notifyDataSetChanged()
    }

}
