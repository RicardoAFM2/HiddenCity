package com.digitalge.hiddencity.Adapter

import com.google.android.libraries.places.api.model.PhotoMetadata
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitalge.hiddencity.R
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.PlacesClient

class MonumentAdapter(
    private val monumentList: List<Place>,
    private val placesClient: PlacesClient
) : RecyclerView.Adapter<MonumentAdapter.MonumentViewHolder>() {


    class MonumentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewIcon)
        val textViewNome: TextView = view.findViewById(R.id.textViewNome)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonumentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_guia, parent, false)
        return MonumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonumentViewHolder, position: Int) {
        val monument = monumentList[position]
        holder.textViewNome.text = monument.name
        monument.photoMetadatas?.firstOrNull()?.let { photoMetadata ->
            fetchPhotoAndDisplay(placesClient, photoMetadata, holder.imageView)
        }
    }

    private fun fetchPhotoAndDisplay(placesClient: PlacesClient, photoMetadata: PhotoMetadata, imageView: ImageView) {
        val photoRequest = FetchPhotoRequest.builder(photoMetadata).setMaxWidth(400).build()

        placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
            val bitmap = fetchPhotoResponse.bitmap
            imageView.setImageBitmap(bitmap)
        }.addOnFailureListener {
            // Handle the failure to fetch the photo here
            imageView.setImageResource(R.drawable.ic_launcher_background) // Uma imagem padrão em caso de falha
        }
    }


    override fun getItemCount() = monumentList.size
}