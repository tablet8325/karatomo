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
        
        // 가끔 title 자체가 null로 파싱될 때를 대비합니다.
        val displayTitle = song.title ?: "(제목 없음)"
        val displaySinger = song.singer ?: "(가수 없음)"
        val displayBrand = song.brand ?: ""
        val displayNo = song.no ?: ""

        holder.binding.tvTitle.text = displayTitle
        holder.binding.tvSinger.text = displaySinger
        holder.binding.tvNo.text = "$displayBrand $displayNo"

        holder.binding.btnBookmark.setOnClickListener {
            BookmarkManager.addSong(song)
            Toast.makeText(holder.itemView.context, "$displayTitle 북마크 추가", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = songs.size

    fun updateData(newList: List<Song>) {
        songs.clear()
        songs.addAll(newList)
        notifyDataSetChanged()
    }
}
