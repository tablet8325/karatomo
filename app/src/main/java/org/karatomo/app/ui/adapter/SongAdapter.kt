package org.karatomo.app.ui.adapter

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.R
import org.karatomo.app.network.Song

class SongAdapter(private var songs: List<Song>) : RecyclerView.Adapter<SongAdapter.VH>() {

    fun updateData(newSongs: List<Song>) {
        this.songs = newSongs
        notifyDataSetChanged()
    }

    // [에러 해결] submitList 대신 사용하거나 추가
    fun submitList(newSongs: List<Song>) = updateData(newSongs)

    // [에러 해결] addSong 누락 수정
    fun addSong(song: Song) {
        val newList = songs.toMutableList()
        newList.add(song)
        updateData(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val song = songs[position]
        holder.tvTitle.text = song.title
        holder.tvSinger.text = song.singer
        holder.tvNo.text = song.no
    }

    override fun getItemCount() = songs.size

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvSongTitle)
        val tvSinger: TextView = v.findViewById(R.id.tvSongSinger)
        val tvNo: TextView = v.findViewById(R.id.tvSongNo)
    }
}
