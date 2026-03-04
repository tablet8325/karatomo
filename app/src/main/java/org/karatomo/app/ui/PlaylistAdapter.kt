package org.karatomo.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.databinding.ItemPlaylistBinding
import org.karatomo.app.model.Playlist

class PlaylistAdapter(
    private val playlists: List<Playlist>
) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPlaylistBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.binding.tvName.text = "${playlist.name} (${playlist.songs.size})"
    }

    override fun getItemCount() = playlists.size
}
