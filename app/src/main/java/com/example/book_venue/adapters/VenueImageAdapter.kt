package com.example.book_venue.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.book_venue.data.Venue
import com.example.book_venue.databinding.VenueImageBinding

class VenueImageAdapter(private val images: ArrayList<Venue>)
    : RecyclerView.Adapter<VenueImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = VenueImageBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = VenueImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        Glide.with(holder.itemView.context).load(image.images).into(holder.binding.imageView)
    }

    override fun getItemCount(): Int = images.size

}
