package com.digitalge.hiddencity.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitalge.hiddencity.Base_de_Dados.Favoritos
import com.digitalge.hiddencity.Base_de_Dados.Favoritos_e_Locais
import com.digitalge.hiddencity.R
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient

class FavoritospublicoAdapter(
    private var favoritos: List<Favoritos>,
    private val placesClient: PlacesClient,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<FavoritospublicoAdapter.FavoritosViewHolder>() {

    private var filteredFavoritos: List<Favoritos> = favoritos

    class FavoritosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.text_place_name)
        val imageView: ImageView = view.findViewById(R.id.image_place)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return FavoritosViewHolder(view).apply {
            itemView.setOnClickListener {
                filteredFavoritos[adapterPosition].PlaceID?.let(onItemClicked)
            }
        }
    }

    override fun onBindViewHolder(holder: FavoritosViewHolder, position: Int) {
        val favorito = filteredFavoritos[position]
        holder.nameTextView.text = favorito.Nome
        favorito.PlaceID?.let { placeId ->
            loadPlaceDetails(placeId, holder.imageView)
        }
    }

    private fun loadPlaceDetails(placeId: String, imageView: ImageView) {
        val placeFields = listOf(Place.Field.PHOTO_METADATAS)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            place.photoMetadatas?.firstOrNull()?.let { photoMetadata ->
                fetchPhotoAndDisplay(photoMetadata, imageView)
            }
        }.addOnFailureListener {
            imageView.setImageResource(R.drawable.ic_launcher_background)  // In case of error loading the image
        }
    }

    private fun fetchPhotoAndDisplay(photoMetadata: PhotoMetadata, imageView: ImageView) {
        val photoRequest = FetchPhotoRequest.builder(photoMetadata).build()
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
            imageView.setImageBitmap(fetchPhotoResponse.bitmap)
        }.addOnFailureListener {
            imageView.setImageResource(R.drawable.ic_launcher_background)  // In case of error loading the image
        }
    }

    override fun getItemCount(): Int = filteredFavoritos.size

    fun updateFavoritos(newFavoritos: List<Favoritos>) {
        favoritos = newFavoritos
        filteredFavoritos = newFavoritos
        notifyDataSetChanged()
    }

    fun filter(text: String) {
        filteredFavoritos = if (text.isEmpty()) {
            favoritos
        } else {
            favoritos.filter { it.Nome.contains(text, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}

