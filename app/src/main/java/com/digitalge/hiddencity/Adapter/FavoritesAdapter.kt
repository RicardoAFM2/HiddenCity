package com.digitalge.hiddencity.Adapter


import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.digitalge.hiddencity.Base_de_Dados.Favoritos
import com.digitalge.hiddencity.Base_de_Dados.Guia
import com.digitalge.hiddencity.DetalhesLocalActivity
import com.digitalge.hiddencity.R
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.Locale


class FavoritesAdapter(
    private val placesClient: PlacesClient,
    private val favorites: MutableList<Favoritos>,
    private val context: Context,
    private val onDeleteClick: (Favoritos) -> Unit,
    private val onLongItemClick: (Boolean, Int?) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {


    fun removeItemAtPosition(position: Int) {
        if (position >= 0 && position < favorites.size) {
            val item = favorites.removeAt(position)
            notifyItemRemoved(position)
            onDeleteClick(item)
        }
    }

    var isEditing = false
    var editingPosition: Int? = null


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = view.findViewById(R.id.text_place_name)
        private val imageView: ImageView = view.findViewById(R.id.image_place)
        private val shakeAnimation = AnimationUtils.loadAnimation(context, R.anim.shake).apply {
            repeatCount = Animation.INFINITE
        }

        init {
            itemView.setOnLongClickListener {
                if (editingPosition == adapterPosition) {
                    stopEditing()
                } else {
                    startEditing(adapterPosition)
                }
                true
            }

            itemView.setOnClickListener {
                if (!isEditing) {
                    // Normal click action, if not in editing mode
                    val intent = Intent(context, DetalhesLocalActivity::class.java).apply {
                        putExtra("place_id", favorites[adapterPosition].PlaceID)
                    }
                    context.startActivity(intent)
                } else {
                    // If editing, click should stop editing mode
                    stopEditing()
                }
            }
        }

        private fun startEditing(position: Int) {
            isEditing = true
            editingPosition = position
            onLongItemClick(true, position)
            notifyDataSetChanged()
        }

        private fun stopEditing() {
            isEditing = false
            editingPosition = null
            onLongItemClick(false, null)
            notifyDataSetChanged()
        }

        fun bind(favorite: Favoritos) {
            nameTextView.text = favorite.Nome
            fetchPlacePhotoAndDisplay(favorite.PlaceID, imageView)

            if (isEditing && editingPosition == adapterPosition) {
                itemView.startAnimation(shakeAnimation)
            } else {
                itemView.clearAnimation()
            }
        }
    }


    private fun fetchPlacePhotoAndDisplay(placeId: String, imageView: ImageView) {
        val placeFields = listOf(Place.Field.PHOTO_METADATAS)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            place.photoMetadatas?.firstOrNull()?.let { photoMetadata ->
                val photoRequest = FetchPhotoRequest.builder(photoMetadata).build()
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
                    imageView.setImageBitmap(fetchPhotoResponse.bitmap)  // Atualiza a ImageView com a imagem
                }.addOnFailureListener { e ->
                    Log.e("API Error", "Erro ao buscar foto do local: ${e.message}")
                    // Definir uma imagem padrão caso a busca falhe
                    imageView.setImageResource(R.drawable.ic_launcher_background)
                }
            }
        }.addOnFailureListener { e ->
            Log.e("API Error", "Erro ao buscar detalhes do local: ${e.message}")
            // Definir uma imagem padrão caso a busca falhe
            imageView.setImageResource(R.drawable.ic_launcher_background)
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(favorites[position])
    }
    override fun getItemCount() = favorites.size

    fun updateFavorites(newFavorites: List<Favoritos>) {
        favorites.clear()
        favorites.addAll(newFavorites)
        notifyDataSetChanged()
    }

    fun removeFavorite(favorite: Favoritos) {
        val index = favorites.indexOf(favorite)
        if (index != -1) {
            favorites.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    private var originalGuias: MutableList<Favoritos> = mutableListOf()


    fun filter(text: String) {
        val filteredList = if (text.isEmpty()) {
            originalGuias.toList()  // Garantir que está utilizando uma cópia da lista original
        } else {
            originalGuias.filter { it.Nome.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) }
        }
        favorites.clear()
        favorites.addAll(filteredList)
        notifyDataSetChanged()
    }

    fun setOriginalGuias(favorites: List<Favoritos>) {
        originalGuias.clear()
        originalGuias.addAll(favorites)
        notifyDataSetChanged()
    }
}


