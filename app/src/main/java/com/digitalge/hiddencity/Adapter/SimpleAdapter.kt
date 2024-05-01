package com.digitalge.hiddencity.Adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.digitalge.hiddencity.R
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient



data class PlaceItem(
    val name: String,
    val placeId: String,
    val imageUrl: String = ""
)


class SimpleAdapter(
    private val items: MutableList<PlaceItem>,
    private val onItemClick: (PlaceItem) -> Unit,
    private val onItemLongClick: (PlaceItem) -> Unit,  // Adicione um handler para long press
    private val placesClient: PlacesClient
) : RecyclerView.Adapter<SimpleAdapter.ViewHolder>() {

    var currentPosition: Int = RecyclerView.NO_POSITION // Aqui adicionamos a posição atual


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.image_place)
        private val textView: TextView = view.findViewById(R.id.text_place_name)


        fun bind(item: PlaceItem, onClick: (PlaceItem) -> Unit, onLongClick: (PlaceItem) -> Unit, placesClient: PlacesClient) {
            textView.text = item.name
            itemView.setOnClickListener { onClick(item) }
            itemView.setOnLongClickListener {
                itemView.startAnimation(AnimationUtils.loadAnimation(itemView.context, R.anim.shake).apply {
                    repeatCount = Animation.INFINITE
                })
                onLongClick(item)
                true
            }

            // Fetch photo using placeId if available
            if (item.placeId.isNotEmpty()) {
                val fields = listOf(Place.Field.PHOTO_METADATAS)
                val placeRequest = FetchPlaceRequest.newInstance(item.placeId, fields)

                placesClient.fetchPlace(placeRequest).addOnSuccessListener { response ->
                    val place = response.place
                    place.photoMetadatas?.firstOrNull()?.let { photoMetadata ->
                        val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                            .setMaxWidth(500)  // Specify the max width to fetch
                            .setMaxHeight(500)
                            .build()

                        placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
                            imageView.setImageBitmap(fetchPhotoResponse.bitmap)
                        }.addOnFailureListener {
                            imageView.setImageResource(R.drawable.ic_launcher_background) // Fallback image
                        }
                    } ?: run {
                        imageView.setImageResource(R.drawable.ic_launcher_background) // Fallback image if no photo metadata
                    }
                }.addOnFailureListener {
                    imageView.setImageResource(R.drawable.ic_launcher_background) // Fallback image on fetch failure
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == currentPosition) {
            holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.shake).apply {
                repeatCount = Animation.INFINITE
            })
        } else {
            holder.itemView.clearAnimation()
        }
        holder.bind(items[position], onItemClick, onItemLongClick, placesClient)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<PlaceItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun clearAnimations() {
        currentPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }
}

