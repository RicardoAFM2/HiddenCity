package com.digitalge.hiddencity.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitalge.hiddencity.DetalhesLocalActivity
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.digitalge.hiddencity.R


class MonumentAdapter(
    private val originalList: List<Place>,
    private val placesClient: PlacesClient,
    private val context: Context
) : RecyclerView.Adapter<MonumentAdapter.MonumentViewHolder>() {

    private var filteredList: List<Place> = originalList

    class MonumentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_place)
        val textViewNome: TextView = view.findViewById(R.id.text_place_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonumentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return MonumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonumentViewHolder, position: Int) {
        val monument = filteredList[position]
        holder.textViewNome.text = monument.name
        monument.photoMetadatas?.firstOrNull()?.let { photoMetadata ->
            fetchPhotoAndDisplay(photoMetadata, holder.imageView)
        }
        holder.itemView.setOnClickListener {
            // Criando Intent para iniciar DetalhesLocalActivity
            val intent = Intent(context, DetalhesLocalActivity::class.java)
            intent.putExtra("place_id", monument.id) // Passando o place_id como extra
            context.startActivity(intent)
        }
    }

    fun filter(text: String) {
        filteredList = if (text.isEmpty()) {
            originalList
        } else {
            originalList.filter { it.name.contains(text, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    private fun fetchPhotoAndDisplay(photoMetadata: PhotoMetadata, imageView: ImageView) {
        val photoRequest = FetchPhotoRequest.builder(photoMetadata).setMaxWidth(400).build()
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
            imageView.setImageBitmap(fetchPhotoResponse.bitmap)
        }.addOnFailureListener {
            imageView.setImageResource(R.drawable.ic_launcher_background) // Uma imagem padrão em caso de falha
        }
    }

    override fun getItemCount(): Int = filteredList.size
}
