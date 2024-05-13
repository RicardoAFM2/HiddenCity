package com.digitalge.hiddencity.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitalge.hiddencity.Base_de_Dados.Favoritos_e_Locais
import com.digitalge.hiddencity.DetalhesLocalActivity
import com.digitalge.hiddencity.R
import com.digitalge.hiddencity.detalhes_local_marcador
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient

class PontoVisitadoAdapter(
    private var pontos: List<Favoritos_e_Locais>,
    private val placesClient: PlacesClient,
    private val context: Context
) : RecyclerView.Adapter<PontoVisitadoAdapter.PontoViewHolder>() {

    private var filteredPontos: List<Favoritos_e_Locais> = pontos.toList()


    class PontoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_place)
        val textViewNome: TextView = view.findViewById(R.id.text_place_name)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PontoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PontoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PontoViewHolder, position: Int) {
        val ponto = filteredPontos[position]
        holder.textViewNome.text = ponto.Nome
        loadPlacePhoto(ponto.IDlocal, holder.imageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(context,DetalhesLocalActivity::class.java).apply {
                putExtra("place_id", ponto.IDlocal)
            }
            context.startActivity(intent)
        }
    }
    private fun loadPlacePhoto(placeId: String, imageView: ImageView) {
        val placeFields = listOf(Place.Field.PHOTO_METADATAS)
        val placeRequest = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(placeRequest).addOnSuccessListener { response ->
            val place = response.place
            place.photoMetadatas?.firstOrNull()?.let { metadata ->
                fetchPhotoAndDisplay(metadata, imageView)
            }
        }.addOnFailureListener { exception ->
            // Handle errors here
            imageView.setImageResource(R.drawable.ic_launcher_background)
        }
    }

    private fun fetchPhotoAndDisplay(photoMetadata: PhotoMetadata, imageView: ImageView) {
        val photoRequest = FetchPhotoRequest.builder(photoMetadata).build()
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
            imageView.setImageBitmap(fetchPhotoResponse.bitmap)
        }.addOnFailureListener {
            // Handle errors here
            imageView.setImageResource(R.drawable.ic_launcher_background)
        }
    }


    override fun getItemCount(): Int = filteredPontos.size // Use filteredPontos count

    fun updatePontos(newPontos: List<Favoritos_e_Locais>) {
        pontos = newPontos
        filteredPontos = newPontos
        notifyDataSetChanged()
    }
    fun filter(query: String) {
        filteredPontos = if (query.isEmpty()) {
            pontos
        } else {
            pontos.filter { it.Nome.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}