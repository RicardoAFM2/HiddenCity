package com.digitalge.hiddencity.Imagens

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.digitalge.hiddencity.R

class ImageSliderAdapter(
    private val images: MutableList<Bitmap>,
    private val placeIds: MutableList<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {
    // Implementação de ViewHolder e métodos de bind

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        if (position < images.size && position < placeIds.size) {
            holder.imageView.setImageBitmap(images[position])
            holder.imageView.setOnClickListener {
                onClick(placeIds[position])
            }
        }
    }

    override fun getItemCount(): Int = images.size

}