package com.digitalge.hiddencity.Adapter

import android.content.Context
import com.digitalge.hiddencity.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.room.InvalidationTracker
import com.bumptech.glide.Glide
import com.digitalge.hiddencity.AppDatabase
import com.digitalge.hiddencity.Base_de_Dados.Comentarios


class CommentsAdapter(
    private var comments: MutableList<Comentarios>,
    private val lifecycleOwner: LifecycleOwner,
    private val loggedInUserId: Int, // ID do usuário logado
    private val onCommentClick: (Comentarios, Boolean) -> Unit) :
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
        val avatarUrlLiveData = getUserAvatarUrl(comentario.IdUtilizador, holder.itemView.context)
        avatarUrlLiveData.observe(lifecycleOwner, Observer { url ->
            Glide.with(holder.itemView.context)
                .load(url)
                .error(R.drawable.ic_launcher_background)  // error é um drawable que você define
                .into(holder.userAvatar)
        })

        holder.itemView.setOnClickListener {
            // Verifica se o ID do usuário do comentário é igual ao ID do usuário logado
            onCommentClick(comentario, comentario.IdUtilizador == loggedInUserId)
        }
    }

    override fun getItemCount() = comments.size

    fun updateData(newData: List<Comentarios>) {
        this.comments.clear()
        this.comments.addAll(newData)
        notifyDataSetChanged()  // Notifica que os dados mudaram
    }

    fun getUserAvatarUrl(userId: Int, context: Context): LiveData<String> {
        return AppDatabase.getDatabase(context).UtilizadorDao().getUserAvatarUrlById(userId)
    }
}

