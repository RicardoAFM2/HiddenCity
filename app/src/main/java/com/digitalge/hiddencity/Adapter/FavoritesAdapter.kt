package com.digitalge.hiddencity.Adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.digitalge.hiddencity.Base_de_Dados.Favoritos
import com.digitalge.hiddencity.DetalhesLocalActivity
import com.digitalge.hiddencity.R


class FavoritesAdapter(
    val favorites: MutableList<Favoritos>,
    private val context: Context,
    private val onDeleteClick: (Favoritos) -> Unit,
    private val onLongItemClick: (Boolean, Int?) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

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
            Glide.with(itemView.context).load(favorite.URL).into(imageView)
            if (isEditing && editingPosition == adapterPosition) {
                itemView.startAnimation(shakeAnimation)
            } else {
                itemView.clearAnimation()
            }
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

    fun addItems(newFavorites: List<Favoritos>) {
        favorites.clear()
        favorites.addAll(newFavorites)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        favorites.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateData(newFavorites: List<Favoritos>) {
        favorites.clear()
        favorites.addAll(newFavorites)
        notifyDataSetChanged()
    }
}
