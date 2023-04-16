package com.example.book_venue.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.book_venue.*
import com.example.book_venue.databinding.ErrorDialogBinding
import com.example.book_venue.databinding.SuccessDialogBinding
import com.example.book_venue.databinding.VenueCardBinding
import com.example.book_venue.databinding.WarningDialogBinding
import com.example.book_venue.data.Venue
import com.example.book_venue.viewModel.VenueViewHolder
import com.example.book_venue.ui.AddVenueActivity
import com.example.book_venue.ui.PreviewVenueImageActivity
import com.example.book_venue.ui.ViewVenueActivity
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("NotifyDataSetChanged")
class VenueAdapter() : RecyclerView.Adapter<VenueViewHolder>() {

    private var activity: ViewVenueActivity = ViewVenueActivity()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var items: ArrayList<Venue>
    private lateinit var context: Context

    constructor(context: Context, activity: ViewVenueActivity, items: ArrayList<Venue>) : this() {
        this.context = context
        this.activity = activity
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val binding = VenueCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VenueViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        val currentVenue = items[position]
        holder.bind(currentVenue)

        holder.binding.venueCard.setOnClickListener {
            //context.startActivity(Intent(context,PreviewVenueImageActivity::class.java))
            val dialogView: View = LayoutInflater.from(context).inflate(R.layout.dialog_image, null)

            val imageUrl = if (currentVenue.url0 != "") currentVenue.url0 else R.drawable.logo
            Glide.with(context).load(imageUrl).into(dialogView.findViewById(R.id.previewedImage))

            val imageDialog = AlertDialog.Builder(context)
            imageDialog.setView(dialogView).show()
        }

        holder.binding.venueHeadImage.setOnClickListener {
            val dialogView: View = LayoutInflater.from(context).inflate(R.layout.dialog_image, null)

            val imageUrl = if (currentVenue.url0 != "") currentVenue.url0 else R.drawable.logo
            Glide.with(context).load(imageUrl).into(dialogView.findViewById(R.id.previewedImage))

            val imageDialog = AlertDialog.Builder(context)
            imageDialog.setView(dialogView).show()
        }
    }

    override fun getItemCount(): Int = items.size

    fun deleteData(position: Int) {

        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        val wbinding: WarningDialogBinding = WarningDialogBinding.bind(LayoutInflater.from(context)
            .inflate(R.layout.warning_dialog,
                (context as Activity).findViewById(R.id.layoutDialogContainer)))

        builder.setView(wbinding.root)
        wbinding.textTitle.text = context.getString(R.string.delete_venue)
        wbinding.textMessage.text = context.getString(R.string.delete_venue_text)
        wbinding.buttonYes.text = context.resources.getString(R.string.yes)
        wbinding.buttonNo.text = context.resources.getString(R.string.no)
        wbinding.imageIcon.setImageResource(R.drawable.warning)

        val alertDialog = builder.create()
        wbinding.buttonYes.setOnClickListener {
            alertDialog.dismiss()
            val item: Venue = items[position]
            db.collection("venue").document(item.docId).delete()
                .addOnSuccessListener {
                    showSuccessDialog("Venue is deleted successfully")
                    notifyRemoved(position)
                }
                .addOnFailureListener { showErrorDialog(it.message.toString()) }
        }
        wbinding.buttonNo.setOnClickListener {
            activity.binding.venueRecycler.adapter?.notifyDataSetChanged()
            alertDialog.dismiss()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun setFilterList(filteredList: ArrayList<Venue>) {
        this.items = filteredList
        notifyDataSetChanged()
    }

    fun updateData(position: Int) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        val wbinding: WarningDialogBinding = WarningDialogBinding.bind(LayoutInflater.from(context)
            .inflate(R.layout.warning_dialog,
                (context as Activity).findViewById(R.id.layoutDialogContainer)))

        builder.setView(wbinding.root)
        wbinding.textTitle.text = context.resources.getString(R.string.warning_title)
        wbinding.textMessage.text = context.getString(R.string.venue_update_text)
        wbinding.buttonYes.text = context.resources.getString(R.string.yes)
        wbinding.buttonNo.text = context.resources.getString(R.string.no)
        wbinding.imageIcon.setImageResource(R.drawable.warning)

        val alertDialog = builder.create()
        wbinding.buttonYes.setOnClickListener {
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
                putString("url0", item.url0)
                putString("notes", item.Notes)
            }

            val intent = Intent(activity, AddVenueActivity::class.java)
            intent.putExtras(bundle)
            activity.startActivity(intent)
            activity.finish()

        }
        wbinding.buttonNo.setOnClickListener {
            activity.binding.venueRecycler.adapter?.notifyDataSetChanged()
            alertDialog.dismiss()
        }

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun notifyRemoved(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        activity.binding.venueRecycler.adapter?.notifyDataSetChanged()
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

    private fun showErrorDialog(message: String) {
        val builder = android.app.AlertDialog.Builder(context, R.style.AlertDialogTheme)
        val ebinding: ErrorDialogBinding =
            ErrorDialogBinding.bind(LayoutInflater.from(context)
                .inflate(R.layout.error_dialog,
                    (context as Activity).findViewById<ConstraintLayout>(R.id.layoutDialogContainer)))

        builder.setView(ebinding.root)
        ebinding.textTitle.text = context.resources.getString(R.string.network_error_title)
        ebinding.textMessage.text = message
        ebinding.buttonAction.text = context.resources.getString(R.string.okay)
        ebinding.imageIcon.setImageResource(R.drawable.error)

        val alertDialog = builder.create()
        ebinding.buttonAction.setOnClickListener {
            alertDialog.dismiss()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}
