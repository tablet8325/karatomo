package org.karatomo.app.ui.adapter

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager

class PlaylistTabAdapter(
    private val onItemClick: (String) -> Unit,
    private val onAddClick: () -> Unit
) : RecyclerView.Adapter<PlaylistTabAdapter.VH>() {

    private var names = BookmarkManager.getPlaylistNames()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val name = names[position]
        (holder.itemView as TextView).apply {
            text = name
            setPadding(40, 0, 40, 0)
            gravity = Gravity.CENTER
            setOnClickListener { onItemClick(name) }
        }
    }

    override fun getItemCount() = names.size

    override fun notifyDataSetChanged() {
        names = BookmarkManager.getPlaylistNames()
        super.notifyDataSetChanged()
    }

    class VH(v: View) : RecyclerView.ViewHolder(v)
}
