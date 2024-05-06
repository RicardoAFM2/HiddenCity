package com.digitalge.hiddencity.Adapter

import com.digitalge.hiddencity.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitalge.hiddencity.Base_de_Dados.Comentarios


class CommentsAdapter(private var comments: MutableList<Comentarios>) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userAvatar: ImageView = itemView.findViewById(R.id.userAvatar)
        var userName: TextView = itemView.findViewById(R.id.commentUserName)
        var commentText: TextView = itemView.findViewById(R.id.commentText)
        var ratingText: TextView = itemView.findViewById(R.id.commentRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(v)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comentario = comments[position]
        holder.userName.text = comentario.Nome
        holder.commentText.text = comentario.Descricao
        holder.ratingText.text = "Avaliação: ${comentario.Avalicao}"
        // Adicione a configuração do avatar aqui, se necessário
    }

    override fun getItemCount() = comments.size

    fun updateData(newData: List<Comentarios>) {
        this.comments.clear()
        this.comments.addAll(newData)
        notifyDataSetChanged()  // Notifica que os dados mudaram
    }
}

