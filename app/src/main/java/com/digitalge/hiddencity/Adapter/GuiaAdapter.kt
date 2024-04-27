package com.digitalge.hiddencity.Adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.digitalge.hiddencity.Base_de_Dados.Guia
import com.digitalge.hiddencity.R


class GuiaAdapter(var guias: List<Guia>, private val onItemLongClicked: (Guia) -> Unit = {}) : RecyclerView.Adapter<GuiaAdapter.GuiaViewHolder>(){

    class GuiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val imageViewIcon: ImageView = itemView.findViewById(R.id.imageViewIcon)
        val textViewNome: TextView = itemView.findViewById(R.id.textViewNome)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuiaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_guia, parent, false)
        return  GuiaViewHolder(itemView)
    }

    override fun getItemCount() = guias.size


    override fun onBindViewHolder(holder: GuiaViewHolder, position: Int) {
      val guia = guias[position]
        holder.textViewNome.text = guia.Nome

        holder.itemView.setOnLongClickListener {
            Log.d("GuiaAdapter", "Item long clicked: ${guia.Nome}")
            onItemLongClicked(guia)
            true
        }

        Log.d("GuiaAdapter", "Loading image from URL: ${guia.url}")

        Glide.with(holder.itemView.context)
            .load(guia.url)
            .apply(RequestOptions().error(R.drawable.ic_launcher_background))
            .into(holder.imageViewIcon)

        Log.d("GuiaAdapter", "Binding complete for position $position")
    }
}
