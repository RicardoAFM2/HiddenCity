package com.digitalge.hiddencity.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.digitalge.hiddencity.Base_de_Dados.Guia
import com.digitalge.hiddencity.Base_de_Dados.Guia_e_Locais
import com.digitalge.hiddencity.R

class PublicoAdapter(private var guias: List<Guia>, private val onItemClicked: (Guia) -> Unit) : RecyclerView.Adapter<PublicoAdapter.GuiaViewHolder>() {
    class GuiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNome: TextView = itemView.findViewById(R.id.text_place_name)
        val imageView: ImageView = itemView.findViewById(R.id.image_place)
        fun bind(guia: Guia, onItemClicked: (Guia) -> Unit) {
            textViewNome.text = guia.Nome
            itemView.setOnClickListener { onItemClicked(guia) }

            // Você pode optar por carregar a imagem aqui também, dependendo da sua estrutura
            Glide.with(itemView.context)
                .load(guia.url)
                .placeholder(R.drawable.ic_launcher_background)  // Placeholder enquanto a imagem não é carregada
                .error(R.drawable.ic_launcher_background)  // Imagem de erro
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuiaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return GuiaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GuiaViewHolder, position: Int) {
        val guia = guias[position]  // Corretamente acessando o item guia baseado na posição
        holder.bind(guia, onItemClicked)  // O método bind já deve configurar o
    }

    override fun getItemCount() = guias.size

    // Atualizar a lista de guias
    fun updateGuias(newGuias: List<Guia>) { // Corrigindo o tipo de Lista aqui
        guias = newGuias
        notifyDataSetChanged()
    }
}