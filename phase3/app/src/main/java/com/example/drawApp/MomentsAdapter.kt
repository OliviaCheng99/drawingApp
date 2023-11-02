package com.example.drawApp

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView

class MomentsAdapter(private val context: Context, private var images: List<ByteArray>) :
    RecyclerView.Adapter<MomentsAdapter.MomentsViewHolder>() {

    inner class MomentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageItem)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MomentsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.moments_item, parent, false)
        return MomentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MomentsViewHolder, position: Int) {
        // Access the ByteArray at the specified position
        val image = images[position]

        // Bind the image ByteArray to the ImageView
        holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.size))

        // Set click listener to delete the image
        holder.deleteButton.setOnClickListener {
            // Call a function to delete the image by its unique ID (assuming you have the ID)
            // You can pass the ID to the function and handle the deletion in the ViewModel or repository
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun updateImages(newImages: List<ByteArray>) {
        images = newImages
        notifyDataSetChanged()
    }
}
