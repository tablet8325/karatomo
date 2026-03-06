package org.karatomo.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.R

class PlaylistTabAdapter(
    private val onItemClick: (String) -> Unit,
    private val onAddClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = mutableListOf<String>()

    fun submitList(newList: List<String>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_playlist_tab, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val name = items[position]
        (holder as PlaylistViewHolder).bind(name)
    }

    inner class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvName: TextView = view.findViewById(R.id.tvPlaylistName)
        fun bind(name: String) {
            tvName.text = name
            itemView.setOnClickListener { onItemClick(name) }
        }
    }
}
