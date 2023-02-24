package com.example.book_venue

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.book_venue.databinding.VenueCardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class VenueAdapter() : RecyclerView.Adapter<VenueViewHolder>() {

    private var activity: ViewVenueActivity = ViewVenueActivity()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: FirebaseUser = auth.currentUser!!
    private lateinit var items: ArrayList<Venue>
    private lateinit var context: Context

    constructor(activity: ViewVenueActivity, items: ArrayList<Venue>, context: Context) : this() {
        this.context = context
        this.activity = activity
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val binding = VenueCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VenueViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun deleteData(position: Int) {

        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        val view: View = LayoutInflater.from(context)
            .inflate(R.layout.warning_dialog,
                (context as Activity).findViewById(R.id.layoutDialogContainer))

        builder.setView(view)
        (view.findViewById<View>(R.id.textTitle) as TextView).text =
            context.getString(R.string.delete_venue)
        (view.findViewById<View>(R.id.textMessage) as TextView).text =
            context.getString(R.string.delete_venue_text)
        (view.findViewById<View>(R.id.buttonYes) as Button).text =
            context.resources.getString(R.string.yes)
        (view.findViewById<View>(R.id.buttonNo) as Button).text =
            context.resources.getString(R.string.no)
        (view.findViewById<View>(R.id.imageIcon) as ImageView).setImageResource(R.drawable.warning)

        val alertDialog = builder.create()
        view.findViewById<View>(R.id.buttonYes).setOnClickListener {
            alertDialog.dismiss()
            val item: Venue = items[position]
            db.collection("venue").document(item.docId).delete()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        notifyRemoved(position)
                        //Toast.makeText(activity, "Data Deleted !!", Toast.LENGTH_SHORT).show()

                        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
                        val view: View = LayoutInflater.from(context)
                            .inflate(R.layout.success_dialog,
                                (context as Activity).findViewById<ConstraintLayout>(R.id.layoutDialogContainer))

                        builder.setView(view)
                        (view.findViewById<View>(R.id.textTitle) as TextView).text = context.resources.getString(R.string.success_title)
                        (view.findViewById<View>(R.id.textMessage) as TextView).text = context.getString(R.string.venue_delete_success)
                        (view.findViewById<View>(R.id.buttonAction) as Button).text = context.resources.getString(R.string.okay)
                        (view.findViewById<View>(R.id.imageIcon) as ImageView).setImageResource(R.drawable.done)

                        val alertDialog = builder.create()
                        view.findViewById<View>(R.id.buttonAction).setOnClickListener {
                            alertDialog.dismiss()
                            //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                        }
                        if (alertDialog.window != null) {
                            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
                        }
                        alertDialog.show()
                    }
                }
        }
        view.findViewById<View>(R.id.buttonNo).setOnClickListener { alertDialog.dismiss() }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun updateData(position: Int) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        val view: View = LayoutInflater.from(context)
            .inflate(R.layout.warning_dialog,
                (context as Activity).findViewById<ConstraintLayout>(R.id.layoutDialogContainer))

        builder.setView(view)
        (view.findViewById<TextView>(R.id.textTitle)).text =
            context.resources.getString(R.string.warning_title)
        (view.findViewById<TextView>(R.id.textMessage)).text =
            context.getString(R.string.venue_update_text)
        (view.findViewById<Button>(R.id.buttonYes)).text =
            context.resources.getString(R.string.yes)
        (view.findViewById<Button>(R.id.buttonNo)).text =
            context.resources.getString(R.string.no)
        (view.findViewById<ImageView>(R.id.imageIcon)).setImageResource(R.drawable.warning)

        val alertDialog = builder.create()
        view.findViewById<View>(R.id.buttonYes).setOnClickListener {
            alertDialog.dismiss()
            val item: Venue = items[position]
            val bundle = Bundle().apply {
                putString("docId", item.docId)
                putString("name", item.Name)
                putString("description", item.Description)
                putString("landmark", item.Landmark)
                putString("city", item.City)
                putString("state", item.State)
                putString("capacity", item.VenueCapacity)
                putString("available", item.Availability)
                putString("dealerPh", item.DealerContact)
                putString("rentHour", item.RentPerHour)
                putString("restRooms", item.RestRooms)
                putString("types", item.Types)
                putString("parking", item.Parking)
                putString("docId", item.docId)
            }

            val intent = Intent(activity, AddVenueActivity::class.java)
            intent.putExtras(bundle)
            activity.startActivity(intent)
            activity.finish()

        }
        view.findViewById<View>(R.id.buttonNo).setOnClickListener { alertDialog.dismiss() }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun notifyRemoved(position: Int){
        items.removeAt(position)
        //notifyDataSetChanged()
        notifyItemRemoved(position)
        //venueRecycler.adapter?.notifyItemRemoved(position)
        //activity.venueRecycler.adapter?.notifyDataSetChanged()
        activity.loadVenuesFromDb(user.uid)
        //activity.restart()
    }
}
