package com.digitalge.hiddencity.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.digitalge.hiddencity.Base_de_Dados.Locais
import com.digitalge.hiddencity.R
import com.digitalge.hiddencity.detalhes_local_marcador

class PontoCriadoAdapter(private var pontos: List<Locais>, private val context: Context) : RecyclerView.Adapter<PontoCriadoAdapter.PontoViewHolder>() {

    private var filteredPontos: List<Locais> = pontos

    class PontoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_place)
        val textViewNome: TextView = view.findViewById(R.id.text_place_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PontoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PontoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PontoViewHolder, position: Int) {
        val ponto = filteredPontos[position]  // Use filteredPontos here
        holder.textViewNome.text = ponto.Nome
        if (ponto.Imagens != null && ponto.Imagens.isNotEmpty()) {
            Glide.with(holder.imageView.context)
                .load(ponto.Imagens)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imageView)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, detalhes_local_marcador::class.java).apply {
                putExtra("localId", ponto.IdLocais)
            }
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = filteredPontos.size  // Use filteredPontos here

    fun updatePontos(newPontos: List<Locais>) {
        pontos = newPontos
        filteredPontos = newPontos
        notifyDataSetChanged()
    }

    fun filter(text: String) {
        filteredPontos = if (text.isEmpty()) {
            pontos
        } else {
            pontos.filter { it.Nome.contains(text, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}
