package org.karatomo.app.ui.adapter

import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.databinding.ItemSongBinding
import org.karatomo.app.manager.BookmarkManager
import org.karatomo.app.network.Song

class SongAdapter(private val songs: MutableList<Song>) :
    RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        holder.binding.tvTitle.text = song.title ?: "(제목 없음)"
        holder.binding.tvSinger.text = song.singer ?: "(가수 없음)"
        holder.binding.tvNo.text = "${song.brand} ${song.no}"

        holder.binding.btnBookmark.setOnClickListener {
            BookmarkManager.addSong(song)
            Toast.makeText(holder.itemView.context, "${song.title} 북마크 추가", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = songs.size

    fun updateData(newList: List<Song>) {
        songs.clear()
        songs.addAll(newList)
        notifyDataSetChanged()
    }
}
