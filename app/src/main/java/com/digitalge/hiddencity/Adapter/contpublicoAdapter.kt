package com.digitalge.hiddencity.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitalge.hiddencity.Base_de_Dados.Guia_e_Locais
import com.digitalge.hiddencity.R
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient


class contpublicoAdapter(
    private var guias: List<Guia_e_Locais>,
    private val context: Context,
    private val onItemClicked: (Guia_e_Locais) -> Unit
) : RecyclerView.Adapter<contpublicoAdapter.GuiaViewHolder>() {
    private val placesClient: PlacesClient = Places.createClient(context)
    private var originalGuias: List<Guia_e_Locais> = guias.toList()

    class GuiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNome: TextView = itemView.findViewById(R.id.text_place_name)
        val imageView: ImageView = itemView.findViewById(R.id.image_place)
        // Adicione outros campos conforme necessário
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuiaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return GuiaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GuiaViewHolder, position: Int) {
        val guia = guias[position]
        holder.textViewNome.text = guia.Nome

        holder.itemView.setOnClickListener {
            onItemClicked(guia)
        }
        // Configure outros campos aqui
        val placeRequest = FetchPlaceRequest.newInstance(guia.placeID, listOf(Place.Field.PHOTO_METADATAS))
        placesClient.fetchPlace(placeRequest).addOnSuccessListener { response ->
            val metadata = response.place.photoMetadatas?.firstOrNull()
            metadata?.let {
                val photoRequest = FetchPhotoRequest.builder(it)
                    .setMaxWidth(500)
                    .setMaxHeight(300)
                    .build()
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
                    holder.imageView.setImageBitmap(fetchPhotoResponse.bitmap)
                }.addOnFailureListener {
                    // Trate falhas
                    holder.imageView.setImageResource(R.drawable.ic_launcher_background)
                }
            }
        }
    }

    override fun getItemCount() = guias.size

    fun updateGuias(newGuias: List<Guia_e_Locais>) {
        guias = newGuias.toMutableList()
        originalGuias = newGuias.toList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        guias = if (query.isEmpty()) {
            originalGuias.toMutableList()
        } else {
            originalGuias.filter { it.Nome.contains(query, ignoreCase = true) }.toMutableList()
        }
        notifyDataSetChanged()
    }
}