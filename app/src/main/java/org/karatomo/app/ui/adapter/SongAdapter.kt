package org.karatomo.app.ui.adapter

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.R
import org.karatomo.app.network.Song

class SongAdapter(private var songs: List<Song>) : RecyclerView.Adapter<SongAdapter.VH>() {

    // 데이터를 갱신하는 기본 함수
    fun updateData(newSongs: List<Song>) {
        this.songs = newSongs
        notifyDataSetChanged()
    }

    // [에러 해결] BookmarkFragment에서 찾는 함수명 추가
    fun submitList(newSongs: List<Song>) {
        updateData(newSongs)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val song = songs[position]
        holder.tvTitle.text = song.title ?: "알 수 없는 제목"
        holder.tvSinger.text = song.singer ?: "알 수 없는 가수"
        holder.tvNo.text = song.no ?: "-"
    }

    override fun getItemCount() = songs.size

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvSongTitle)
        val tvSinger: TextView = v.findViewById(R.id.tvSongSinger)
        val tvNo: TextView = v.findViewById(R.id.tvSongNo)
    }
}
