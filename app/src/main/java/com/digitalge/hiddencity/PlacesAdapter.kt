package com.digitalge.hiddencity


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitalge.hiddencity.PlaceInfo


class PlacesAdapter(
    private var items: List<PlaceInfo>,
    private val onClick: (PlaceInfo) -> Unit
) : RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {

    class ViewHolder(view: View, private val onClick: (PlaceInfo) -> Unit) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(android.R.id.text1)

        fun bind(placeInfo: PlaceInfo) {
            textView.text = placeInfo.name
            itemView.setOnClickListener { onClick(placeInfo) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<PlaceInfo>) {
        items = newItems
        notifyDataSetChanged()
    }
}