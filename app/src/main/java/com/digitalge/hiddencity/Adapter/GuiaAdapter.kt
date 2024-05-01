package com.digitalge.hiddencity.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.digitalge.hiddencity.Base_de_Dados.Guia
import com.digitalge.hiddencity.R

class GuiaAdapter(
    var guias: List<Guia>,
    private val onItemLongClicked: (Guia) -> Unit,
    private val onItemClicked: (Guia) -> Unit
) :  RecyclerView.Adapter<GuiaAdapter.GuiaViewHolder>() {

    var isAnimationRunning = false
    class GuiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val imageViewIcon: ImageView = itemView.findViewById(R.id.image_place)
        val textViewNome: TextView = itemView.findViewById(R.id.text_place_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuiaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return  GuiaViewHolder(itemView)
    }

    override fun getItemCount() = guias.size


    override fun onBindViewHolder(holder: GuiaViewHolder, position: Int) {
        val guia = guias[position]
        holder.textViewNome.text = guia.Nome

        val shakeAnimation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.shake).apply { repeatCount = Animation.INFINITE }

        shakeAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                isAnimationRunning = true // Defina como true quando a animação começar
            }

            override fun onAnimationEnd(animation: Animation) {
                isAnimationRunning = false // Defina como false quando a animação terminar
            }

            override fun onAnimationRepeat(animation: Animation) {
                // Se necessário, implemente comportamento para repetição
            }
        })

        holder.itemView.setOnLongClickListener {
            holder.itemView.startAnimation(shakeAnimation)
            onItemLongClicked(guia) // Isso chamará a função passada quando o Adapter foi criado
            true
        }

        holder.itemView.setOnClickListener {
            if (!isAnimationRunning) {
                onItemClicked(guia)
            }
        }

        Log.d("GuiaAdapter", "Loading image from URL: ${guia.url}")

        Glide.with(holder.itemView.context)
            .load(guia.url)
            .error(R.drawable.ic_launcher_background)
            .into(holder.imageViewIcon)


        Log.d("GuiaAdapter", "Binding complete for position $position")
    }


    fun clearItemAnimations(viewHolder: RecyclerView.ViewHolder?) {
        viewHolder?.itemView?.clearAnimation()
    }
}
